package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Balance extends L2Skill
{
	public Balance(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		double summaryCurrentHp = 0;
		int summaryMaximumHp = 0;

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || target.isHealBlocked(false, true))
					continue;
				summaryCurrentHp += target.getCurrentHp();
				summaryMaximumHp += target.getMaxHp();
			}

		double percent = summaryCurrentHp / summaryMaximumHp;

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || target.isHealBlocked(false, true))
					continue;
				target.setCurrentHp(Math.max(0, target.getMaxHp() * percent), false);
				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}
