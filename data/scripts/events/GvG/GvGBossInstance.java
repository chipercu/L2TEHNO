package npc.model;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public final class GvGBossInstance extends L2MonsterInstance
{
	public GvGBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}