package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2HotSpringSquashInstance extends L2MonsterInstance
{
	public L2HotSpringSquashInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		RainbowSpringSiege.getInstance().onDieSquash(this);
		super.doDie(killer);
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
