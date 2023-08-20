package l2open.gameserver.model.entity.ItemBroker;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ItemAuctionManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.ExNoticePostArrived;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Log;
import l2open.util.Util;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItemAuction
{
	static final Logger _log = Logger.getLogger(ItemAuctionManager.class.getName());
	private static final long ENDING_TIME_EXTEND_5 = 300; // я на ебальники сцал тому кто такое пишет...TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
	private static final long ENDING_TIME_EXTEND_3 = 180; // дважды сцал на ебальники, ну пиздец...TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES);
	private final int _auctionId;
	private final int _instanceId;
	private final long _startingTime;
	private volatile long _endingTime;
	private final AuctionItem _auctionItem;
	private TIntObjectHashMap<ItemAuctionBid> _auctionBids;
	private final Object _auctionStateLock;
	private volatile ItemAuctionState _auctionState;
	private volatile ItemAuctionExtendState _scheduledAuctionEndingExtendState;
	private volatile ItemAuctionExtendState _auctionEndingExtendState;
	private ItemAuctionBid _highestBid;
	private final L2ItemInstance _item;
	private int _lastBidPlayerObjId;

	public ItemAuction(int auctionId, int instanceId, long startingTime, long endingTime, AuctionItem auctionItem, ItemAuctionState auctionState)
	{
		_auctionId = auctionId;
		_instanceId = instanceId;
		_startingTime = startingTime;
		_endingTime = endingTime;
		_auctionItem = auctionItem;
		_auctionBids = new TIntObjectHashMap<ItemAuctionBid>();
		_auctionState = auctionState;
		_auctionStateLock = new Object();
		_scheduledAuctionEndingExtendState = ItemAuctionExtendState.INITIAL;
		_auctionEndingExtendState = ItemAuctionExtendState.INITIAL;

		_item = _auctionItem.createNewItemInstance();
		L2World.removeObject(_item);

		/**
		5 - До конца аука в городе N осталось N часов лот N текущая ставка N Adena
		желательно сделать конфигом. HowHourAnons = 1 (ставится полный час) типо того...

		**/
		if(_auctionState != ItemAuctionState.FINISHED && ConfigValue.AltItemAuctionAnnounce > 0)
		{
			if((getFinishingTimeRemaining()-ConfigValue.AltItemAuctionAnnounce*60000) > 0)
			{
				ThreadPoolManager.getInstance().schedule(new Runnable()
				{
					public void run()
					{
						String[] params = {String.valueOf(getFinishingTimeRemaining()/3600000), _item.getName(), Util.formatAdena(_highestBid == null ? getAuctionInitBid() : _highestBid.getLastBid())};
						Announcements.getInstance().announceByCustomMessage("ItemBrokerInstance.announce.end." + getInstanceId(), params);
					}
				}, getFinishingTimeRemaining()-ConfigValue.AltItemAuctionAnnounce*60000);
			}
		}
	}

	void addBid(ItemAuctionBid bid)
	{
		_auctionBids.put(bid.getPlayerObjId(), bid);
		if(_highestBid == null || _highestBid.getLastBid() < bid.getLastBid())
			_highestBid = bid;
	}

	public final ItemAuctionState getAuctionState()
	{
		ItemAuctionState auctionState;
		synchronized(_auctionStateLock)
		{
			auctionState = _auctionState;
		}

		return auctionState;
	}

	public final boolean setAuctionState(ItemAuctionState expected, ItemAuctionState wanted)
	{
		synchronized(_auctionStateLock)
		{
			if(_auctionState != expected)
			{
				return false;
			}
			_auctionState = wanted;
			storeMe();
			return true;
		}
	}

	public final int getAuctionId()
	{
		return _auctionId;
	}

	public final int getInstanceId()
	{
		return _instanceId;
	}

	public final L2ItemInstance getItem()
	{
		return _item;
	}

	public final L2ItemInstance createNewItemInstance()
	{
		return _auctionItem.createNewItemInstance();
	}

	public final long getAuctionInitBid()
	{
		return _auctionItem.getAuctionInitBid();
	}

	public final ItemAuctionBid getHighestBid()
	{
		return _highestBid;
	}

	public final ItemAuctionExtendState getAuctionEndingExtendState()
	{
		return _auctionEndingExtendState;
	}

	public final ItemAuctionExtendState getScheduledAuctionEndingExtendState()
	{
		return _scheduledAuctionEndingExtendState;
	}

	public final void setScheduledAuctionEndingExtendState(ItemAuctionExtendState state)
	{
		_scheduledAuctionEndingExtendState = state;
	}

	public final long getStartingTime()
	{
		return _startingTime;
	}

	public final long getEndingTime()
	{
		return _endingTime;
	}

	public final long getStartingTimeRemaining()
	{
		return Math.max(_startingTime - System.currentTimeMillis(), 0);
	}

	public final long getFinishingTimeRemaining()
	{
		return Math.max(_endingTime - System.currentTimeMillis(), 0);
	}

	public final void storeMe()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO item_auction (auctionId,instanceId,auctionItemId,startingTime,endingTime,auctionStateId) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE auctionStateId=?");
			statement.setInt(1, _auctionId);
			statement.setInt(2, _instanceId);
			statement.setInt(3, _auctionItem.getAuctionItemId());
			statement.setLong(4, _startingTime);
			statement.setLong(5, _endingTime);
			statement.setByte(6, _auctionState.getStateId());
			statement.setByte(7, _auctionState.getStateId());
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public final int getAndSetLastBidPlayerObjectId(int playerObjId)
	{
		final int lastBid = _lastBidPlayerObjId;
		_lastBidPlayerObjId = playerObjId;
		return lastBid;
	}

	private final void updatePlayerBid(ItemAuctionBid bid, boolean delete)
	{
		updatePlayerBidInternal(bid, delete);
	}

	final void updatePlayerBidInternal(ItemAuctionBid bid, boolean delete)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			if (delete)
			{
				statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=? AND playerObjId=?");
				statement.setInt(1, _auctionId);
				statement.setInt(2, bid.getPlayerObjId());
			}
			else
			{
				statement = con.prepareStatement("INSERT INTO item_auction_bid (auctionId,playerObjId,playerBid) VALUES (?,?,?) ON DUPLICATE KEY UPDATE playerBid=?");
				statement.setInt(1, _auctionId);
				statement.setInt(2, bid.getPlayerObjId());
				statement.setLong(3, bid.getLastBid());
				statement.setLong(4, bid.getLastBid());
			}

			statement.execute();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public final void registerBid(final L2Player player, final long newBid)
	{
		if(player == null)
		{
			throw new NullPointerException();
		}
		if(newBid < getAuctionInitBid())
		{
			player.sendPacket(Msg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_THAT_CAN_BE_BID);
			return;
		}

		if(newBid > ConfigValue.AltItemAuctionBitLimit && ConfigValue.AltItemAuctionBitLimit > 0)
		{
			player.sendPacket(Msg.YOUR_BID_CANNOT_EXCEED_100_BILLION);
			return;
		}

		if(getAuctionState() != ItemAuctionState.STARTED)
		{
			return;
		}
		final int playerObjId = player.getObjectId();

		synchronized (_auctionBids)
		{
			if(_highestBid != null && newBid < _highestBid.getLastBid())
			{
				player.sendPacket(Msg.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
				return;
			}

			ItemAuctionBid bid = getBidFor(playerObjId);
			if(bid == null)
			{
				if(!reduceItemCount(player, newBid))
				{
					if(ConfigValue.AltItemAuctionItemId == 57)
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
					else
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
					return;
				}

				bid = new ItemAuctionBid(playerObjId, newBid);
				_auctionBids.put(playerObjId, bid);
			}
			else
			{
				if(!bid.isCanceled())
				{
					if(bid.getLastBid() >= newBid)
					{
						player.sendPacket(Msg.THE_SECOND_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_ORIGINAL);
						return;
					}

					if(!reduceItemCount(player, newBid - bid.getLastBid()))
					{
						if(ConfigValue.AltItemAuctionItemId == 57)
							player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
						else
							player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
						return;
					}
				}
				else if (!reduceItemCount(player, newBid))
				{
					if(ConfigValue.AltItemAuctionItemId == 57)
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
					else
						player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
					return;
				}
				bid.setLastBid(newBid);
			}

			onPlayerBid(player, bid);
			updatePlayerBid(bid, false);

			player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1).addNumber(newBid));
			player.sendPacket(new ExNoticePostArrived(-6));
		}
	}

	private final void onPlayerBid(final L2Player player, final ItemAuctionBid bid)
	{
		if(_highestBid == null)
		{
			_highestBid = bid;
		}
		else if(_highestBid.getLastBid() < bid.getLastBid())
		{
			final L2Player old = _highestBid.getPlayer();
			if(old != null)
			{
				old.sendPacket(Msg.YOU_HAVE_BEEN_OUTBID);
			}
			_highestBid = bid;
		}

		if(_endingTime - System.currentTimeMillis() > 600000)
			return;
		switch(_auctionEndingExtendState)
		{
			case INITIAL:
				_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_5_MIN;
				_endingTime += ENDING_TIME_EXTEND_5;
				broadcastToAllBidders(Msg.BIDDER_EXISTS__THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES);
			break;
			case EXTEND_BY_5_MIN:
				if (getAndSetLastBidPlayerObjectId(player.getObjectId()) == player.getObjectId())
					return;
				_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_3_MIN;
				_endingTime += ENDING_TIME_EXTEND_3;
				broadcastToAllBidders(Msg.BIDDER_EXISTS__AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES);
			break;
			case EXTEND_BY_3_MIN:
				if(ConfigValue.AltItemAuctionTimeExtendsOnBid <= 0 || getAndSetLastBidPlayerObjectId(player.getObjectId()) == player.getObjectId())
					return;
				_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
				_endingTime += ConfigValue.AltItemAuctionTimeExtendsOnBid;
			break;
			case EXTEND_BY_CONFIG_PHASE_A:
				if (getAndSetLastBidPlayerObjectId(player.getObjectId()) == player.getObjectId() || _scheduledAuctionEndingExtendState != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B)
					return;
				_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B;
				_endingTime += ConfigValue.AltItemAuctionTimeExtendsOnBid;
			break;
			case EXTEND_BY_CONFIG_PHASE_B:
				if (getAndSetLastBidPlayerObjectId(player.getObjectId()) == player.getObjectId() || _scheduledAuctionEndingExtendState != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A)
					return;
				_endingTime += ConfigValue.AltItemAuctionTimeExtendsOnBid;
				_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
		}
	}

	public void broadcastToAllBidders(L2GameServerPacket packet)
	{
		TIntObjectIterator<ItemAuctionBid> itr = _auctionBids.iterator();
		ItemAuctionBid bid = null;
		while(itr.hasNext())
		{
			itr.advance();
			bid = itr.value();
			L2Player player = bid.getPlayer();
			if(player != null)
				player.sendPacket(packet);
		}
	}

	public final boolean cancelBid(final L2Player player)
	{
		if(player == null)
		{
			throw new NullPointerException();
		}
		switch(getAuctionState())
		{
			case CREATED:
				return false;
				
			case FINISHED:
				if(_startingTime < System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(ConfigValue.AltItemAuctionExpiredAfter, TimeUnit.DAYS))
					return false;
				break;
		}

		final int playerObjId = player.getObjectId();

		synchronized(_auctionBids)
		{
			if(_highestBid == null)
			{
				return false;
			}
			ItemAuctionBid bid = getBidFor(playerObjId);
			if(bid == null || bid.isCanceled())
			{
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU));
				return false;
			}
			if(bid.getPlayerObjId() == _highestBid.getPlayerObjId())
			{
				if(getAuctionState() == ItemAuctionState.FINISHED)
				{
			  		return false;
				}
				player.sendPacket(new SystemMessage(2079));
				return true;
			}

			increaseItemCount(player, bid.getLastBid());
			bid.cancelBid();

			updatePlayerBid(bid, getAuctionState() == ItemAuctionState.FINISHED);

			player.sendPacket(new SystemMessage(679));
		}
		return true;
	}

	private boolean reduceItemCount(L2Player player, long count)
	{
		PcInventory inv = player.getInventory();
		if(inv.getCountOf(ConfigValue.AltItemAuctionItemId) >= count)
		{
			inv.destroyItemByItemId(ConfigValue.AltItemAuctionItemId, count, true);
			return true;
		}

		if(ConfigValue.AltItemAuctionItemId == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
		else
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);

		return false;
	}

	private void increaseItemCount(L2Player player, long count)
	{
		Log.LogItem(player.getInventory().getOwner(), Log.Sys_GetItem, player.getInventory().addItem(ConfigValue.AltItemAuctionItemId, count));
		player.sendPacket(SystemMessage.obtainItems(ConfigValue.AltItemAuctionItemId,count,0));
	}

	public final long getLastBid(final L2Player player)
	{
		final ItemAuctionBid bid = getBidFor(player.getObjectId());
		return bid != null ? bid.getLastBid() : -1L;
	}
	
	public ItemAuctionBid getBidFor(int charId)
	{
		return _auctionBids.get(charId);
	}
}
