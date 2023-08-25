package l2open.gameserver.model.entity.soi;

import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.Map;
import java.util.HashMap;

public class HallofSufferingWorld extends World
{
	public L2NpcInstance tumor;
	public int rewardType = -1;
	private HashMap<L2NpcInstance, Boolean> isNpc;
	public L2NpcInstance destroyedTumor;

	public Map<L2NpcInstance, Boolean> getNpcList()
	{
		return isNpc;
	}

	public void createNpcList()
	{
		isNpc = new HashMap<L2NpcInstance, Boolean>();
	}

	public HallofSufferingWorld()
	{
		super();
	}

	public void add(L2NpcInstance npc, boolean bool)
	{
		isNpc.put(npc, bool);
	}
}