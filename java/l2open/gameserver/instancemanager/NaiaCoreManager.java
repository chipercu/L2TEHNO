package l2open.gameserver.instancemanager;

import l2open.extensions.scripts.Functions;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.geometry.Polygon;

import java.util.logging.Logger;

public final class NaiaCoreManager
{
	private static final Logger _log = Logger.getLogger(NaiaTowerManager.class.getName());
	private static final NaiaCoreManager _instance = new NaiaCoreManager();
	private static L2Zone _zone;
	private static boolean _active = false;
	private static boolean _bossSpawned = false;
	private static final L2Territory _coreTerritory = new L2Territory(668110).addR(-44789, 246305, -14220, -13800).addR(-44130, 247452, -14220, -13800).addR(-46092, 248606, -14220, -13800).addR(-46790, 247414, -14220, -13800).addR(-46139, 246304, -14220, -13800);
	private static final Location spawnLoc = new Location(-45496, 246744, -14209);

	public static NaiaCoreManager getInstance()
	{
		return _instance;
	}

	public NaiaCoreManager()
	{
		_zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.poison, 875551);
		_log.info("Naia Core Manager: Loaded");
	}

	public static void launchNaiaCore()
	{
		if(isActive())
			return;
		_active = true;
		DoorTable.getInstance().getDoor(18250025).closeMe();
		_zone.setActive(true);
		spawnSpores();
		ThreadPoolManager.getInstance().schedule(new ClearCore(), 14400000);
	}

	private static boolean isActive()
	{
		return _active;
	}

	public static void setZoneActive(boolean value)
	{
		_zone.setActive(value);
	}

	private static void spawnSpores()
	{
		spawnToRoom(25605, 10, _coreTerritory);
		spawnToRoom(25606, 10, _coreTerritory);
		spawnToRoom(25607, 10, _coreTerritory);
		spawnToRoom(25608, 10, _coreTerritory);
	}

	public static void spawnEpidos(int index)
	{
		if (!isActive())
			return;
		if(_bossSpawned)
			return;
		int epidostospawn = 0;
		switch (index)
		{
			case 1:
				epidostospawn = 25609;
				break;
			case 2:
				epidostospawn = 25610;
				break;
			case 3:
				epidostospawn = 25611;
				break;
			case 4:
				epidostospawn = 25612;
				break;
		}

		try
		{
			L2Spawn sp = new L2Spawn(NpcTable.getTemplate(epidostospawn));
			sp.setLoc(spawnLoc);
			sp.doSpawn(true);
			sp.stopRespawn();
			_bossSpawned = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static boolean isBossSpawned()
	{
		return _bossSpawned;
	}

	public static void removeSporesAndSpawnCube()
	{
		int[] spores = { 25605, 25606, 25607, 25608 };

		for (L2NpcInstance spore : L2ObjectsStorage.getAllByNpcId(spores, false))
			spore.deleteMe();
		try
		{
			L2Spawn sp = new L2Spawn(NpcTable.getTemplate(32376));
			sp.setLoc(spawnLoc);
			sp.doSpawn(true);
			sp.stopRespawn();
			Functions.npcShout(sp.getLastSpawn(), "Teleportation to Beleth Throne Room is available for 2 minutes");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void spawnToRoom(int mobId, int count, L2Territory territory)
	{
		for (int i = 0; i < count; i++)
		{
			try
			{
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(mobId));
				sp.setLoc(territory.getRandomPoint(), Rnd.get(65535));
				sp.setRespawnDelay(120, 30);
				sp.setAmount(1);
				sp.doSpawn(true);
				sp.startRespawn();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class ClearCore extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			int[] spores = { 25605, 25606, 25607, 25608 };

			int[] epidoses = { 25609, 25610, 25611, 25612 };

			for (L2NpcInstance spore : L2ObjectsStorage.getAllByNpcId(spores, false))
				spore.deleteMe();
			for (L2NpcInstance epidos : L2ObjectsStorage.getAllByNpcId(epidoses, false))
				epidos.deleteMe();
			setZoneActive(false);
			_active = false;
			DoorTable.getInstance().getDoor(18250025).openMe();
			NaiaCoreManager._zone.setActive(false);
		}
	}
}