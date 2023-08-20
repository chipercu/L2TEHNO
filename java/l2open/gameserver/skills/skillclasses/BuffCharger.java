package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class BuffCharger extends L2Skill
{
	private int _target;

	public BuffCharger(StatsSet set)
	{
		super(set);
		_target = set.getInteger("targetBuff", 0);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
		{
			int level = 0;
			GArray<L2Effect> el = target.getEffectList().getEffectsBySkillId(_target);
			if(el != null)
				level = el.get(0).getSkill().getLevel();

			L2Skill next = SkillTable.getInstance().getInfo(_target, level + 1);
			if(next != null)
				next.getEffects(activeChar, target, false, false);
		}
	}
}