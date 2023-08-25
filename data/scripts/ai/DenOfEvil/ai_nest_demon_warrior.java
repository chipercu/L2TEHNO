package ai.DenOfEvil;

import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_nest_demon_warrior extends ai_nest_warrior_basic
{
	private L2NpcInstance myself = null;

	public ai_nest_demon_warrior(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int DeBuff = 6168;
	public int DeBuffProb = 2000;

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		if(Rnd.get(10000) < DeBuffProb)
		{
			if(Skill_GetConsumeMP(DeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(DeBuff) < myself.getCurrentHp() - damage && Skill_InReuseDelay(DeBuff) == 0)
			{
				AddUseSkillDesire(attacker, SkillTable.getInstance().getInfo(DeBuff,9), 1);
			}
		}
	}
}
