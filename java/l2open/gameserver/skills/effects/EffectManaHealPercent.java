package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

public class EffectManaHealPercent extends L2Effect
{
	public EffectManaHealPercent(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
		if(_effected.isDead() || _effected.isHealBlocked(true, false) || _effected.block_mp.get())
			return;
		double newMp = calc() * _effected.getMaxMp() / 100;
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