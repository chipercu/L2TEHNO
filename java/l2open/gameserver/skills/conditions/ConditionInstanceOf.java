package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.skills.Env;

public class ConditionInstanceOf extends Condition
{
	private final String _instance_name;
	private final boolean _istarget;

	public ConditionInstanceOf(String instance_name, boolean istarget)
	{
		_instance_name = instance_name;
		_istarget = istarget;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character obj = _istarget ? env.target : env.character;
		if(obj == null)
			return false;

		String target_class = obj.getClass().getSimpleName();
		return target_class.equalsIgnoreCase(_instance_name);
	}
}
