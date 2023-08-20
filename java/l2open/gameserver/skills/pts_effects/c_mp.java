package l2open.gameserver.skills.pts_effects;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {c_mp;-63;5}
 * @c_mp
 * @-63 - Количество МП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class c_mp extends L2Effect
{
	private double _mp_tick;

	public c_mp(Env env, EffectTemplate template, Double mp_tick, Integer tick_time)
	{
		super(env, template);

		_mp_tick = mp_tick;
		_tick_time = tick_time;
		isDot = true;
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead() && (!_effected.isBlessedByNoblesse() && !_effected._blessed || getSkill().isToggle()))
		{
			_effected.getEffectList().stopEffect(getSkill().getId());
			return false;
		}

		double damage = ConfigValue.DotModifer*_tick_time*_mp_tick;

		if(!getSkill().isOffensive())
			if(getSkill().isMagic())
				damage = _effected.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, damage, null, getSkill());
			else
				damage = _effected.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, damage, null, getSkill());

		if(damage*-1 > _effected.getCurrentMp() && getSkill().isToggle())
		{
			_effected.sendPacket(new SystemMessage(SystemMessage.YOUR_SKILL_WAS_REMOVED_DUE_TO_A_LACK_OF_MP));
			_effected.getEffectList().stopEffect(getSkill().getId());
			return false;
		}

		_effected.setCurrentMp(_effected.getCurrentMp() + damage);
		return true;
	}
}