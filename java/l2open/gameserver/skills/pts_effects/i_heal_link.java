package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.FuncPTS;

/**
 * {i_heal_link;30;per;3}
 * @i_heal_link
 * 30 - значение хила
 * per - процентное изменение ХП или diff статическое.
 * 3 - на сколько едениц уменьшать силу хила для следующей цели.
 **/
/**
 * @author : Diagod
 **/
public class i_heal_link extends L2Effect
{
	public i_heal_link(Env env, EffectTemplate template, Integer val1, FuncPTS func, Integer val2)
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
