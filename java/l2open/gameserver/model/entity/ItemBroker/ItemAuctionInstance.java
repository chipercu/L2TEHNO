package l2open.gameserver.model.entity.ItemBroker;

import gnu.trove.TIntObjectHashMap;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.ItemAuctionManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Rnd;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItemAuctionInstance
{
	static final Logger _log = Logger.getLogger(ItemAuctionInstance.class.getName());
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd.MM.yy");

	private static final long START_TIME_SPACE = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
	private static final long FINISH_TIME_SPACE = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
	private final int _instanceId;
	private final AtomicInteger _auctionIds;
	private final TIntObjectHashMap<ItemAuction> _auctions;
	private final GArray<AuctionItem> _items;
	private final AuctionDateGenerator _dateGenerator;
	private ItemAuction _currentAuction;
	private ItemAuction _nextAuction;
	private ScheduledFuture<?> _stateTask;

	public ItemAuctionInstance(int instanceId, AtomicInteger auctionIds, Node node) throws Exception
	{
		_instanceId = instanceId;
		_auctionIds = auctionIds;
		_auctions = new TIntObjectHashMap<ItemAuction>();
		_items = new GArray<AuctionItem>();

		NamedNodeMap nanode = node.getAttributes();
		StatsSet generatorConfig = new StatsSet();
		for(int i = nanode.getLength(); i-- > 0;)
		{
			Node n = nanode.item(i);
			if(n != null)
			{
				generatorConfig.set(n.getNodeName(), n.getNodeValue());
			}
		}
		_dateGenerator = new AuctionDateGenerator(generatorConfig);

		for(Node na = node.getFirstChild(); na != null; na = na.getNextSibling())
		{
			try
			{
				if("item".equalsIgnoreCase(na.getNodeName()))
				{
					NamedNodeMap naa = na.getAttributes();
					int auctionItemId = Integer.parseInt(naa.getNamedItem("auctionItemId").getNodeValue());
					int auctionLenght = Integer.parseInt(naa.getNamedItem("auctionLenght").getNodeValue());
					long auctionInitBid = Integer.parseInt(naa.getNamedItem("auctionInitBid").getNodeValue());

					int itemId = Integer.parseInt(naa.getNamedItem("itemId").getNodeValue());
					int itemCount = Integer.parseInt(naa.getNamedItem("itemCount").getNodeValue());

					if(auctionLenght < 1)
					{
						throw new IllegalArgumentException("auctionLenght < 1 for instanceId: " + _instanceId + ", itemId " + itemId);
					}
					StatsSet itemExtra = new StatsSet();
					AuctionItem item = new AuctionItem(auctionItemId, auctionLenght, auctionInitBid, itemId, itemCount, itemExtra);

					if(!item.checkItemExists())
					{
						throw new IllegalArgumentException("Item with id " + itemId + " not found");
					}
					for(AuctionItem tmp : _items)
					{
						if(tmp.getAuctionItemId() == auctionItemId)
						{
							throw new IllegalArgumentException("Dublicated auction item id " + auctionItemId);
						}
					}
					_items.add(item);
					NamedNodeMap nab;
					int i;
					for(Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling())
					{
						if(!"extra".equalsIgnoreCase(nb.getNodeName()))
							continue;
						nab = nb.getAttributes();
						for(i = nab.getLength(); i-- > 0;)
						{
							Node n = nab.item(i);
							if(n != null)
							{
								itemExtra.set(n.getNodeName(), n.getNodeValue());
							}
						}
					}
				}
			}
			catch (IllegalArgumentException e)
			{
				_log.log(Level.WARNING, "ItemAuctionInstance: Failed loading auction item", e);
			}
		}

		if (_items.isEmpty())
		{
			throw new IllegalArgumentException("No items defined");
		}
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT auctionId FROM item_auction WHERE instanceId=?");
			statement.setInt(1, _instanceId);
			rset = statement.executeQuery();

			while(rset.next())
			{
				int auctionId = rset.getInt(1);
				try
				{
					ItemAuction auction = loadAuction(auctionId);
					if(auction != null)
					{
						_auctions.put(auctionId, auction);
					}
					else
					{
						ItemAuctionManager.deleteAuction(auctionId);
					}
				}
				catch (SQLException e)
				{
					_log.log(Level.WARNING, "ItemAuctionInstance: Failed loading auction: " + auctionId, e);
				}
			}
		}
		catch (SQLException e)
		{
			_log.log(Level.SEVERE, "ItemAuctionInstance: Failed loading auctions.", e);
			return;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		_log.log(Level.INFO, "ItemAuctionInstance: Loaded " + _items.size() + " item(s) and registered " + _auctions.size() + " auction(s) for instance " + _instanceId + ".");
		checkAndSetCurrentAndNextAuction();
	}

	public final ItemAuction getCurrentAuction()
	{
		return _currentAuction;
	}

	public final ItemAuction getNextAuction()
	{
		return _nextAuction;
	}

	public final void shutdown()
	{
		ScheduledFuture stateTask = _stateTask;
		if(stateTask != null)
			stateTask.cancel(false);
	}

	private AuctionItem getAuctionItem(int auctionItemId)
	{
		for (int i = _items.size(); i-- > 0; )
		{
			AuctionItem item = _items.get(i);
			if(item.getAuctionItemId() == auctionItemId)
				return item;
		}
		return null;
	}

	final void checkAndSetCurrentAndNextAuction()
	{
		ItemAuction[] auctions = _auctions.getValues(new ItemAuction[_auctions.size()]);

		ItemAuction currentAuction = null;
		ItemAuction nextAuction = null;

		switch (auctions.length)
		{
			case 0:
			{
				nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
				break;
			}
			
			case 1:
			{
				switch (auctions[0].getAuctionState())
				{
					case CREATED:
					{
						if (auctions[0].getStartingTime() < (System.currentTimeMillis() + START_TIME_SPACE))
						{
							currentAuction = auctions[0];
							nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
						}
						else
						{
							nextAuction = auctions[0];
						}
						break;
					}
					
					case STARTED:
					{
						currentAuction = auctions[0];
						nextAuction = createAuction(Math.max(currentAuction.getEndingTime() + FINISH_TIME_SPACE, System.currentTimeMillis() + START_TIME_SPACE));
						break;
					}
					
					case FINISHED:
					{
						currentAuction = auctions[0];
						nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
						break;
					}
					
					default:
						throw new IllegalArgumentException();
				}
				break;
			}
			default:
			{
				Arrays.sort(auctions, new Comparator<ItemAuction>()
				{
					@Override
					public final int compare(ItemAuction o1, ItemAuction o2)
					{
						return Long.valueOf(o2.getStartingTime()).compareTo(o1.getStartingTime());
					}
				});
				long currentTime = System.currentTimeMillis();

				for(int i = 0; i < auctions.length; i++)
				{
					ItemAuction auction = auctions[i];
					if(auction.getAuctionState() == ItemAuctionState.STARTED)
					{
						currentAuction = auction;
						break;
					}
					else if (auction.getStartingTime() <= currentTime)
					{
						currentAuction = auction;
						break; // only first
					}
				}

				for(int i = 0; i < auctions.length; i++)
				{
					ItemAuction auction = auctions[i];
					if (auction.getStartingTime() > currentTime && currentAuction != auction)
					{
						nextAuction = auction;
						break;
					}
				}

				if(nextAuction == null)
				{
					nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
				}
			}
		}

		_auctions.put(nextAuction.getAuctionId(), nextAuction);

		_currentAuction = currentAuction;
		_nextAuction = nextAuction;

		if(currentAuction != null && currentAuction.getAuctionState() != ItemAuctionState.FINISHED)
		{
			if (currentAuction.getAuctionState() == ItemAuctionState.STARTED)
				setStateTask(ThreadPoolManager.getInstance().schedule(new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getEndingTime() - System.currentTimeMillis(), 0)));
			else
				setStateTask(ThreadPoolManager.getInstance().schedule(new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getStartingTime() - System.currentTimeMillis(), 0)));
			_log.log(Level.INFO, "ItemAuctionInstance: Schedule current auction " + currentAuction.getAuctionId() + " for instance " + _instanceId);
		}
		else
		{
			setStateTask(ThreadPoolManager.getInstance().schedule(new ScheduleAuctionTask(nextAuction), Math.max(nextAuction.getStartingTime() - System.currentTimeMillis(), 0)));
			_log.log(Level.INFO, "ItemAuctionInstance: Schedule next auction " + nextAuction.getAuctionId() + " on " + DATE_FORMAT.format(new Date(nextAuction.getStartingTime())) + " for instance " + _instanceId);
		}
	}

	public final ItemAuction getAuction(int auctionId)
	{
		return _auctions.get(auctionId);
	}

	public final ItemAuction[] getAuctionsByBidder(int bidderObjId)
	{
		ItemAuction[] auctions = getAuctions();
		GArray<ItemAuction> stack = new GArray<ItemAuction>(auctions.length);
		for (ItemAuction auction : getAuctions())
		{
			if(auction.getAuctionState() == ItemAuctionState.CREATED)
				continue;
			ItemAuctionBid bid = auction.getBidFor(bidderObjId);
			if(bid != null)
			{
				stack.add(auction);
			}
		}
		return stack.toArray(new ItemAuction[stack.size()]);
	}

	public final ItemAuction[] getAuctions()
	{
		ItemAuction[] auctions;
		synchronized(_auctions)
		{
			auctions = _auctions.getValues(new ItemAuction[_auctions.size()]);
		}
		return auctions;
	}

	final void onAuctionFinished(ItemAuction auction)
	{
		auction.broadcastToAllBidders(new SystemMessage(2173).addNumber(auction.getAuctionId()));

		ItemAuctionBid bid = auction.getHighestBid();
		if(bid != null)
		{
			L2ItemInstance item = auction.createNewItemInstance();
			L2Player player = bid.getPlayer();
			if(player != null)
			{
				player.getWarehouse().addItem(item, "ItemAuction");
				player.sendPacket(new SystemMessage(2131));

				_log.log(Level.INFO, "ItemAuctionInstance: Auction " + auction.getAuctionId() + " has finished. Highest bid by " + player.getName() + " for instance " + _instanceId);
			}
			else
			{
				item.setOwnerId(bid.getPlayerObjId());
				item.setLocation(L2ItemInstance.ItemLocation.WAREHOUSE);
				item.updateDatabase();
				L2World.removeObject(item);

				_log.log(Level.INFO, "ItemAuctionInstance: Auction " + auction.getAuctionId() + " has finished.");
			}
		}
		else
		{
			_log.log(Level.INFO, "ItemAuctionInstance: Auction " + auction.getAuctionId() + " has finished. There have not been any bid for instance " + _instanceId);
		}
	}

	final void setStateTask(ScheduledFuture<?> future)
	{
		ScheduledFuture stateTask = _stateTask;
		if (stateTask != null)
		{
			stateTask.cancel(false);
		}
		_stateTask = future;
	}

	private ItemAuction createAuction(long after)
	{
		AuctionItem auctionItem;
		if(ConfigValue.AuctionSetAucItem[_instanceId-32320] > 0)
		{
			auctionItem = getAuctionItem(ConfigValue.AuctionSetAucItem[_instanceId-32320]);
			/*getAuction()
			if(auction.getAuctionState() == ItemAuctionState.FINISHED)
				_items.get(Rnd.get(_items.size()));*/
		}
		else
			auctionItem = _items.get(Rnd.get(_items.size()));
		
		long startingTime = _dateGenerator.nextDate(after);
		long endingTime = startingTime + TimeUnit.MILLISECONDS.convert(auctionItem.getAuctionLength(), TimeUnit.MINUTES);
		ItemAuction auction = new ItemAuction(_auctionIds.getAndIncrement(), _instanceId, startingTime, endingTime, auctionItem, ItemAuctionState.CREATED);
		auction.storeMe();
		return auction;
	}

	private ItemAuction loadAuction(int auctionId) throws SQLException
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT auctionItemId,startingTime,endingTime,auctionStateId FROM item_auction WHERE auctionId=?");
			statement.setInt(1, auctionId);
			rset = statement.executeQuery();

			if(!rset.next())
			{
				_log.log(Level.WARNING, "ItemAuctionInstance: Auction data not found for auction: " + auctionId);
				return null;
			}
			int auctionItemId = rset.getInt(1);
			long startingTime = rset.getLong(2);
			long endingTime = rset.getLong(3);
			byte auctionStateId = rset.getByte(4);
			statement.close();

			if (startingTime >= endingTime)
			{
				_log.log(Level.WARNING, "ItemAuctionInstance: Invalid starting/ending paramaters for auction: " + auctionId);
				return null;
			}
			AuctionItem auctionItem = getAuctionItem(auctionItemId);
			if (auctionItem == null)
			{
				_log.log(Level.WARNING, "ItemAuctionInstance: AuctionItem: " + auctionItemId + ", not found for auction: " + auctionId);
				return null;
			}
			ItemAuctionState auctionState = ItemAuctionState.stateForStateId(auctionStateId);
			if (auctionState == null)
			{
				_log.log(Level.WARNING, "ItemAuctionInstance: Invalid auctionStateId: " + auctionStateId + ", for auction: " + auctionId);
				return null;
			}

			statement = con.prepareStatement("SELECT playerObjId,playerBid FROM item_auction_bid WHERE auctionId=?");
			statement.setInt(1, auctionId);
			rset = statement.executeQuery();

			ItemAuction auction = new ItemAuction(auctionId, _instanceId, startingTime, endingTime, auctionItem, auctionState);

			while(rset.next())
			{
				int playerObjId = rset.getInt(1);
				long playerBid = rset.getLong(2);
				ItemAuctionBid bid = new ItemAuctionBid(playerObjId, playerBid);
				auction.addBid(bid);
			}
			return auction;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private final class ScheduleAuctionTask extends l2open.common.RunnableImpl
	{
		private final ItemAuction _auction;

		public ScheduleAuctionTask(ItemAuction auction)
		{
			_auction = auction;
		}

		public final void runImpl()
		{
			try
			{
				runImp();
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "ItemAuctionInstance: Failed scheduling auction " + _auction.getAuctionId(), e);
			}
		}

		private void runImp() throws Exception
		{
			ItemAuctionState state = _auction.getAuctionState();
			switch(state)
			{
				case CREATED:
					if (!_auction.setAuctionState(state, ItemAuctionState.STARTED))
					{
						throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.STARTED.toString() + ", expected: " + state.toString());
					}
					_log.log(Level.INFO, "ItemAuctionInstance: Auction " + _auction.getAuctionId() + " has started for instance " + _auction.getInstanceId());
					if(ConfigValue.AnnounceStartAuction)
					{
						String[] params = {_auction.getItem().getName()+(_auction.getItem().getRealEnchantLevel() > 0 ? "+"+_auction.getItem().getRealEnchantLevel() : "")};
						Announcements.getInstance().announceByCustomMessage("l2open.gameserver.model.instances.ItemBrokerInstance.announce." + _auction.getInstanceId(), params);
					}
					checkAndSetCurrentAndNextAuction();
					break;
				case STARTED:
					switch (_auction.getAuctionEndingExtendState())
					{
						case EXTEND_BY_5_MIN:
							if(_auction.getScheduledAuctionEndingExtendState() == ItemAuctionExtendState.INITIAL)
							{
								_auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_5_MIN);
								setStateTask(ThreadPoolManager.getInstance().schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0L)));
								return;
							}
							break;
						case EXTEND_BY_3_MIN:
							if(_auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_3_MIN)
							{
								_auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_3_MIN);
								setStateTask(ThreadPoolManager.getInstance().schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0L)));
								return;
							}
							break;
						case EXTEND_BY_CONFIG_PHASE_A:
							if(_auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B)
							{
								_auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B);
								setStateTask(ThreadPoolManager.getInstance().schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0L)));
								return;
							}
							break;
						case EXTEND_BY_CONFIG_PHASE_B:
							if (_auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A)
							{
								_auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A);
								setStateTask(ThreadPoolManager.getInstance().schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0)));
								return;
							}
					}

					if(!_auction.setAuctionState(state, ItemAuctionState.FINISHED))
					{
						throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.FINISHED.toString() + ", expected: " + state.toString());
					}
					onAuctionFinished(_auction);
					checkAndSetCurrentAndNextAuction();
					break;
				default:
					throw new IllegalStateException("Invalid state: " + state);
			}
		}
	}
}
