package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class RDam extends L2Skill
{
	public RDam(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null && !target.isDead())
			{
				activeChar.sendHDmgMsg(activeChar, target, this, (int)getPower(), false, false);
				target.reduceCurrentHp(getPower(), activeChar, this, true, true, true, true, false, getPower(), true, false, false, false);
				getEffects(activeChar, target, false, false);
			}
	}
}