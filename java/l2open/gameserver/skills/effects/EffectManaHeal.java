package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

public class EffectManaHeal extends L2Effect
{
	public EffectManaHeal(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
		if(_effected.isDead() || _effected.isHealBlocked(true, false) || _effected.block_mp.get())
			return;
		double mp = calc();
		double newMp = Math.min(mp * 1.7, _effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, mp, _effector, getSkill()));
		if(getSkill().getId() == 1157)
			newMp = getSkill().getPower();
		double addToMp = Math.max(0, newMp);
		if(addToMp > 0)
			addToMp = _effected.setCurrentMp(addToMp + _effected.getCurrentMp());
		_effected.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToMp)));
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}