package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.FinishRotating;
import l2open.gameserver.serverpackets.StartRotating;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_block_controll}
 **/
/**
 * @author : Diagod
 **/
public class p_block_controll extends L2Effect
{
	public p_block_controll(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startFear();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().stopFear();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}