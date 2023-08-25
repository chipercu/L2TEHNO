package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

public class EffectHeal extends L2Effect
{
	public EffectHeal(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
		if(_effected.isDead() || _effected.isHealBlocked(true, false) || _effected.block_hp.get())
			return;
		double newHp = calc() * _effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100, _effector, getSkill()) / 100;
		newHp = _effector.calcStat(Stats.HEAL_POWER, newHp, _effected, getSkill());
		double addToHp = Math.max(0, newHp);
		if(addToHp > 0)
			addToHp = _effected.setCurrentHp(addToHp + _effected.getCurrentHp(), false);
		_effected.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}