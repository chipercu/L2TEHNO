package ai.DenOfEvil;

import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_nest_wizard extends ai_nest_wizard_basic
{
	private L2NpcInstance myself = null;

	public ai_nest_wizard(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int SelfRangeHeal = 4613;
	public int SelfRangeHealProb = 1000;

	@Override
	protected void onEvtClanAttacked(L2Character victim, L2Character attacker, int damage)
	{
		super.onEvtClanAttacked(victim, attacker, damage);
		if(Rnd.get(10000) < SelfRangeHealProb)
		{
			if(Skill_GetConsumeMP(SelfRangeHeal) < myself.getCurrentMp() && Skill_GetConsumeHP(SelfRangeHeal) < myself.getCurrentHp() && Skill_InReuseDelay(SelfRangeHeal) == 0)
			{
				AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SelfRangeHeal, 9), 1);
			}
		}
	}
}
