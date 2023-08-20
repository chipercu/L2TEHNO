package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class VitalityUpdate extends L2Skill
{
	private final double _addPoints;
	private final double _setPoints;

	public VitalityUpdate(StatsSet set)
	{
		super(set);
		_addPoints = set.getDouble("addVitalityPoints", 0);
		_setPoints = set.getDouble("setVitalityPoints", 0);
	}

	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;
		L2Player player = activeChar.getPlayer();
		if(_addPoints != 0)
			player.setVitality(player.getVitality() + _addPoints);
		if(_setPoints >= 0)
			player.setVitality(_setPoints);
	}
}
