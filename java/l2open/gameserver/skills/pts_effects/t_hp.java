package l2open.gameserver.skills.pts_effects;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.ExRegenMax;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncPTS;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {t_hp;-63;5;diff}
 * @t_hp
 * @-63 - Количество ХП на Тик...
 * @5 - Время тика(666мс 1 тик)
 * @diff - что делаем, добавляем статик или процент.
 **/
/**
 * @author : Diagod
 **/
public class t_hp extends L2Effect
{
	private double _hp_tick;
	private FuncPTS _act;

	public t_hp(Env env, EffectTemplate template, Double hp_tick, Integer tick_time, FuncPTS act)
	{
		super(env, template);

		_hp_tick = hp_tick;
		_tick_time = tick_time;
		_act = act;
		isDot = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// TODO: Пока на время пускай будет определятся так!!! НО В БЛИЖАЙШЕЕ ВРЕМЯ перецепить на абнормал_тайм со скила!!!
		if(getEffected().isPlayer() && getAbnormalType() == SkillAbnormalType.hp_recover)
			getEffected().sendPacket(new ExRegenMax((int) (getCount() * getPeriod() / 1000), _tick_time, _hp_tick));
		//if(getEffected().isPlayer())
		//	_log.info("t_hp: onStart");
	}

	/*@Override
	public void onExit()
	{
		super.onExit();
		if(getEffected().isPlayer())
			_log.info("t_hp: onExit");
	}*/

	@Override
	public boolean onActionTime()
	{
		double damage = _act.calc(-ConfigValue.DotModifer * _hp_tick * _tick_time, _effected.getMaxHp());

		//if(_effected.isPlayer())
		//	_log.info("t_hp: onActionTime["+damage+"]["+_hp_tick+"]["+_tick_time+"]["+_effected.getMaxHp()+"]");

		if(_effected.block_hp.get())
			return false;
		else if(_effected.isDead() && (!_effected.isBlessedByNoblesse() && !_effected._blessed || getSkill().isToggle() || getAbnormalType() == SkillAbnormalType.hp_recover))
		{
			_effected.getEffectList().stopEffect(getSkill().getId());
			return false;
		}

		if(damage < 2 && damage >= 0)
			damage = 2;

		// Проверки, выполняющиеся только для дамажущих скилов...
		if(damage > 0)
		{
			damage = Math.min(_effected.getCurrentHp() - 1, _effector.calcStat(getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, _effected, getSkill()));

			if(getSkill().getAbsorbPart() > 0 && !_effected.isDoor() && !_effector.isHealBlocked(false, true))
				_effector.setCurrentHp(getSkill().getAbsorbPart() * Math.min(_effected.getCurrentHp(), damage) + _effector.getCurrentHp(), false);

			// Не знаю, должны ли ДОТ хилы выбивать с инвиза...
			// TODO: Проверить это на ПТСке!!!
			if(_effected.isInvisible() && _effected.getEffectList().getEffectByType(EffectType.p_hide) != null)
				_effected.getEffectList().stopAllSkillEffects(EffectType.p_hide);
			_effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || getSkill().isToggle() || _effected == _effector, false, true, damage, false, false, false, false);
		}
		else
			_effected.setCurrentHp(_effected.getCurrentHp()+damage*-1, false);
		return true;
	}
}