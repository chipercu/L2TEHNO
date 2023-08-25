package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.FinishRotating;
import l2open.gameserver.serverpackets.StartRotating;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_party_buff}
 **/
/**
 * @author : Diagod
 **/
public class p_party_buff extends L2Effect
{
	public p_party_buff(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.p_party_buff.getAndSet(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.p_party_buff.setAndGet(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}