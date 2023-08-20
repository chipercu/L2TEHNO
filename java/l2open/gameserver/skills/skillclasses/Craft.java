package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.RecipeController;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Craft extends L2Skill
{
	private final boolean _dwarven;

	public Craft(StatsSet set)
	{
		super(set);
		_dwarven = set.getBool("isDwarven");
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer() || activeChar.isOutOfControl() || activeChar.getDuel() != null)
			return false;
		return true;
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		RecipeController.getInstance().requestBookOpen((L2Player) activeChar, _dwarven);
	}
}
