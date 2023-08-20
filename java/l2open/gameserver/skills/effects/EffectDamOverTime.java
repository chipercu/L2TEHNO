package l2open.gameserver.skills.effects;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

public class EffectDamOverTime extends L2Effect
{
	public EffectDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead() && (!_effected.isBlessedByNoblesse() && !_effected._blessed || getSkill().isToggle()))
			return false;
		else if(!_effected.isDead() && !_effected.block_hp.get())
		{
			double damage = calc();

			if(!getSkill().isToggle())
				damage = _effector.calcStat(getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, _effected, getSkill());

			if(damage > _effected.getCurrentHp() - 1 && !_effected.isNpc())
			{
				if(!getSkill().isOffensive())
					_effected.sendPacket(Msg.NOT_ENOUGH_HP);
				return false;
			}

			if(getSkill().getAbsorbPart() > 0 && !_effected.isDoor() && !_effector.isHealBlocked(false, true))
				_effector.setCurrentHp(getSkill().getAbsorbPart() * Math.min(_effected.getCurrentHp(), damage) + _effector.getCurrentHp(), false);

			if(_effected.isInvisible() && _effected.getEffectList().getEffectByType(EffectType.p_hide) != null)
				_effected.getEffectList().stopAllSkillEffects(EffectType.p_hide);

			_effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || getSkill().isToggle() || _effected == _effector, false, true, damage, true, false, false, false);
		}
		return true;
	}
}