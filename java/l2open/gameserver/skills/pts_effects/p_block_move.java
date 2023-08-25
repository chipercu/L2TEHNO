package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.FinishRotating;
import l2open.gameserver.serverpackets.StartRotating;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_block_move}
 **/
/**
 * @author : Diagod
 **/
public class p_block_move extends L2Effect
{
	public p_block_move(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.p_block_move(true, getSkill());
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.p_block_move(false, getSkill());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}