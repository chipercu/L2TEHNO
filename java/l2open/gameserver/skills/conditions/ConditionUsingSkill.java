package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.skills.Env;

public final class ConditionUsingSkill extends Condition
{
	private final int _skillId;
	private final SkillType _skillType;

	public ConditionUsingSkill(int skillId)
	{
		_skillId = skillId;
		_skillType = null;
	}

	public ConditionUsingSkill(String skilltype)
	{
		_skillId = -1;
		_skillType = SkillType.valueOf(skilltype);
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.skill == null)
			return false;
		return env.skill.getId() == _skillId || env.skill.getSkillType() == _skillType;
	}
}