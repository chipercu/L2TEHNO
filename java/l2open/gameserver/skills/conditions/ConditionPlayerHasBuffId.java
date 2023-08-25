package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;
import l2open.util.GArray;

public class ConditionPlayerHasBuffId extends Condition
{
	private final int _id;
	private final int _level;

	public ConditionPlayerHasBuffId(int id, int level)
	{
		_id = id;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character character = env.character;
		if(character == null)
			return false;
		if(_level == -1)
			return character.getEffectList().getEffectsBySkillId(_id) != null;
		GArray<L2Effect> el = character.getEffectList().getEffectsBySkillId(_id);
		if(el == null)
			return false;
		for(L2Effect effect : el)
			if(effect != null && effect.getSkill().getLevel() >= _level)
				return true;
		return false;
	}
}