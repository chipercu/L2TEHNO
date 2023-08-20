package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.skills.Env;

public class ConditionPlayerClassId extends Condition
{
	private final int _class;

	public ConditionPlayerClassId(int id)
	{
		_class = id;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		return ((L2Player) env.character).getActiveClassId() == _class;
	}
}