package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeSpawn;
import l2open.gameserver.model.entity.siege.fortress.FortressSiege;
import l2open.gameserver.model.instances.L2FortEnvoyInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2StaticObjectInstance;
import l2open.gameserver.model.instances.L2SupportUnitInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.StaticObjectsTable;
import l2open.util.GArray;
import l2open.util.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class FortressSiegeManager extends SiegeManager
{
	protected static Logger _log = Logger.getLogger(CastleSiegeManager.class.getName());

	private static FastMap<Integer, GArray<L2NpcInstance>> _envoyNpcsList;
	private static FastMap<Integer, GArray<L2NpcInstance>> _commanderNpcsList;
	private static FastMap<Integer, GArray<SiegeSpawn>> _commanderSpawnList;
	private static FastMap<Integer, GArray<SiegeSpawn>> _flagList;
	private static FastMap<Integer, GArray<SiegeSpawn>> _flagPoleSpawnList;
	private static FastMap<Integer, FastMap<Integer, Integer>> _guardDoorList;
	private static FastMap<Integer, GArray<Integer>> _commandCenterDoorList;

	private static int _defenderRespawnDelay = 20000;
	private static int _siegeClanMinLevel = 4;
	private static int _siegeLength = 60;

	public static void load()
	{
		try
		{
			String path = "./config/siege_fortress.properties";
			if (Boolean.parseBoolean(System.getenv("DEVELOP"))){
				path = "java/config/siege_fortress.properties";
			}
			InputStream is = Files.newInputStream(new File(path).toPath());
			Properties siegeSettings = new Properties();
			siegeSettings.load(is);
			is.close();

			// Siege spawns settings
			_envoyNpcsList = new FastMap<Integer, GArray<L2NpcInstance>>().setShared(true);
			_commanderNpcsList = new FastMap<Integer, GArray<L2NpcInstance>>().setShared(true);
			_commanderSpawnList = new FastMap<Integer, GArray<SiegeSpawn>>().setShared(true);
			_flagList = new FastMap<Integer, GArray<SiegeSpawn>>().setShared(true);
			_flagPoleSpawnList = new FastMap<Integer, GArray<SiegeSpawn>>().setShared(true);
			_guardDoorList = new FastMap<Integer, FastMap<Integer, Integer>>().setShared(true);
			_commandCenterDoorList = new FastMap<Integer, GArray<Integer>>().setShared(true);

			for(Fortress fortress : FortressManager.getInstance().getFortresses().values())
			{
				GArray<L2NpcInstance> _envoyNpcs = new GArray<L2NpcInstance>();
				GArray<L2NpcInstance> _commanderNpcs = new GArray<L2NpcInstance>();
				GArray<SiegeSpawn> _commanderSpawns = new GArray<SiegeSpawn>();
				GArray<SiegeSpawn> _flagSpawns = new GArray<SiegeSpawn>();
				GArray<SiegeSpawn> _flagPoleSpawns = new GArray<SiegeSpawn>();
				FastMap<Integer, Integer> _guardDoors = new FastMap<Integer, Integer>().setShared(true);
				GArray<Integer> _commandCenterDoors = new GArray<Integer>();

				for(int i = 1; i < 0xFF; i++)
				{
					// castleId;npcId;x;y;z;h
					// N115Envoy1=2;36441;11471;95305;-3270;16384
					String _spawnParams = siegeSettings.getProperty("N" + fortress.getId() + "Envoy" + i, "");

					if(_spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ";");

					try
					{
						int castle_id = Integer.parseInt(st.nextToken());
						int npc_id = Integer.parseInt(st.nextToken());
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));

						L2NpcInstance envoyNpc = new L2FortEnvoyInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(npc_id), castle_id);
						envoyNpc.setCurrentHpMp(envoyNpc.getMaxHp(), envoyNpc.getMaxMp(), true);
						envoyNpc.setXYZInvisible(loc.correctGeoZ());
						envoyNpc.setSpawnedLoc(envoyNpc.getLoc());
						envoyNpc.setHeading(loc.h);
						_envoyNpcs.add(envoyNpc);
					}
					catch(Exception e)
					{
						_log.warning("Error while loading envoy(s) for " + fortress.getName());
					}
				}

				for(int i = 1; i < 0xFF; i++)
				{
					String guardDoor = siegeSettings.getProperty("N" + fortress.getId() + "GuardDoor" + i, "");

					if(guardDoor.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(guardDoor.trim(), ";");
					_guardDoors.put(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
				}

				for(int i = 1; i < 0xFF; i++)
				{
					String commandCenter = siegeSettings.getProperty("N" + fortress.getId() + "CommandCenterDoor" + i, "");

					if(commandCenter.length() == 0)
						break;

					_commandCenterDoors.add(Integer.parseInt(commandCenter));
				}

				for(int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty("N" + fortress.getId() + "Commander" + i, "");

					if(_spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

					try
					{
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
						int npc1_id = Integer.parseInt(st.nextToken());
						int npc2_id = Integer.parseInt(st.nextToken());

						L2NpcInstance commanderNpc = new L2SupportUnitInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(npc1_id));
						commanderNpc.setCurrentHpMp(commanderNpc.getMaxHp(), commanderNpc.getMaxMp(), true);
						commanderNpc.setXYZInvisible(loc.correctGeoZ());
						commanderNpc.setSpawnedLoc(commanderNpc.getLoc());
						commanderNpc.setHeading(loc.h);
						commanderNpc.spawnMe();

						_commanderNpcs.add(commanderNpc);
						_commanderSpawns.add(new SiegeSpawn(fortress.getId(), loc, npc2_id));
					}
					catch(Exception e)
					{
						_log.warning("Error while loading commander(s) for " + fortress.getName());
					}
				}

				for(int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty("N" + fortress.getId() + "Flag" + i, "");

					if(_spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

					try
					{
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
						int flag_id = Integer.parseInt(st.nextToken());

						_flagSpawns.add(new SiegeSpawn(fortress.getId(), loc, flag_id));
					}
					catch(Exception e)
					{
						_log.warning("Error while loading control flag(s) for " + fortress.getName());
					}
				}

				for(int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty("N" + fortress.getId() + "FlagPole" + i, "");

					if(_spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

					try
					{
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
						int npc_id = Integer.parseInt(st.nextToken());

						_flagPoleSpawns.add(new SiegeSpawn(fortress.getId(), loc, npc_id));
					}
					catch(Exception e)
					{
						_log.warning("Error while loading FlagPole(s) for " + fortress.getName());
					}
				}

				_envoyNpcsList.put(fortress.getId(), _envoyNpcs);
				_commanderNpcsList.put(fortress.getId(), _commanderNpcs);
				_commanderSpawnList.put(fortress.getId(), _commanderSpawns);
				_flagList.put(fortress.getId(), _flagSpawns);
				_flagPoleSpawnList.put(fortress.getId(), _flagPoleSpawns);
				_guardDoorList.put(fortress.getId(), _guardDoors);
				_commandCenterDoorList.put(fortress.getId(), _commandCenterDoors);

				if(_commanderSpawns.size() > 3)
					fortress.setFortType(1);

				if(_envoyNpcs.isEmpty())
					_log.warning("Not found envoy Npc`s for " + fortress.getName());
				if(_commanderNpcs.isEmpty())
					_log.warning("Not found commander Npc`s for " + fortress.getName());
				if(_commanderSpawns.isEmpty())
					_log.warning("Not found commanders for " + fortress.getName());
				if(_flagSpawns.isEmpty())
					_log.warning("Not found control flags for " + fortress.getName());
				if(_flagPoleSpawns.isEmpty())
					_log.warning("Not found flagpole for " + fortress.getName());
				if(_guardDoors.isEmpty())
					_log.warning("Not found guard doors for " + fortress.getName());
				if(_commandCenterDoors.isEmpty())
					_log.warning("Not found command center doors for " + fortress.getName());

				fortress.getSiege().setDefenderRespawnDelay(_defenderRespawnDelay);
				fortress.getSiege().setSiegeClanMinLevel(_siegeClanMinLevel);
				fortress.getSiege().setSiegeLength(_siegeLength);

				spawnFlagPoles(fortress);

				fortress.getSiege().getZone().setActive(false);
				fortress.getSiege().startAutoTask(true);
			}
		}
		catch(Exception e)
		{
			System.err.println("Error while loading siege data.");
			e.printStackTrace();
		}
	}

	public static GArray<L2NpcInstance> getEnvoyNpcsList(int siegeUnitId)
	{
		return _envoyNpcsList.get(siegeUnitId);
	}

	public static GArray<L2NpcInstance> getCommanderNpcsList(int siegeUnitId)
	{
		return _commanderNpcsList.get(siegeUnitId);
	}

	public static GArray<SiegeSpawn> getCommanderSpawnList(int siegeUnitId)
	{
		return _commanderSpawnList.get(siegeUnitId);
	}

	public static GArray<SiegeSpawn> getFlagsList(int siegeUnitId)
	{
		return _flagList.get(siegeUnitId);
	}

	public static FastMap<Integer, Integer> getGuardDoors(int siegeUnitId)
	{
		return _guardDoorList.get(siegeUnitId);
	}

	public static GArray<Integer> getCommandCenterDoors(int siegeUnitId)
	{
		return _commandCenterDoorList.get(siegeUnitId);
	}

	public static boolean isCombatFlag(int itemId)
	{
		return itemId == 9819;
	}

	public static boolean checkIfCanPickup(L2Player player)
	{
		if(player.isCombatFlagEquipped())
		{
			player.sendMessage("You already have the combat flag");
			return false;
		}

		L2Clan clan = player.getClan();
		Siege siege = getSiege(player);

		if(siege == null || clan == null || clan.getSiege() != siege || !clan.isAttacker())
		{
			player.sendMessage(player.isLangRus() ? "Вы должны быть в атакующем клане, чтобы поднять Флаг" : "You must be in attacker clan to pickup Combat Flag");
			return false;
		}

		return true;
	}

	public static FortressSiege getSiege(L2Object activeObject)
	{
		return getSiege(activeObject.getX(), activeObject.getY());
	}

	public static FortressSiege getSiege(int x, int y)
	{
		for(Fortress fortress : FortressManager.getInstance().getFortresses().values())
			if(fortress.getSiege().checkIfInZone(x, y, true))
				return fortress.getSiege();
		return null;
	}

	public static void spawnFlagPoles(Fortress fortress)
	{
		for(SiegeSpawn sp : _flagPoleSpawnList.get(fortress.getId()))
		{
			String line = "FlagPole;" + sp.getNpcId() + ";" + sp.getLoc().x + ";" + sp.getLoc().y + ";" + sp.getLoc().z + ";3;none;0;0";
			L2StaticObjectInstance flagPole = StaticObjectsTable.parse(line);
			fortress.getSiege().addFlagPole(flagPole);
		}
	}

	public static int getSiegeClanMinLevel()
	{
		return _siegeClanMinLevel;
	}

	public static int getSiegeLength()
	{
		return _siegeLength;
	}
}