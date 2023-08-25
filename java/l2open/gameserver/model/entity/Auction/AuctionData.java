package l2open.gameserver.model.entity.Auction;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.AuctionManager;
import l2open.gameserver.model.L2Player;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * В этом классе только работа с базой
 * @author Diamond
 */
public class AuctionData
{
	protected static Logger _log = Logger.getLogger(AuctionData.class.getName());

	private final Auction _auction;

	public AuctionData(Auction auction)
	{
		_auction = auction;
	}

	public void load()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM auction WHERE id = ?");
			statement.setInt(1, _auction.getId());
			rs = statement.executeQuery();

			while(rs.next())
			{
				_auction.setCurrentBid(rs.getLong("currentBid"));
				_auction.setEndDate(Calendar.getInstance());
				_auction.getEndDate().setTimeInMillis(rs.getLong("endDate"));
				_auction.setItemName(rs.getString("itemName"));
				_auction.setSellerId(rs.getInt("sellerId"));
				_auction.setSellerClanName(rs.getString("sellerClanName"));
				_auction.setSellerName(rs.getString("sellerName"));
				_auction.setStartingBid(rs.getLong("startingBid"));
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception: Auction.load(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public void loadBid()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT bidderId, bidderName, maxBid, clan_name, time_bid FROM auction_bid WHERE auctionId = ? ORDER BY maxBid DESC");
			statement.setInt(1, _auction.getId());
			rs = statement.executeQuery();

			while(rs.next())
			{
				if(rs.isFirst())
				{
					_auction.setHighestBidderId(rs.getInt("bidderId"));
					_auction.setHighestBidderName(rs.getString("bidderName"));
					_auction.setHighestBidderMaxBid(rs.getLong("maxBid"));
				}
				else if(rs.getLong("maxBid") > _auction.getHighestBidderMaxBid())
				{
					_auction.setHighestBidderId(rs.getInt("bidderId"));
					_auction.setHighestBidderName(rs.getString("bidderName"));
					_auction.setHighestBidderMaxBid(rs.getLong("maxBid"));
				}
				_auction.getBidders().put(rs.getInt("bidderId"), new Bidder(rs.getString("bidderName"), rs.getString("clan_name"), rs.getLong("maxBid"), rs.getLong("time_bid")));
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception: Auction.loadBid(): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public void setBid(L2Player bidder, long bid)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			if(_auction.getBidders().get(bidder.getClanId()) != null)
			{
				statement = con.prepareStatement("UPDATE auction_bid SET bidderId=?, bidderName=?, maxBid=?, time_bid=? WHERE auctionId=? AND bidderId=?");
				statement.setInt(1, bidder.getClanId());
				statement.setString(2, bidder.getClan().getLeaderName());
				statement.setLong(3, bid);
				statement.setLong(4, Calendar.getInstance().getTimeInMillis());
				statement.setInt(5, _auction.getId());
				statement.setInt(6, bidder.getClanId());
				statement.execute();
			}
			else
			{
				statement = con.prepareStatement("INSERT INTO auction_bid (id, auctionId, bidderId, bidderName, maxBid, clan_name, time_bid) VALUES (?, ?, ?, ?, ?, ?, ?)");
				statement.setInt(1, IdFactory.getInstance().getNextId());
				statement.setInt(2, _auction.getId());
				statement.setInt(3, bidder.getClanId());
				statement.setString(4, bidder.getName());
				statement.setLong(5, bid);
				statement.setString(6, bidder.getClan().getName());
				statement.setLong(7, Calendar.getInstance().getTimeInMillis());
				statement.execute();
			}
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Exception: Auction.updateInDB(L2Player bidder, int bid): " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void cancelBid(int bidder)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM auction_bid WHERE auctionId=? AND bidderId=?");
			statement.setInt(1, _auction.getId());
			statement.setInt(2, bidder);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Exception: Auction.cancelBid(String bidder): " + e.getMessage(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void removeBids()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM auction_bid WHERE auctionId=?");
			statement.setInt(1, _auction.getId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Exception: Auction.deleteFromDB(): " + e.getMessage(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void setAuctionBiddedAt(int clanId, int bid_at)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET auction_bid_at=? WHERE clan_id=?");
			statement.setInt(1, bid_at);
			statement.setInt(2, clanId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("Could not store auction for clan: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void deleteAuctionFromDB()
	{
		AuctionManager.getInstance().getAuctions().remove(this);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM auction WHERE id=?");
			statement.setInt(1, _auction.getId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Exception: Auction.deleteFromDB(): " + e.getMessage(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void addAuction()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO auction (id, sellerId, sellerName, sellerClanName, itemName, startingBid, currentBid, endDate) VALUES (?,?,?,?,?,?,?,?)");
			statement.setInt(1, _auction.getId());
			statement.setInt(2, _auction.getSellerId());
			statement.setString(3, _auction.getSellerName());
			statement.setString(4, _auction.getSellerClanName());
			statement.setString(5, _auction.getItemName());
			statement.setLong(6, _auction.getStartingBid());
			statement.setLong(7, _auction.getCurrentBid());
			statement.setLong(8, _auction.getEndDate().getTimeInMillis());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Exception: Auction.confirmAuction(): " + e.getMessage(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void saveAuctionDate()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE auction SET endDate = ? WHERE id = ?");
			statement.setLong(1, _auction.getEndDate().getTimeInMillis());
			statement.setInt(2, _auction.getId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Exception: saveAuctionDate(): " + e.getMessage(), e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}