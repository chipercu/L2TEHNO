package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;
import l2open.util.Rnd;

public class ConditionGameChance extends Condition
{
	private final int _chance;

	ConditionGameChance(int chance)
	{
		_chance = chance;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return Rnd.chance(_chance);
	}
}
