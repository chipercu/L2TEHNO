package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_passive}
 * @p_passive
 **/
/**
 * @author : Diagod
 **/
public class p_passive extends L2Effect
{
	public p_passive(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc())
			((L2NpcInstance) _effected).setUnAggred(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected.isNpc())
			((L2NpcInstance) _effected).setUnAggred(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}