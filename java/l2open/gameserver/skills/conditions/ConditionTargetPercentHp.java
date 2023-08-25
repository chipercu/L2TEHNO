package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;

public class ConditionTargetPercentHp extends Condition
{
	private final double _hp;

	public ConditionTargetPercentHp(int hp)
	{
		_hp = hp / 100.;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target != null && env.target.getCurrentHpRatio() <= _hp;
	}
}