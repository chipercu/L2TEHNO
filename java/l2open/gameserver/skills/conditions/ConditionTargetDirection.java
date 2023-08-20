package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;
import l2open.util.Util;

public class ConditionTargetDirection extends Condition
{
	private final Util.TargetDirection _dir;

	public ConditionTargetDirection(Util.TargetDirection direction)
	{
		_dir = direction;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return Util.getDirectionTo(env.target, env.character) == _dir;
	}
}
