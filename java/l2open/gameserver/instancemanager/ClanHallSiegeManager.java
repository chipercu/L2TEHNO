package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.siege.SiegeSpawn;
import l2open.gameserver.model.entity.siege.clanhall.ClanHallSiege;
import l2open.gameserver.model.instances.L2ClanHallMessengerInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.GArray;
import l2open.util.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class ClanHallSiegeManager extends SiegeManager
{
	protected static Logger _log = Logger.getLogger(CastleSiegeManager.class.getName());

	private static FastMap<Integer, GArray<SiegeSpawn>> _siegeBossSpawnList;
	private static FastMap<Integer, GArray<L2ClanHallMessengerInstance>> _messengersList;

	private static int _defenderRespawnDelay = 20000;
	private static int _siegeClanMinLevel = 4;
	private static int _siegeLength = 60;

	public static void load()
	{
		try
		{
			InputStream is = new FileInputStream(new File("./config/siege_clanhall.properties"));
			Properties siegeSettings = new Properties();
			siegeSettings.load(is);
			is.close();

			// Siege spawns settings
			_siegeBossSpawnList = new FastMap<Integer, GArray<SiegeSpawn>>().setShared(true);
			_messengersList = new FastMap<Integer, GArray<L2ClanHallMessengerInstance>>().setShared(true);

			for(ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values())
			{
				if(clanhall.getSiege() == null)
					continue;

				GArray<SiegeSpawn> _siegeBossSpawns = new GArray<SiegeSpawn>();
				GArray<L2ClanHallMessengerInstance> _messengers = new GArray<L2ClanHallMessengerInstance>();

				for(int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty("N" + clanhall.getId() + "SiegeBoss" + i, "");

					if(_spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

					try
					{
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
						int npc_id = Integer.parseInt(st.nextToken());

						_siegeBossSpawns.add(new SiegeSpawn(clanhall.getId(), loc, npc_id));
					}
					catch(Exception e)
					{
						_log.warning("Error while loading Siege Boss for " + clanhall.getName());
					}
				}

				for(int i = 1; i < 0xFF; i++)
				{
					String _spawnParams = siegeSettings.getProperty("N" + clanhall.getId() + "Messenger" + i, "");

					if(_spawnParams.length() == 0)
						break;

					StringTokenizer st = new StringTokenizer(_spawnParams.trim(), ",");

					try
					{
						Location loc = new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
						loc.setH(Integer.parseInt(st.nextToken()));
						int npc_id = Integer.parseInt(st.nextToken());

						L2ClanHallMessengerInstance messenger = new L2ClanHallMessengerInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(npc_id));
						messenger.setCurrentHpMp(messenger.getMaxHp(), messenger.getMaxMp(), true);
						messenger.setXYZInvisible(loc.correctGeoZ());
						messenger.setSpawnedLoc(messenger.getLoc());
						messenger.setHeading(loc.h);
						messenger.spawnMe();

						_messengers.add(messenger);
					}
					catch(Exception e)
					{
						_log.warning("Error while loading Messenger(s) for " + clanhall.getName());
					}
				}

				_siegeBossSpawnList.put(clanhall.getId(), _siegeBossSpawns);
				_messengersList.put(clanhall.getId(), _messengers);

				if(_siegeBossSpawns.isEmpty())
					_log.warning("Not found Siege Boss for " + clanhall.getName() + ", id " + clanhall.getId());
				if(_messengersList.isEmpty())
					_log.warning("Not found Messengers for " + clanhall.getName());

				clanhall.getSiege().setDefenderRespawnDelay(_defenderRespawnDelay);
				clanhall.getSiege().setSiegeClanMinLevel(_siegeClanMinLevel);
				clanhall.getSiege().setSiegeLength(_siegeLength);

				if(clanhall.getSiege().getZone() != null)
					clanhall.getSiege().getZone().setActive(false);
				else
					_log.warning("Not found Zone for " + clanhall.getName() + ", id " + clanhall.getId());

				clanhall.getSiege().startAutoTask(true);
			}
		}
		catch(Exception e)
		{
			System.err.println("Error while loading siege data.");
		}
	}

	public static GArray<SiegeSpawn> getSiegeBossSpawnList(int siegeUnitId)
	{
		return _siegeBossSpawnList.get(siegeUnitId);
	}

	public static ClanHallSiege getSiege(L2Object activeObject)
	{
		return getSiege(activeObject.getX(), activeObject.getY());
	}

	public static ClanHallSiege getSiege(int x, int y)
	{
		for(ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values())
			if(clanhall.getSiege() != null && clanhall.getSiege().checkIfInZone(x, y, true))
				return clanhall.getSiege();
		return null;
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