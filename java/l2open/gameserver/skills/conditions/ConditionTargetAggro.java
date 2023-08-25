package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.skills.Env;

public class ConditionTargetAggro extends Condition
{
	private final boolean _isAggro;

	public ConditionTargetAggro(boolean isAggro)
	{
		_isAggro = isAggro;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character target = env.target;
		if(target == null)
			return false;
		if(target.isMonster())
			return ((L2MonsterInstance) target).isAggressive() == _isAggro;
		if(target.isPlayer())
			return ((L2Player) target).getKarma() > 0;
		return false;
	}
}
