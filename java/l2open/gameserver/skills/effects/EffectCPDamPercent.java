package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

public class EffectCPDamPercent extends L2Effect
{
	public EffectCPDamPercent(final Env env, final EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
		if(_effected.isDead() || _effected.block_hp.get())
			return;
		double newCp = (100 - calc()) * _effected.getMaxCp() / 100;
		newCp = Math.min(_effected.getCurrentCp(), Math.max(0, newCp));
		_effected.setCurrentCp(newCp);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}