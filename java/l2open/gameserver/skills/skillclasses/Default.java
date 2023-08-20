package l2open.gameserver.skills.skillclasses;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Default extends L2Skill
{
	public Default(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		//activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Default.NotImplemented", activeChar).addNumber(getId()).addString("" + getSkillType()));
		activeChar.sendActionFailed();
	}
}
