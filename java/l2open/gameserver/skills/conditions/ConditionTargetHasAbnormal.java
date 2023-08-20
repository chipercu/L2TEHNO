package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.skills.Env;

public final class ConditionTargetHasAbnormal extends Condition
{
	private final SkillAbnormalType _abnormal;
	private final int _level;

	public ConditionTargetHasAbnormal(SkillAbnormalType abnormal, int level)
	{
		_abnormal = abnormal;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character target = env.target;
		if(target == null)
			return false;
		L2Effect effect = target.getEffectList().getEffectByStackType(_abnormal);
		if(effect == null)
			return false;
		else if(_level == -1 || effect.getSkill().getLevel() >= _level)
			return true;
		return false;
	}
}
