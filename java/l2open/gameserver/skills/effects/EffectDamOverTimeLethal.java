package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Stats;

public class EffectDamOverTimeLethal extends L2Effect
{
	public EffectDamOverTimeLethal(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead())
			return false;

		double damage = calc();

		if(getSkill().isOffensive())
			damage *= 2;

		if(!getSkill().isToggle())
			damage = _effector.calcStat(getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, _effected, getSkill());

		_effected.reduceCurrentHp(damage, _effected.isPlayer() ? _effected : _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || getSkill().isToggle() || _effected == _effector, false, true, damage, true, false, false, false);

		Formulas.calcLethalHit(getEffector(), getEffected(), this.getSkill());

		return true;
	}
}