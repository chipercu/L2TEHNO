package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_target_me}
 * @p_target_me
 **/
/**
 * @author : Diagod
 **/
public class p_target_me extends L2Effect
{
	public p_target_me(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isPlayable())
			((L2PlayableAI) _effected.getAI()).lockTarget(_effector);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected.isPlayable())
			((L2PlayableAI) _effected.getAI()).lockTarget(null);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}