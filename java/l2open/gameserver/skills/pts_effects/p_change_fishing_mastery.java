package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
 /**
 * {p_change_fishing_mastery;2;0.9375}
 **/
public class p_change_fishing_mastery extends L2Effect
{
	public p_change_fishing_mastery(Env env, EffectTemplate template, Integer level, Double unk)
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
