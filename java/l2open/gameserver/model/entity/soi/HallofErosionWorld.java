package l2open.gameserver.model.entity.soi;

import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


public class HallofErosionWorld extends World
{
	public boolean raidboss_spawned = false;
	private HashMap<L2NpcInstance, Boolean> isNpc;
	public int[] mark_cohemenes;
	public boolean raidboss_dead = false;
	public L2NpcInstance raidboss;
	public ScheduledFuture<?> remainingTimeTask;

	public void createNpcList()
	{
		isNpc = new HashMap<L2NpcInstance, Boolean>();
	}

	public void add(L2NpcInstance npc, boolean bool)
	{
		isNpc.put(npc, bool);
	}

	public HallofErosionWorld()
	{
		super();
	}

	public Map<L2NpcInstance, Boolean> getNpcList()
	{
		return isNpc;
	}
}