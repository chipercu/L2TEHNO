package l2open.gameserver.model.entity.soi;

import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class HeartOfInfinityWorld extends World
{
	public int boss_respawn_time = 40;
	public boolean successfully = false;
	public int warning = 0;
	public ScheduledFuture<?> remainingTimeTask;
	public int raidbossCount = 0;
	public L2NpcInstance center_tumor;
	public ScheduledFuture<?> bossSpawnTask;

	public void createNpcList()
	{
	}

	public void add(L2NpcInstance npc, boolean bool)
	{
	}

	public HeartOfInfinityWorld()
	{
		super();
	}

	public Map<L2NpcInstance, Boolean> getNpcList()
	{
		return Collections.emptyMap();
	}
}