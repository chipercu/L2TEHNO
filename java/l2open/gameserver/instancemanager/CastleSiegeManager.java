package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.SiegeSpawn;
import l2open.gameserver.model.instances.L2ArtefactInstance;
import l2open.gameserver.model.instances.L2ControlTowerInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.GArray;
import l2open.util.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class CastleSiegeManager extends SiegeManager
{
	private static Logger _log = Logger.getLogger(CastleSiegeManager.class.getName());

	private static FastMap<Integer, GArray<SiegeSpawn>> _controlTowerSpawnList;
	private static FastMap<Integer, GArray<SiegeSpawn>> _artefactSpawnList;

	private static int _controlTowerLosePenalty = 150000;
	private static int _defenderRespawnDelay = 30000;
	private static int _siegeClanMinLevel = 5;
	private static int _siegeLength = 120;

	public static void load()
	{
		try
		{
			String path = "./config/siege_castle.properties";
			if (Boolean.parseBoolean(System.getenv("DEVELOP"))){
				path = "java/config/siege_castle.properties";
			}
			InputStream is = Files.newInputStream(new File(path).toPath());
			Properties siegeSettings = new Properties();
			siegeSettings.load(is);
			is.close();

			// Siege spawns settings
			_controlTowerLosePenalty = Integer.parseInt(siegeSettings.getProperty("ControlTowerLosePenalty", "150000"));
			_controlTowerSpawnList = new FastMap<Integer, GArray<SiegeSpawn>>().setShared(true);
			_artefactSpawnList = new FastMap<Integer, GArray<SiegeSpawn>>().setShared(true);

			for(Castle castle : CastleManager.getInstance().getCastles().values())
			{
				GArray<SiegeSpawn> controlTowersSpawns = new GArray<SiegeSpawn>();

				for(int i = 1; i < 0xFF; i++)
				{
					String spawnParams = siegeSettings.getProperty(castle.getName() + "ControlTower" + Integer.toString(i), "");

					if(spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(spawnParams.trim(), ",");

					try
					{
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
						int npc_id = Integer.parseInt(st.nextToken());
						int hp = Integer.parseInt(st.nextToken());

						controlTowersSpawns.add(new SiegeSpawn(castle.getId(), loc, npc_id, hp));
					}
					catch(Exception e)
					{
						_log.warning("Error while loading control tower(s) for " + castle.getName() + " castle.");
					}
				}

				GArray<SiegeSpawn> artefactSpawns = new GArray<SiegeSpawn>();

				for(int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty(castle.getName() + "Artefact" + Integer.toString(i), "");

					if(_spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

					try
					{
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
						int npc_id = Integer.parseInt(st.nextToken());

						artefactSpawns.add(new SiegeSpawn(castle.getId(), loc, npc_id));
					}
					catch(Exception e)
					{
						_log.warning("Error while loading artefact(s) for " + castle.getName() + " castle.");
					}
				}

				_controlTowerSpawnList.put(castle.getId(), controlTowersSpawns);
				_artefactSpawnList.put(castle.getId(), artefactSpawns);

				castle.getSiege().setControlTowerLosePenalty(_controlTowerLosePenalty);
				castle.getSiege().setDefenderRespawnDelay(_defenderRespawnDelay);
				castle.getSiege().setSiegeClanMinLevel(_siegeClanMinLevel);
				castle.getSiege().setSiegeLength(_siegeLength);

				spawnArtifacts(castle);
				spawnControlTowers(castle);

				castle.getSiege().getZone().setActive(false);
				castle.getSiege().startAutoTask(true);
			}
		}
		catch(Exception e)
		{
			System.err.println("Error while loading siege data.");
			e.printStackTrace();
		}
	}

	public static GArray<SiegeSpawn> getControlTowerSpawnList(int _castleId)
	{
		if(_controlTowerSpawnList.containsKey(_castleId))
			return _controlTowerSpawnList.get(_castleId);
		return null;
	}

	public static void spawnArtifacts(Castle castle)
	{
		for(SiegeSpawn sp : _artefactSpawnList.get(castle.getId()))
		{
			L2ArtefactInstance art = new L2ArtefactInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(sp.getNpcId()));
			art.setCurrentHpMp(art.getMaxHp(), art.getMaxMp(), true);
			art.setHeading(sp.getLoc().h);
			art.spawnMe(sp.getLoc().changeZ(50));
			castle.getSiege().addArtifact(art);
		}
	}

	public static void spawnControlTowers(Castle castle)
	{
		for(SiegeSpawn sp : getControlTowerSpawnList(castle.getId()))
		{
			L2ControlTowerInstance tower = new L2ControlTowerInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(sp.getNpcId()), castle.getSiege(), sp.getValue());
			tower.setCurrentHpMp(tower.getMaxHp(), tower.getMaxMp(), true);
			tower.setHeading(sp.getLoc().h);
			tower.spawnMe(sp.getLoc());
			castle.getSiege().addControlTower(tower);
		}
	}

	public static int getControlTowerLosePenalty()
	{
		return _controlTowerLosePenalty;
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