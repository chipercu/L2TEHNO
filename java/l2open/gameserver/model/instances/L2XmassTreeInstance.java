package l2open.gameserver.model.instances;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2XmassTreeInstance extends L2NpcInstance
{
	public L2XmassTreeInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return false;
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}

	@Override
	public boolean hasRandomWalk()
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