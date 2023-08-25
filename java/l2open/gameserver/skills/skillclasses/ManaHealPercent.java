package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class ManaHealPercent extends L2Skill
{
	public ManaHealPercent(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || target.isHealBlocked(true, false) || target.block_mp.get())
					continue;

				getEffects(activeChar, target, getActivateRate() > 0, false);

				double addToMp = Math.max(0, target.getMaxMp() * _power / 100);

				if(addToMp > 0)
					addToMp = target.setCurrentMp(target.getCurrentMp() + addToMp);
				if(target.isPlayer())
					if(activeChar != target)
						target.sendPacket(new SystemMessage(SystemMessage.XS2S_MP_HAS_BEEN_RESTORED_BY_S1).addString(activeChar.getName()).addNumber(Math.round(addToMp)));
					else
						activeChar.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToMp)));
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}