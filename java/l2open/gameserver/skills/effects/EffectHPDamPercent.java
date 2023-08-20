package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

public class EffectHPDamPercent extends L2Effect
{
	public EffectHPDamPercent(final Env env, final EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		if(_effected.isDead() || _effected.block_hp.get())
			return;

		double newHp = (100. - calc()) * _effected.getMaxHp() / 100.;
		newHp = Math.min(_effected.getCurrentHp(), Math.max(0, newHp));
		_effected.setCurrentHp(newHp, false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}