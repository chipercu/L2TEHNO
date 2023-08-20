package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class CPDam extends L2Skill
{
	public CPDam(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
		if(ss)
			activeChar.unChargeShots(false);

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead())
					continue;

				target.doCounterAttack(this, activeChar);

				if(target.checkReflectSkill(activeChar, this))
					target = activeChar;

				if(target.isCurrentCpZero())
					continue;

				double damage = _power * target.getCurrentCp();

				if(damage < 1)
					damage = 1;

				target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, damage, true, false, false, false);

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
	}
}