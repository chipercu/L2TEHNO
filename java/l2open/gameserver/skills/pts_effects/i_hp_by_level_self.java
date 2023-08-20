package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_hp_by_level_self;63}
 * @i_hp_by_level_self
 * @63 - Количество добавляемого ХП.
 **/
/**
 * @author : Diagod
 **/
public class i_hp_by_level_self extends L2Effect
{
	private double _hp;

	public i_hp_by_level_self(Env env, EffectTemplate template, Double hp)
	{
		super(env, template);

		_hp = hp;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effector.isDead() || _effector.isHealBlocked(true, false) || _effector.block_hp.get())
			return;

		_hp = Math.max(0, Math.min(_hp, _effector.calcStat(Stats.p_limit_hp, null, null) * _effector.getMaxHp() / 100. - _effector.getCurrentHp()));
		_effector.setCurrentHp(_effector.getCurrentHp() + _hp, false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}