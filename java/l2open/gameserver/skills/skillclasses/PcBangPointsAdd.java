package l2open.gameserver.skills.skillclasses;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;


public class PcBangPointsAdd extends L2Skill
{
	public PcBangPointsAdd(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!ConfigValue.AltPcBangPointsEnabled)
			return;
		int points = (int) _power;

		for(L2Character target : targets)
		{
			if(target.isPlayer())
				target.getPlayer().addPcBangPoints(points, false, 2);
			getEffects(activeChar, target, getActivateRate() > 0, false);
		}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}