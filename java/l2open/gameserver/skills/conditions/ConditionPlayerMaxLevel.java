package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;

public class ConditionPlayerMaxLevel extends Condition
{
	private final int _level;

	public ConditionPlayerMaxLevel(int level)
	{
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.character.getLevel() <= _level;
	}
}