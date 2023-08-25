package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;

public class ConditionTargetMobId extends Condition
{
	private final int _mobId;

	public ConditionTargetMobId(int mobId)
	{
		_mobId = mobId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target != null && env.target.getNpcId() == _mobId;
	}
}
