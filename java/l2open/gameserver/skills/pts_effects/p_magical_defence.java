package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill.AddedSkill;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_magical_defence;{all};30;per}
 **/
public class p_magical_defence extends L2Effect
{
	public p_magical_defence(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}