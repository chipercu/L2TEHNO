package l2open.gameserver.instancemanager;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class SiegeGuardManager
{
	private static Logger _log = Logger.getLogger(SiegeGuardManager.class.getName());

	private Residence _siegeUnit;
	private GArray<L2Spawn> _siegeGuardSpawn = new GArray<L2Spawn>();

	public SiegeGuardManager(Residence siegeUnit)
	{
		_siegeUnit = siegeUnit;
	}

	/**
	 * Add guard.<BR><BR>
	 */
	public void addSiegeGuard(L2Player activeChar, int npcId)
	{
		if(activeChar == null)
			return;
		addSiegeGuard(activeChar.getLoc(), npcId);
	}

	/**
	 * Add guard.<BR><BR>
	 */
	public void addSiegeGuard(Location loc, int npcId)
	{
		saveSiegeGuard(loc, npcId, 0);
	}

	/**
	 * Hire merc.<BR><BR>
	 */
	public void hireMerc(L2Player activeChar, int npcId)
	{
		if(activeChar == null)
			return;
		hireMerc(activeChar.getLoc(), npcId);
	}

	/**
	 * Hire merc.<BR><BR>
	 */
	public void hireMerc(Location loc, int npcId)
	{
		saveSiegeGuard(loc, npcId, 1);
	}

	/**
	 * Remove a single mercenary, identified by the npcId and location.
	 * Presumably, this is used when a castle lord picks up a previously dropped ticket
	 */
	public void removeMerc(int npcId, Location loc)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("Delete From siege_guards Where npcId = ? And x = ? AND y = ? AND z = ? AND isHired = 1");
			statement.setInt(1, npcId);
			statement.setInt(2, loc.x);
			statement.setInt(3, loc.y);
			statement.setInt(4, loc.z);
			statement.execute();
		}
		catch(Exception e1)
		{
			_log.warning("Error deleting hired siege guard at " + loc.toString() + ":" + e1);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Remove mercs.<BR><BR>
	 */
	public static void removeMercsFromDb(int unitId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("Delete From siege_guards Where unitId = ? And isHired = 1");
			statement.setInt(1, unitId);
			statement.execute();
		}
		catch(Exception e1)
		{
			_log.warning("Error deleting hired siege guard for unit " + unitId + ":" + e1);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Spawn guards.<BR><BR>
	 */
	public void spawnSiegeGuard()
	{
		unspawnSiegeGuard();
		loadSiegeGuard();
		for(L2Spawn spawn : _siegeGuardSpawn)
			if(spawn != null)
			{
				spawn.init();
				if(spawn.getNativeRespawnDelay() == 0)
					spawn.stopRespawn();
			}
	}

	/**
	 * Unspawn guards.<BR><BR>
	 */
	public void unspawnSiegeGuard()
	{
		for(L2Spawn spawn : _siegeGuardSpawn)
		{
			if(spawn == null)
				continue;

			spawn.stopRespawn();
			if(spawn.getLastSpawn() != null)
				spawn.getLastSpawn().deleteMe();
		}

		getSiegeGuardSpawn().clear();
	}

	/**
	 * Load guards.<BR><BR>
	 */
	private void loadSiegeGuard()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM siege_guards Where unitId = ? And isHired = ?");
			statement.setInt(1, getSiegeUnit().getId());
			if(getSiegeUnit().getOwnerId() > 0 && getSiegeUnit().getType() != ResidenceType.Fortress) // If castle is owned by a clan, then don't spawn default guards
				statement.setInt(2, 1);
			else
				statement.setInt(2, 0);
			rset = statement.executeQuery();

			L2Spawn spawn1;
			L2NpcTemplate template;

			while(rset.next())
			{
				template = NpcTable.getTemplate(rset.getInt("npcId"));
				if(template == null)
				{
					_log.warning("Error loading siege guard, missing npc data in npc table for id: " + rset.getInt("npcId"));
					continue;
				}

				spawn1 = new L2Spawn(template);
				spawn1.setAmount(1);
				spawn1.setLoc(new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"), rset.getInt("heading")));
				spawn1.setRespawnDelay(rset.getInt("isHired") == 1 ? 0 : rset.getInt("respawnDelay"));
				spawn1.setLocation(0);
				if(rset.getInt("respawnDelay") > 0)
					spawn1.startRespawn();

				_siegeGuardSpawn.add(spawn1);
			}
		}
		catch(Exception e1)
		{
			_log.warning("Error loading siege guard for unit " + getSiegeUnit().getName() + ":" + e1);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * Save guards.<BR><BR>
	 */
	private void saveSiegeGuard(Location loc, int npcId, int isHire)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("Insert Into siege_guards (unitId, npcId, x, y, z, heading, respawnDelay, isHired) Values (?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setInt(1, getSiegeUnit().getId());
			statement.setInt(2, npcId);
			statement.setInt(3, loc.x);
			statement.setInt(4, loc.y);
			statement.setInt(5, loc.z);
			statement.setInt(6, loc.h);
			if(isHire == 1)
				statement.setInt(7, 0);
			else
				statement.setInt(7, 600);
			statement.setInt(8, isHire);
			statement.execute();
		}
		catch(Exception e1)
		{
			_log.warning("Error adding siege guard for unit " + getSiegeUnit().getName() + ":" + e1);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public Residence getSiegeUnit()
	{
		return _siegeUnit;
	}

	public GArray<L2Spawn> getSiegeGuardSpawn()
	{
		return _siegeGuardSpawn;
	}
}