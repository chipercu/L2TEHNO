package l2open.gameserver.model.entity.Auction;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.AuctionManager;
import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.instancemanager.PlayerMessageStack;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;

import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

public class Auction
{
	protected static Logger _log = Logger.getLogger(Auction.class.getName());

	private final AuctionData _data;

	private int _Id = 0;
	private int _SellerId = 0;
	private String _SellerName = "";
	private String _SellerClanName = "";
	private String _ItemName = "";
	private long _StartingBid = 0;
	private long _CurrentBid = 0;
	private Calendar _EndDate;

	private int _HighestBidderId = 0;
	private String _HighestBidderName = "";
	private long _HighestBidderMaxBid = 0;

	private Map<Integer, Bidder> _bidders = new FastMap<Integer, Bidder>().setShared(true);

	public Auction(int auctionId)
	{
		_Id = auctionId;
		_data = new AuctionData(this);

		_data.load();
		_data.loadBid();

		/*if(getTimeRemaining() < -1800000)
		{
			// если аукцион закончился более чем пол часа назад значит сервер лежал, потому выставляем конец аукциона на сутки после запуска
			_EndDate = Calendar.getInstance();
			_EndDate.add(Calendar.HOUR_OF_DAY, 24);
		}*/
		if(getTimeRemaining() < 1800000)
		{
			// если аукцион закончился или закончится менее чем через пол часа то был недолгий рестарт, потому выставляем конец аукциона на пол часа после запуска
			_EndDate = Calendar.getInstance();
			_EndDate.add(Calendar.MINUTE, 30);
		}
		_EndDate.set(Calendar.MINUTE, 0);
		_EndDate.set(Calendar.SECOND, 0);
		_EndDate.set(Calendar.MILLISECOND, 0);

		correctAuctionTime(false);

		ThreadPoolManager.getInstance().schedule(new AutoEndTask(this), 1000);
	}

	public Auction(int id, L2Clan clan, long delay, long bid, String name)
	{
		_Id = id;
		_data = new AuctionData(this);
		_EndDate = Calendar.getInstance();
		_EndDate.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + delay);
		_EndDate.set(Calendar.MINUTE, 0);
		_ItemName = name;
		_SellerId = clan.getLeaderId();
		_SellerName = clan.getLeaderName();
		_SellerClanName = clan.getName();
		_StartingBid = bid;
	}

	private void correctAuctionTime(boolean forced)
	{
		boolean corrected = false;

		if(_EndDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() || forced)
		{
			// Since auction has past reschedule it to the next one (7 days)
			// This is usually caused by server being down
			corrected = true;
			if(forced)
				setNextAuctionDate();
			else
				endAuction(); //end auction normally in case it had bidders and server was down when it ended
		}

		_EndDate.set(Calendar.MINUTE, 0);
		_EndDate.set(Calendar.SECOND, 0);
		_EndDate.set(Calendar.MILLISECOND, 0);

		if(corrected)
			_data.saveAuctionDate();
	}

	private void setNextAuctionDate()
	{
		while(_EndDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
			// Set next auction date if auction has passed
			_EndDate.add(Calendar.DAY_OF_MONTH, 7); // Schedule to happen in 7 days
	}

	public void setBid(L2Player bidder, long bid)
	{
		if(!CanBid(bidder))
			return;

		int clanId = bidder.getClanId();
		if(clanId <= 0)
			return;

		if(bid < getStartingBid())
		{
			bidder.sendPacket(Msg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_THAT_CAN_BE_BID);
			return;
		}

		if(bid > 100000000000L)
		{
			bidder.sendPacket(Msg.YOUR_BID_CANNOT_EXCEED_100_BILLION);
			return;
		}

		if(bid <= getHighestBidderMaxBid())
		{
			bidder.sendPacket(Msg.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
			return;
		}

		long requiredAdena = bid;
		// Update bid if new bid is higher
		if(_bidders.get(clanId) != null)
		{
			requiredAdena = bid - _bidders.get(clanId).getBid();
			if(requiredAdena < 1)
			{
				bidder.sendPacket(Msg.THE_SECOND_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_ORIGINAL);
				return;
			}
		}

		long timeRemaining = getTimeRemaining();
		if(timeRemaining < 10000)
		{
			bidder.sendPacket(Msg.YOU_CANNOT_PARTICIPATE_IN_AN_AUCTION);
			return;
		}

		if(takeItem(bidder, requiredAdena))
		{
			if(!_bidders.isEmpty() && timeRemaining < 120000) // за 2 минуты до конца
			{
				_EndDate.add(Calendar.MINUTE, 5);
				bidder.sendPacket(Msg.BIDDER_EXISTS__THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES);
			}
			else if(!_bidders.isEmpty() && timeRemaining < 300000) // за 5 минут до конца
			{
				_EndDate.add(Calendar.MINUTE, 3);
				bidder.sendPacket(Msg.BIDDER_EXISTS__AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES);
			}

			if(_HighestBidderId > 0)
				PlayerMessageStack.getInstance().mailto(_HighestBidderId, Msg.YOU_HAVE_BEEN_OUTBID);

			_HighestBidderId = clanId;
			_HighestBidderMaxBid = bid;
			_HighestBidderName = bidder.getClan().getLeaderName();

			_data.setBid(bidder, bid);

			if(_bidders.get(clanId) == null)
				_bidders.put(clanId, new Bidder(bidder.getClan().getLeaderName(), bidder.getClan().getName(), bid, Calendar.getInstance().getTimeInMillis()));
			else
			{
				_bidders.get(clanId).setBid(bid);
				_bidders.get(clanId).setTimeBid(Calendar.getInstance().getTimeInMillis());
			}

			bidder.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1).addString(getItemName()));
			if(_SellerId > 0)
				PlayerMessageStack.getInstance().mailto(_SellerId, Msg.YOU_HAVE_BID_IN_A_CLAN_HALL_AUCTION);

			bidder.getClan().setAuctionBiddedAt(_Id);
			_data.setAuctionBiddedAt(bidder.getClanId(), _Id);
		}
	}

	private void returnItem(String Clan, long quantity, boolean penalty)
	{
		L2Clan clan = ClanTable.getInstance().getClanByName(Clan);
		returnItem(clan, quantity, penalty);
	}

	private void returnItem(L2Clan clan, long quantity, boolean penalty)
	{
		if(clan == null)
			return;
		if(penalty)
			quantity *= 0.9; //take 10% tax fee if needed
		clan.getWarehouse().addItem(ConfigValue.ClanHallBid_ItemId, quantity, null);
	}

	private boolean takeItem(L2Player bidder, long quantity)
	{
		if(bidder.getClan() != null && bidder.getClan().getWarehouse().countOf(ConfigValue.ClanHallBid_ItemId) >= quantity)
		{
			bidder.getClan().getWarehouse().destroyItem(ConfigValue.ClanHallBid_ItemId, quantity);
			return true;
		}
		else if(ConfigValue.ClanHallBidCharInventory && bidder.getInventory().getCountOf(ConfigValue.ClanHallBid_ItemId) >= quantity)
		{
			bidder.getInventory().destroyItemByItemId(ConfigValue.ClanHallBid_ItemId, quantity, true);
			return true;
		}
		bidder.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
		return false;
	}

	private void removeBids()
	{
		_data.removeBids();
		for(int bidderId : _bidders.keySet())
			try
			{
				cancelBid(bidderId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		_bidders.clear();
	}

	public void cancelBid(int bidderId)
	{
		_data.cancelBid(bidderId);
		Bidder bidder = _bidders.get(bidderId);
		L2Clan bidder_clan = ClanTable.getInstance().getClanByName(bidder.getClanName());
		if(bidder_clan != null)
		{
			if(bidder_clan.getHasHideout() == 0)
			{
				returnItem(bidder_clan, bidder.getBid(), true); // 10 % tax
				PlayerMessageStack.getInstance().mailto(bidder_clan.getLeaderId(), new SystemMessage("You haven't won ClanHall " + getItemName() + ". Your bid returned"));
			}
			bidder_clan.setAuctionBiddedAt(0);
			_data.setAuctionBiddedAt(bidder_clan.getClanId(), 0);
		}
		_bidders.remove(bidderId);
	}

	public void endAuction()
	{
		SystemMessage announce_msg = new SystemMessage(SystemMessage.S1_S_AUCTION_HAS_ENDED).addString(getItemName());
		for(L2Player player : L2ObjectsStorage.getPlayers())
			player.sendPacket(announce_msg);

		if(_HighestBidderId == 0 && _SellerId == 0)
		{
			correctAuctionTime(true);
			ThreadPoolManager.getInstance().schedule(new AutoEndTask(this), 1000);
			return;
		}
		if(_HighestBidderId == 0 && _SellerId > 0)
		{
			cancelAuction();
			PlayerMessageStack.getInstance().mailto(_SellerId, Msg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED);
			return;
		}

		ClanHall ch = ClanHallManager.getInstance().getClanHall(getId());
		if(ch == null)
			_log.warning("ClanHall is null for id " + _Id + ". WTF?");

		L2Clan HighestBidderClan = null;
		if(_bidders.get(_HighestBidderId) == null)
			_log.warning("Bidder with id " + _HighestBidderId + "is null. WTF?");
		else
		{
			HighestBidderClan = ClanTable.getInstance().getClanByName(_bidders.get(_HighestBidderId).getClanName());
			if(HighestBidderClan == null)
				_log.warning("Clan with name " + _bidders.get(_HighestBidderId).getClanName() + "is null. WTF?");
		}

		if(ch != null && HighestBidderClan != null)
		{
			if(_SellerId > 0)
			{
				returnItem(_SellerClanName, _HighestBidderMaxBid, true);
				returnItem(_SellerClanName, ch.getLease(), false);
				PlayerMessageStack.getInstance().mailto(_SellerId, new SystemMessage(SystemMessage.THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN).addString(HighestBidderClan.getName()));
			}

			ch.setLease(Math.max(ch.getPrice() / 100, _HighestBidderMaxBid / 100)); // Аренда - 1% от стоимости в неделю
			ch.changeOwner(HighestBidderClan);
			PlayerMessageStack.getInstance().mailto(HighestBidderClan.getLeaderId(), new SystemMessage("Congratulation! You have won ClanHall " + getItemName() + ". " + ch.getDesc()));
		}

		cancelAuction();
	}

	public boolean CanBid(L2Player bidder)
	{
		L2Clan bidder_clan = bidder.getClan();

		if(bidder_clan == null || bidder_clan.getLeaderId() != bidder.getObjectId())
		{
			bidder.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}
		else if(bidder_clan.getLevel() < getMinClanLevel())
		{
			if(getMinClanLevel() == 2)
				bidder.sendPacket(Msg.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
			else
				bidder.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Auction.MinClanLevel", bidder).addNumber(getMinClanLevel()));
			return false;
		}
		else if(bidder_clan.getHasHideout() > 0)
		{
			bidder.sendPacket(Msg.YOU_CANNOT_PARTICIPATE_IN_AN_AUCTION);
			return false;
		}
		else if(bidder_clan.getAuctionBiddedAt() > 0 && bidder_clan.getAuctionBiddedAt() != getId())
		{
			bidder.sendPacket(Msg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
			return false;
		}

		for(Auction auction : AuctionManager.getInstance().getAuctions())
			if(!equals(auction) && auction.getBidders().containsKey(bidder_clan.getClanId()))
			{
				bidder.sendPacket(Msg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
				return false;
			}

		if(bidder_clan.getMembersCount() < getMinClanMembers())
		{
			bidder.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Auction.MinClanMembers", bidder).addNumber(getMinClanMembers()));
			return false;
		}

		if(getMinClanMembersAvgLevel() > 1)
		{
			float avg_level = 0;
			int avg_level_count = 0;
			for(L2ClanMember member : bidder_clan.getMembers())
				if(member != null)
				{
					avg_level += member.getLevel();
					avg_level_count++;
				}

			avg_level /= avg_level_count;
			if(avg_level < getMinClanMembersAvgLevel())
			{
				bidder.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Auction.MinClanMembersAvgLevel", bidder).addNumber(getMinClanMembersAvgLevel()).addNumber((long) Math.ceil(avg_level)));
				return false;
			}
		}

		return true;
	}

	public void cancelAuction()
	{
		_data.deleteAuctionFromDB();
		removeBids();
	}

	public void confirmAuction()
	{
		AuctionManager.getInstance().getAuctions().add(this);
		_data.addAuction();
		_data.loadBid();
	}

	private int getClanHallGrade()
	{
		ClanHall ch = ClanHallManager.getInstance().getClanHall(getId());
		return ch == null ? 0 : ch.getGrade();
	}

	private int getMinClanLevel()
	{
		int grade = getClanHallGrade();
		if(grade == 1)
			return ConfigValue.ClanHallBid_Grade1_MinClanLevel;
		if(grade == 2)
			return ConfigValue.ClanHallBid_Grade2_MinClanLevel;
		if(grade == 3)
			return ConfigValue.ClanHallBid_Grade3_MinClanLevel;
		return 2;
	}

	private int getMinClanMembers()
	{
		int grade = getClanHallGrade();
		if(grade == 1)
			return ConfigValue.ClanHallBid_Grade1_MinClanMembers;
		if(grade == 2)
			return ConfigValue.ClanHallBid_Grade2_MinClanMembers;
		if(grade == 3)
			return ConfigValue.ClanHallBid_Grade3_MinClanMembers;
		return 1;
	}

	private int getMinClanMembersAvgLevel()
	{
		int grade = getClanHallGrade();
		if(grade == 1)
			return ConfigValue.ClanHallBid_Grade1_MinClanMembersAvgLevel;
		if(grade == 2)
			return ConfigValue.ClanHallBid_Grade2_MinClanMembersAvgLevel;
		if(grade == 3)
			return ConfigValue.ClanHallBid_Grade3_MinClanMembersAvgLevel;
		return 1;
	}

	public int getId()
	{
		return _Id;
	}

	public long getCurrentBid()
	{
		return _CurrentBid;
	}

	public void setCurrentBid(long value)
	{
		_CurrentBid = value;
	}

	public Calendar getEndDate()
	{
		return _EndDate;
	}

	public void setEndDate(Calendar value)
	{
		_EndDate = value;
	}

	public int getHighestBidderId()
	{
		return _HighestBidderId;
	}

	public void setHighestBidderId(int value)
	{
		_HighestBidderId = value;
	}

	public String getHighestBidderName()
	{
		return _HighestBidderName;
	}

	public void setHighestBidderName(String value)
	{
		_HighestBidderName = value;
	}

	public long getHighestBidderMaxBid()
	{
		return _HighestBidderMaxBid;
	}

	public void setHighestBidderMaxBid(long value)
	{
		_HighestBidderMaxBid = value;
	}

	public String getItemName()
	{
		return _ItemName;
	}

	public void setItemName(String value)
	{
		_ItemName = value;
	}

	public int getSellerId()
	{
		return _SellerId;
	}

	public void setSellerId(int value)
	{
		_SellerId = value;
	}

	public String getSellerName()
	{
		return _SellerName;
	}

	public void setSellerName(String value)
	{
		_SellerName = value;
	}

	public String getSellerClanName()
	{
		return _SellerClanName;
	}

	public void setSellerClanName(String value)
	{
		_SellerClanName = value;
	}

	public long getStartingBid()
	{
		return _StartingBid;
	}

	public void setStartingBid(long value)
	{
		_StartingBid = value;
	}

	public Map<Integer, Bidder> getBidders()
	{
		return _bidders;
	}

	public long getTimeRemaining()
	{
		return getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
	}
}