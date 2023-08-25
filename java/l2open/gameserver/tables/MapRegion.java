package l2open.gameserver.tables;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.clanhall.BanditStrongholdSiege;
import l2open.gameserver.model.entity.siege.clanhall.WildBeastFarmSiege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class MapRegion
{
	private final static Logger _log = Logger.getLogger(MapRegion.class.getName());

	private static MapRegion _instance;

	private final int[][] _regions = new int[L2World.WORLD_SIZE_X][L2World.WORLD_SIZE_Y];

	public static enum TeleportWhereType
	{
		Castle,
		ClanHall,
		ClosestTown,
		SecondClosestTown,
		Headquarter,
		Fortress
	}

	public static MapRegion getInstance()
	{
		if(_instance == null)
			_instance = new MapRegion();
		return _instance;
	}

	private MapRegion()
	{
		int count = 0;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM mapregion");
			rset = statement.executeQuery();
			while(rset.next())
			{
				int y = rset.getInt("y10_plus");

				for(int i = ConfigValue.GeoFirstX; i <= ConfigValue.GeoLastX; i++)
				{
					int region = rset.getInt("x" + i);
					_regions[i - ConfigValue.GeoFirstX][y] = region;
					count++;
				}
			}

			_log.fine("Loaded " + count + " mapregions.");
		}
		catch(Exception e)
		{
			_log.warning("error while creating map region data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public final int getMapRegion(int posX, int posY)
	{
		int tileX = posX - L2World.MAP_MIN_X >> 15;
		int tileY = posY - L2World.MAP_MIN_Y >> 15;
		return _regions[tileX][tileY];
	}

	public static Location getTeleToCastle(L2Character activeChar)
	{
		return getInstance().getTeleToLocation(activeChar, TeleportWhereType.Castle);
	}

	public static Location getTeleToClanHall(L2Character activeChar)
	{
		return getInstance().getTeleToLocation(activeChar, TeleportWhereType.ClanHall);
	}

	public static Location getTeleToClosestTown(L2Character activeChar)
	{
		return getInstance().getTeleToLocation(activeChar, TeleportWhereType.ClosestTown);
	}

	public static Location getTeleToSecondClosestTown(L2Character activeChar)
	{
		return getInstance().getTeleToLocation(activeChar, TeleportWhereType.SecondClosestTown);
	}

	public static Location getTeleToHeadquarter(L2Character activeChar)
	{
		return getInstance().getTeleToLocation(activeChar, TeleportWhereType.Headquarter);
	}

	public static Location getTeleToFortress(L2Character activeChar)
	{
		return getInstance().getTeleToLocation(activeChar, TeleportWhereType.Fortress);
	}

	public static Location getTeleTo(L2Character activeChar, TeleportWhereType teleportWhere)
	{
		return getInstance().getTeleToLocation(activeChar, teleportWhere);
	}

	public Location getTeleToLocation(L2Character activeChar, TeleportWhereType teleportWhere)
	{
		L2Player player = activeChar.getPlayer();

		if(player != null)
		{
			if(player.getEventMaster() != null)
				player.getEventMaster().onEscape(player);
			Object[] script_args = new Object[] { player };
			for(ScriptClassAndMethod handler : Scripts.onEscape)
				activeChar.callScripts(handler.scriptClass, handler.method, script_args);

			Reflection r = player.getReflection();
			if(r.getId() != 0 && r.getReturnLoc() != null)
				return r.getReturnLoc();

			L2Clan clan = player.getClan();

			if(clan != null)
			{
				// If teleport to clan hall
				if(teleportWhere == TeleportWhereType.ClanHall && clan.getHasHideout() != 0)
					return ClanHallManager.getInstance().getClanHall(clan.getHasHideout()).getZone().getSpawn();

				// If teleport to castle
				if(teleportWhere == TeleportWhereType.Castle && clan.getHasCastle() != 0)
					return CastleManager.getInstance().getCastleByIndex(clan.getHasCastle()).getZone().getSpawn();

				// If teleport to fortress
				if(teleportWhere == TeleportWhereType.Fortress && clan.getHasFortress() != 0)
					return FortressManager.getInstance().getFortressByIndex(clan.getHasFortress()).getZone().getSpawn();

				// Checking if in siege
				Siege siege = SiegeManager.getSiege(activeChar, true);
				if(siege != null)
				{
					if(teleportWhere == TeleportWhereType.Headquarter)
					{
						// Check if player's clan is attacker
						L2NpcInstance flag = siege.getHeadquarter(player.getClan());
						if(flag != null)
							return flag.getLoc().rnd(50, 75, false); // Спаун рядом с флагом
						return TownManager.getInstance().getClosestTown(activeChar).getSpawn();
					}

					// Check if player's clan is defender
					if((teleportWhere == TeleportWhereType.Castle || teleportWhere == TeleportWhereType.Fortress) && siege.getDefenderClan(player.getClan()) != null && siege.getSiegeUnit() != null && siege.getSiegeUnit().getZone() != null)
						return player.getKarma() > 1 ? siege.getSiegeUnit().getZone().getPKSpawn() : siege.getSiegeUnit().getZone().getSpawn();
					return player.getKarma() > 1 ? TownManager.getInstance().getClosestTown(activeChar).getPKSpawn() : TownManager.getInstance().getClosestTown(activeChar).getSpawn();
				}

				if(TerritorySiege.checkIfInZone(activeChar))
				{
					if(teleportWhere == TeleportWhereType.Headquarter)
					{
						L2NpcInstance flag = TerritorySiege.getHeadquarter(player.getClan());
						if(flag != null)
							return flag.getLoc().rnd(100, 125, false); // Спаун вокруг аутпоста
						return TownManager.getInstance().getClosestTown(activeChar).getSpawn();
					}

					return player.getKarma() > 1 ? TownManager.getInstance().getClosestTown(activeChar).getPKSpawn() : TownManager.getInstance().getClosestTown(activeChar).getSpawn();
				}
			}

			// Светлые эльфы не могут воскрешаться в городе темных
			if(player.getRace() == Race.elf && TownManager.getInstance().getClosestTown(activeChar).getTownId() == 3)
				return player.getKarma() > 1 ? TownManager.getInstance().getTown(2).getPKSpawn() : TownManager.getInstance().getTown(2).getSpawn();

			// Темные эльфы не могут воскрешаться в городе светлых
			if(player.getRace() == Race.darkelf && TownManager.getInstance().getClosestTown(activeChar).getTownId() == 2)
				return player.getKarma() > 1 ? TownManager.getInstance().getTown(3).getPKSpawn() : TownManager.getInstance().getTown(3).getSpawn();

			L2Zone battle = activeChar.getZone(ZoneType.battle_zone);

			// If in battle zone
			if(battle != null && battle.getRestartPoints() != null)
				return player.getKarma() > 1 ? battle.getPKSpawn() : battle.getSpawn();

			// If in pease zone
			if(activeChar.isInZone(ZoneType.peace_zone) && activeChar.getZone(ZoneType.peace_zone).getRestartPoints() != null)
				return player.getKarma() > 1 ? activeChar.getZone(ZoneType.peace_zone).getPKSpawn() : activeChar.getZone(ZoneType.peace_zone).getSpawn();

			// If in offshore zone == pease zone options.
			if(activeChar.isInZone(ZoneType.offshore) && activeChar.getZone(ZoneType.offshore).getRestartPoints() != null)
				return player.getKarma() > 1 ? activeChar.getZone(ZoneType.offshore).getPKSpawn() : activeChar.getZone(ZoneType.offshore).getSpawn();
			
			// If in other zone
			if(activeChar.isInZone(ZoneType.other) && activeChar.getZone(ZoneType.other).getRestartPoints() != null)
				return activeChar.getZone(ZoneType.other).getSpawn();

			if(BanditStrongholdSiege.getInstance().isPlayerRegister(player.getClan(), activeChar.getName()))
			{
				L2NpcInstance flag = BanditStrongholdSiege.getInstance().getSiegeFlag(player.getClan());
				if(flag != null)
					return  new Location(flag.getX(), flag.getY(), flag.getZ());
			}

			if(WildBeastFarmSiege.getInstance().isPlayerRegister(player.getClan(), activeChar.getName()))
			{
				L2NpcInstance flag = WildBeastFarmSiege.getInstance().getSiegeFlag(player.getClan());
				if(flag != null)
					return  new Location(flag.getX(), flag.getY(), flag.getZ());
			}

			return player.getKarma() > 1 ? TownManager.getInstance().getClosestTown(activeChar).getPKSpawn() : TownManager.getInstance().getClosestTown(activeChar).getSpawn();
		}

		return TownManager.getInstance().getClosestTown(activeChar).getSpawn();
	}
}