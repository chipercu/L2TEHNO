package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;

public class ConditionPlayerHasBuff extends Condition
{
	private final EffectType _effectType;
	private final int _level;

	public ConditionPlayerHasBuff(EffectType effectType, int level)
	{
		_effectType = effectType;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character character = env.character;
		if(character == null)
			return false;
		L2Effect effect = character.getEffectList().getEffectByType(_effectType);
		if(effect == null)
			return false;
		if(_level == -1 || effect.getSkill().getLevel() >= _level)
			return true;
		return false;
	}
}