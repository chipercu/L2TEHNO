package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.FuncPTS;

/**
 * {p_passive}
 * @p_passive
 **/
/**
 * @author : Diagod
 **/
public class p_max_cp extends L2Effect
{
	public p_max_cp(Env env, EffectTemplate template, String cond, int cp_add, FuncPTS func, byte is_heal)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onExit()
	{
		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}