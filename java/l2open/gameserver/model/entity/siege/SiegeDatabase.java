package l2open.gameserver.model.entity.siege;

import javolution.util.FastMap;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.util.Log;

import java.sql.ResultSet;
import java.util.logging.Logger;

public abstract class SiegeDatabase
{
	private static Logger _log = Logger.getLogger(SiegeDatabase.class.getName());
	protected Siege _siege;

	public SiegeDatabase(Siege siege)
	{
		_siege = siege;
	}

	public abstract void saveSiegeDate();

	public void saveLastSiegeDate()
	{}

	/**
	 * Return true if the clan is registered or owner of a castle
	 */
	public static boolean checkIsRegistered(L2Clan clan, int unitid)
	{
		if(clan == null)
			return false;

		if(unitid > 0 && clan.getHasCastle() == unitid)
			return true;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		boolean register = false;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT clan_id FROM siege_clans where clan_id=?" + (unitid == 0 ? "" : " and unit_id=?"));
			statement.setInt(1, clan.getClanId());
			if(unitid != 0)
				statement.setInt(2, unitid);
			rset = statement.executeQuery();
			if(rset.next())
				register = true;
		}
		catch(Exception e)
		{
			_log.warning("Exception: checkIsRegistered(): " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		return register;
	}

	public void clearSiegeClan()
	{
		int unit_id = _siege.getSiegeUnit().getId();
		Log.add("clearSiegeClan unit_id: " + unit_id + " from: ", "siege_debug");
		//Log.addStackTrace("siege_debug"); //TODO RemoveDebug
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM siege_clans WHERE unit_id=?");
			statement.setInt(1, unit_id);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("Exception: clearSiegeClan(): " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		_siege.getAttackerClans().clear();
		_siege.getDefenderClans().clear();
		_siege.getDefenderWaitingClans().clear();
		_siege.getDefenderRefusedClans().clear();
	}

	public void clearSiegeClan(SiegeClanType type)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM siege_clans WHERE unit_id=? and type=?");
			statement.setInt(1, _siege.getSiegeUnit().getId());
			statement.setInt(2, type.getId());
			statement.execute();
			_siege.getSiegeClans(type).clear();
		}
		catch(Exception e)
		{
			_log.warning("Exception: clearSiegeWaitingClan(): " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void removeSiegeClan(int clanId)
	{
		if(clanId <= 0)
			return;
		int unit_id = _siege.getSiegeUnit().getId();
		Log.add("removeSiegeClan unit_id: " + unit_id + ", clanId: " + clanId + ", from: ", "siege_debug");
		//Log.addStackTrace("siege_debug"); //TODO RemoveDebug
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM siege_clans WHERE unit_id=? and clan_id=?");
			statement.setInt(1, unit_id);
			statement.setInt(2, clanId);
			statement.execute();
			loadSiegeClan();
		}
		catch(Exception e)
		{
			_log.warning("Exception: removeSiegeClan(): " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void loadSiegeClan()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			FastMap<SiegeClanType, FastMap<Integer, SiegeClan>> clans = _siege.getSiegeClanList();
			clans.put(SiegeClanType.ATTACKER, new FastMap<Integer, SiegeClan>().setShared(true));
			clans.put(SiegeClanType.DEFENDER, new FastMap<Integer, SiegeClan>().setShared(true));
			clans.put(SiegeClanType.DEFENDER_REFUSED, new FastMap<Integer, SiegeClan>().setShared(true));
			clans.put(SiegeClanType.DEFENDER_WAITING, new FastMap<Integer, SiegeClan>().setShared(true));

			// Add castle owner as defender
			if(_siege.getSiegeUnit().getOwnerId() > 0 && _siege.getSiegeUnit() instanceof Castle)
				_siege.addSiegeClan(_siege.getSiegeUnit().getOwnerId(), SiegeClanType.OWNER);
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT clan_id, type FROM siege_clans where unit_id = ?");
			statement.setInt(1, _siege.getSiegeUnit().getId());
			rset = statement.executeQuery();
			while(rset.next())
				_siege.addSiegeClan(rset.getInt("clan_id"), SiegeClanType.getById(rset.getInt("type")));
			if(!_siege.getAttackerClans().isEmpty())
			{
				if(_siege.getSiegeUnit().getOwnerId() > 0 && _siege.getSiegeUnit() instanceof Fortress)
					_siege.addSiegeClan(_siege.getSiegeUnit().getOwnerId(), SiegeClanType.OWNER);
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: loadSiegeClan(): " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void saveSiegeClan(L2Clan clan, int typeId)
	{
		if(clan == null)
			return;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO siege_clans (clan_id,unit_id,type) VALUES (?,?,?)");
			statement.setInt(1, clan.getClanId());
			statement.setInt(2, _siege.getSiegeUnit().getId());
			statement.setInt(3, typeId);
			statement.execute();
			_siege.addSiegeClan(clan.getClanId(), SiegeClanType.getById(typeId));
		}
		catch(Exception e)
		{
			_log.warning("Exception: saveSiegeClan: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}