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

public class ai_nest_warrior_buff extends ai_nest_warrior_basic
{
	private L2NpcInstance myself = null;

	public ai_nest_warrior_buff(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int Buff = 4028;
	public int BuffDelay = 10;
	public int TIMER_BUFF_DELAY = 33113;

	@Override
	protected void onEvtSpawn()
	{
		myself.i_ai5 = 0;
		super.onEvtSpawn();
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		if(GetAbnormalLevel(myself, Skill_GetAbnormalType(SkillTable.getInstance().getInfo(Buff,2))) <= 0)
		{
			if(Skill_GetConsumeMP(Buff) < myself.getCurrentMp() && Skill_GetConsumeHP(Buff) < myself.getCurrentHp() - damage && Skill_InReuseDelay(Buff) == 0)
			{
				AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(Buff, 2), 1);
			}
		}
	}

	@Override
	protected void onEvtClanAttacked(L2Character victim, L2Character attacker, int damage)
	{
		super.onEvtClanAttacked(victim, attacker, damage);
		if(myself.i_ai5 == 0)
		{
			if(victim.getCurrentHp() < (victim.getMaxHp() * 0.500000))
			{
				if(GetAbnormalLevel(victim, Skill_GetAbnormalType(SkillTable.getInstance().getInfo(Buff,2))) <= 0)
				{
					if(Skill_GetConsumeMP(Buff) < myself.getCurrentMp() && Skill_GetConsumeHP(Buff) < myself.getCurrentHp() && Skill_InReuseDelay(Buff) == 0)
					{
						AddUseSkillDesire(victim, SkillTable.getInstance().getInfo(Buff, 2), 1);
					}
					myself.i_ai5 = 1;
					myself.AddTimerEx(TIMER_BUFF_DELAY, (BuffDelay * 1000));
				}
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER_BUFF_DELAY)
		{
			myself.i_ai5 = 0;
		}
	}
}
