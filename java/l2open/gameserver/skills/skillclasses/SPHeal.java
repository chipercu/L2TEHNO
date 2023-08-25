package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class SPHeal extends L2Skill
{
	public SPHeal(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(final L2Character activeChar, final L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				target.getPlayer().addSp((long) _power);
				target.sendChanges();

				getEffects(activeChar, target, getActivateRate() > 0, false);

				activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED__S1S2).addNumber((long) _power).addString("SP"));
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}
