package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.skills.Env;

public class ConditionPlayerMaxPK extends Condition
{
	private final int _pk;

	public ConditionPlayerMaxPK(int pk)
	{
		_pk = pk;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.isPlayer())
			return ((L2Player) env.character).getPkKills() <= _pk;
		return false;
	}
}