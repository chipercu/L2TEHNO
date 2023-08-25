package l2open.gameserver.model.entity.soi;

import l2open.gameserver.model.instances.L2NpcInstance;

import java.util.Map;


public abstract class World
{
	public long timer;
	public int instanceId;
	public int status;
	public abstract void createNpcList();

	public abstract Map<L2NpcInstance, Boolean> getNpcList();
	public abstract void add(L2NpcInstance paramL2NpcInstance, boolean paramBoolean);
}