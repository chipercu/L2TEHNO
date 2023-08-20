package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_block_debuff}
 **/
public class p_block_debuff extends L2Effect
{
	public p_block_debuff(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().p_block_debuff.getAndSet(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().p_block_debuff.setAndGet(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}