package l2open.util;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2ObjectTasks;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;

public class NpcUtils
{
	public static L2NpcInstance spawnSingle(int npcId, int x, int y, int z)
	{
		return spawnSingle(npcId, new Location(x, y, z, -1), 0, 0L);
	}

	public static L2NpcInstance spawnSingle(int npcId, int x, int y, int z, long despawnTime)
	{
		return spawnSingle(npcId, new Location(x, y, z, -1), 0, despawnTime);
	}

	public static L2NpcInstance spawnSingle(int npcId, int x, int y, int z, int h, long despawnTime)
	{
		return spawnSingle(npcId, new Location(x, y, z, h), 0, despawnTime);
	}

	public static L2NpcInstance spawnSingle(int npcId, Location loc)
	{
		return spawnSingle(npcId, loc, 0, 0L);
	}

	public static L2NpcInstance spawnSingle(int npcId, Location loc, long despawnTime)
	{
		return spawnSingle(npcId, loc, 0, despawnTime);
	}

	/*public static L2NpcInstance spawnSingle(int npcId, Location loc, int reflection)
	{
		return spawnSingle(npcId, loc, reflection, 0L);
	}*/

	public static L2NpcInstance spawnSingle(int npcId, Location loc, int reflection, long despawnTime)
	{
		L2NpcInstance _npc = null;
		try
		{
			L2Spawn sp = new L2Spawn(NpcTable.getTemplate(npcId));
			sp.setLoc(loc);
			L2NpcInstance npc = sp.doSpawn(true);
			npc.setHeading((loc.h < 0) ? Rnd.get(65535) : loc.h);
			npc.setSpawnedLoc(loc);
			npc.setReflection(reflection);
			npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
			_npc = npc;
			if(despawnTime > 0L)
				ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.DeleteTask(npc), despawnTime);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return _npc;
	}
}