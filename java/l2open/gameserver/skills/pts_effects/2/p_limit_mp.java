package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.*;

/**
 * {p_limit_mp;30;per}
 **/
/**
 * @author : Diagod
 **/
public class p_limit_mp extends L2Effect
{
	private double _val;

	public p_limit_mp(Env env, EffectTemplate template, Double value)
	{
		super(env, template);

		_val = value;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}	