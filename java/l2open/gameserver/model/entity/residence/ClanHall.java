package l2open.gameserver.model.entity.residence;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.AuctionManager;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.Auction.Auction;
import l2open.gameserver.model.entity.siege.clanhall.ClanHallSiege;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Log;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class ClanHall extends Residence
{
	protected static Logger _log = Logger.getLogger(ClanHall.class.getName());

	private long _lease;
	private String _desc;
	private String _location;
	private Calendar _paidUntil;
	private boolean _inDebt;
	private int _grade;
	private long _price;

	private class AutoTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(getOwnerId() != 0)
				try
				{
					L2Clan clan = getOwner();
					if(clan == null)
					{
						_log.warning("ClanHall[59]: clan == null for residence id: " +getId() + " ClanHall Name" + getName());
                        changeOwner(null);
						return;
					}

					long lease = getLease();
					Castle castle = CastleManager.getInstance().getCastleByIndex(getZone().getTaxById());
					long tax = lease * castle.getTaxPercent() / 100;
					if(ConfigValue.ClanHallBid_ItemId == 57)
						lease += tax;
					else
						tax = 0;

					long clanadena = ConfigValue.ClanHallBid_ItemId != 57 ? getOwner().getWarehouse().countOf(ConfigValue.ClanHallBid_ItemId) : clan.getAdenaCount();
					if(getPaidUntil() > System.currentTimeMillis())
						ThreadPoolManager.getInstance().schedule(new AutoTask(), getPaidUntil() - System.currentTimeMillis());
					else if(clanadena >= lease)
					{
						clan.getWarehouse().destroyItem(ConfigValue.ClanHallBid_ItemId, lease);
						if(tax > 0)
							castle.addToTreasury(tax, true, false);
						setInDebt(false);
						updateRentTime();
						ThreadPoolManager.getInstance().schedule(new AutoTask(), getPaidUntil() - System.currentTimeMillis());
						Log.add("clanhall " + getName() + " lease " + lease + " adena from clan " + clan.getName() + "(id:" + clan.getClanId() + ") cwh at " + _paidUntil.get(Calendar.DAY_OF_MONTH) + "/" + _paidUntil.get(Calendar.MONTH), "residence");
					}
					else if(!isInDebt())
					{
						setInDebt(true);
						updateRentTime();
						ThreadPoolManager.getInstance().schedule(new AutoTask(), getPaidUntil() - System.currentTimeMillis());
						Log.add("clanhall " + getName() + " is in debt for " + lease + " adena from clan " + clan.getName() + "(id:" + clan.getClanId() + ") cwh at " + _paidUntil.get(Calendar.DAY_OF_MONTH) + "/" + _paidUntil.get(Calendar.MONTH), "residence");
					}
					else
					{
						Log.add("remove " + getName() + "  clanhall from clan " + clan.getName() + "(id:" + clan.getClanId() + "), because thay have only " + clanadena + " when lease is " + lease, "residence");
						changeOwner(null);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	private void startAutoTask()
	{
		new AutoTask().run();
	}

	public ClanHall(int clanHallId)
	{
		super(clanHallId);
	}

	private ClanHallSiege _Siege;
	private int _SiegeDayOfWeek;
	private int _SiegeHourOfDay;

	@Override
	public ClanHallSiege getSiege()
	{
		if(_SiegeDayOfWeek <= 0)
			return null;
		if(_Siege == null)
			_Siege = new ClanHallSiege(this);
		return _Siege;
	}

	@Override
	public int getSiegeDayOfWeek()
	{
		return _SiegeDayOfWeek;
	}

	@Override
	public int getSiegeHourOfDay()
	{
		return _SiegeHourOfDay;
	}

	@Override
	public void changeOwner(L2Clan clan)
	{
		L2Clan oldOwner = getOwner();

		// Remove old owner
		if(oldOwner != null && (clan == null || clan.getClanId() != oldOwner.getClanId()))
		{
			removeSkills(); // Удаляем КХ скилы у старого владельца
			oldOwner.setHasHideout(0); // Unset has hideout flag for old owner
		}

		// Update in database
		updateOwnerInDB(clan);
		rewardSkills(); // Выдаем КХ скилы новому владельцу

		if(clan != null && getLease() > 0)
		{
			updateRentTime();
			startAutoTask();
		}

		int hideout = 0;

		//Если у клана было 2 КХ (обычный и осаждаемый) то при снятии осаждаемого КХ, возвращаем клану возможность тп в обычный кх.
		if(oldOwner != null)
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			ResultSet rs = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT id FROM clanhall WHERE ownerId=?");
				statement.setInt(1, oldOwner.getClanId());
				rs = statement.executeQuery();

				if(rs.next())
					hideout = rs.getInt("id");

				rs.close();
				statement.close();
			}
			catch(Exception e)
			{
				_log.warning("Exception: can't get clanhall id from ownerId " + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, statement, rs);
			}
			if(hideout != 0)
			{
				oldOwner.setHasHideout(hideout);
			}
		}
	}

	@Override
	protected void loadData()
	{
		_SiegeDayOfWeek = 1;
		_SiegeHourOfDay = 12;
		_Siege = null;

		_type = ResidenceType.Clanhall;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		_paidUntil = Calendar.getInstance();

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM clanhall WHERE id = ?");
			statement.setInt(1, getId());
			rs = statement.executeQuery();

			if(rs.next())
			{
				_name = rs.getString("name");
				_ownerId = rs.getInt("ownerId");
				_price = (long) (rs.getLong("price") * ConfigValue.ResidenceLeaseMultiplier);
				_lease = Math.max(_price / 100, rs.getLong("lease"));
				_desc = rs.getString("desc");
				_location = rs.getString("location");
				_paidUntil.setTimeInMillis(rs.getLong("paidUntil"));
				_grade = rs.getInt("Grade");
				_inDebt = rs.getInt("inDebt") == 1;

				_SiegeDayOfWeek = rs.getInt("siegeDayOfWeek");
				_SiegeHourOfDay = rs.getInt("siegeHourOfDay");

				if(_SiegeDayOfWeek > 0)
					getSiege().setSiegeDateTime(rs.getLong("siegeDate") * 1000L);

				StringTokenizer st = new StringTokenizer(rs.getString("skills"), ";");
				while(st.hasMoreTokens())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(Integer.valueOf(st.nextToken()), Integer.valueOf(st.nextToken()));
					if(skill != null)
						_skills.add(skill);
				}
			}
			DatabaseUtils.closeDatabaseSR(statement, rs);

			statement = con.prepareStatement("SELECT clan_id FROM clan_data WHERE hasHideout = ?");
			statement.setInt(1, getId());
			rs = statement.executeQuery();

			if(rs.next())
				_ownerId = rs.getInt("clan_id");

			_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.ClanHall, getId(), true);
		}
		catch(Exception e)
		{
			_log.warning("Exception: ClanHall.load(): " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}

		if(getOwnerId() == 0) // this should never happen, but one never knows ;)
			return;

		if(getLease() > 0)
			startAutoTask();
	}

	private void updateOwnerInDB(L2Clan clan)
	{
		if(clan != null)
			_ownerId = clan.getClanId(); // Update owner id property
		else
			_ownerId = 0; // Remove owner

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clanhall SET ownerId=?, lease=?, inDebt=0 WHERE id=?");
			statement.setInt(1, getOwnerId());
			statement.setLong(2, getLease());
			statement.setInt(3, getId());
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE clan_data SET hasHideout=0 WHERE hasHideout=?");
			statement.setInt(1, getId());
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE clan_data SET hasHideout=? WHERE clan_id=?");
			statement.setInt(1, getId());
			statement.setInt(2, getOwnerId());
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=?");
			statement.setInt(1, getId());
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// Announce to clan memebers
			if(clan != null)
			{
				clan.setHasHideout(getId()); // Set has hideout flag for new owner
				clan.broadcastClanStatus(false, true, true);
			}
			else if(getPrice() > 0)
			{
				Calendar endDate = Calendar.getInstance();
				endDate.add(Calendar.DAY_OF_MONTH, 7); // Schedule to happen in 7 days
				statement = con.prepareStatement("REPLACE INTO auction (id, sellerId, sellerName, sellerClanName, itemName, startingBid, currentBid, endDate) VALUES (?,?,?,?,?,?,?,?)");
				statement.setInt(1, getId());
				statement.setInt(2, 0);
				statement.setString(3, "NPC");
				statement.setString(4, "NPC Clan");
				statement.setString(5, getName());
				statement.setLong(6, getPrice());
				statement.setLong(7, 0);
				statement.setLong(8, endDate.getTimeInMillis());
				statement.execute();
				DatabaseUtils.closeStatement(statement);
				//выставляем сразу на аукцион
				AuctionManager.getInstance().getAuctions().add(new Auction(getId()));
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: updateOwnerInDB(L2Clan clan): " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeConnection(con);
		}
	}

	public long getPrice()
	{
		return _price;
	}

	public long getLease()
	{
		return isInDebt() ? _lease * 2 : _lease;
	}

	public void setLease(long lease)
	{
		_lease = lease;
	}

	public String getDesc()
	{
		return _desc;
	}

	public String getLocation()
	{
		return _location;
	}

	public long getPaidUntil()
	{
		return _paidUntil.getTimeInMillis();
	}

	public Calendar getPaidUntilCalendar()
	{
		return _paidUntil;
	}

	public int getGrade()
	{
		return _grade;
	}

	public void updateRentTime()
	{
		_paidUntil.setTimeInMillis(System.currentTimeMillis() + 604800000);
		_paidUntil.set(Calendar.MINUTE, 0);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clanhall SET paidUntil=?, inDebt=? WHERE id=?");
			statement.setLong(1, _paidUntil.getTimeInMillis());
			statement.setLong(2, _inDebt ? 1 : 0);
			statement.setInt(3, getId());
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.warning("Exception: ClanHall.updateRentTime(): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public boolean isInDebt()
	{
		return _inDebt;
	}

	public void setInDebt(boolean val)
	{
		_inDebt = val;
	}

	@Override
	public void saveOwnDate()
	{}
}