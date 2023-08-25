package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.skills.Env;

public class ConditionAgathion extends Condition
{
	private final int _agathionId;

	public ConditionAgathion(int agathionId)
	{
		_agathionId = agathionId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer() || ((L2Player) env.character).getAgathion() == null)
			return false;
		return ((L2Player) env.character).getAgathion().getId() == _agathionId;
	}
}