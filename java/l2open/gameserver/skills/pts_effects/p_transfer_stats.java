package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

public class p_transfer_stats extends L2Effect
{
	public p_transfer_stats(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effector.getPlayer().p_transfer_stats = true;
		if(_effector.getPet() !=null)
			_effector.getPet().updateAbnormalEffect();
	}		

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effector != null && _effector.getPlayer() != null)
			_effector.getPlayer().p_transfer_stats = false;
		if(_effector != null && _effector.getPet() !=null)
			_effector.getPet().updateAbnormalEffect();
	}	

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}