package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill.AddedSkill;
import l2open.gameserver.skills.Env;
import l2open.util.GArray;

public class EffectCallSkills extends L2Effect
{
	public EffectCallSkills(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		for(AddedSkill as : getSkill().getAddedSkills())
		{
			GArray<L2Character> targets = new GArray<L2Character>();
			targets.add(getEffected());
			getEffector().callSkill(as.getSkill(), targets, false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}