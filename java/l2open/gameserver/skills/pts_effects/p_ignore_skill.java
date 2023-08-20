package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2WorldRegion;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;


public final class p_ignore_skill extends L2Effect
{
	Integer[] value;
	public p_ignore_skill(Env env, EffectTemplate template)
	{
		super(env, template);

		value = new Integer[template._effect_param.length];
		for(int i = 0; i < template._effect_param.length; i++)
			value[i] = Integer.parseInt(template._effect_param[i]);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().addBlockSkill(value);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().removeBlockSkill(value);
	}
}