package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill.AddedSkill;
import l2open.gameserver.skills.Env;

public class EffectAddSkills extends L2Effect
{
	public EffectAddSkills(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		for(AddedSkill as : getSkill().getAddedSkills())
			getEffected().addSkill(as.getSkill());
	}

	@Override
	public void onExit()
	{
		super.onExit();
		for(AddedSkill as : getSkill().getAddedSkills())
			getEffected().removeSkill(as.getSkill(), false, false);
		getEffected().updateEffectIcons();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}