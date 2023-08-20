package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

public class EffectMuteAll extends L2Effect
{
	public EffectMuteAll(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startMuted();
		_effected.startPMuted();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopMuted();
		_effected.stopPMuted();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}