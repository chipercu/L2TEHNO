package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Disablers extends L2Skill
{
	private final boolean _skillInterrupt;

	public Disablers(StatsSet set)
	{
		super(set);
		_skillInterrupt = set.getBool("skillInterrupt", false);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{

				if(target.checkReflectSkill(activeChar, this))
					target = activeChar;

				if(_skillInterrupt)
				{
					if(target.getCastingSkill() != null && !target.getCastingSkill().isMagic() && !target.isRaid() && !target.isEpicRaid())
						target.abortCast(false);
					if(!target.isRaid() && !target.isEpicRaid())
						target.abortAttack(true, true);
				}

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}