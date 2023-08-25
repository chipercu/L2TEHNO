package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

public class EffectHealCPPercent extends L2Effect
{
	public EffectHealCPPercent(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
		if(_effected.isDead() || _effected.isHealBlocked(true, false) || _effected.block_hp.get())
			return;
		double newCp = calc() * _effected.getMaxCp() / 100;
		double addToCp = Math.max(0, newCp);
		if(addToCp > 0)
			addToCp = _effected.setCurrentCp(addToCp + _effected.getCurrentCp());
		_effected.sendPacket(new SystemMessage(SystemMessage.S1_WILL_RESTORE_S2S_CP).addNumber((long) addToCp));
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}