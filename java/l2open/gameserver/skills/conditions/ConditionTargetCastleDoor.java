package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.skills.Env;

public class ConditionTargetCastleDoor extends Condition
{
	private final boolean _isCastleDoor;

	public ConditionTargetCastleDoor(boolean isCastleDoor)
	{
		_isCastleDoor = isCastleDoor;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target instanceof L2DoorInstance == _isCastleDoor;
	}
}
