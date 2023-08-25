package l2open.gameserver.skills.skillclasses;

import l2open.config.*;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class DeathPenalty extends L2Skill
{
	public DeathPenalty(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(final L2Character activeChar, final L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		// Chaotic characters can't use scrolls of recovery
		if(activeChar.getKarma() > 0 && !ConfigValue.ChaoticCanUseScrollOfRecovery)
		{
			activeChar.sendActionFailed();
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				if(!target.isPlayer())
					continue;
				((L2Player) target).getDeathPenalty().reduceLevel();
			}
	}
}