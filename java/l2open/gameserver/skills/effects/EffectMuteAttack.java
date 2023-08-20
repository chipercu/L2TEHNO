package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

public class EffectMuteAttack extends L2Effect
{
	public EffectMuteAttack(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startAMuted();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopAMuted();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}