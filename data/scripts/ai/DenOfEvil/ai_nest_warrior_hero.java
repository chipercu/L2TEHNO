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

public class ai_nest_warrior_hero extends ai_nest_warrior_basic
{
	private L2NpcInstance myself = null;

	public ai_nest_warrior_hero(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public String Privates1 = "";
	public String Privates2 = "";
	public int Buff = 4029;
	public int BuffDelay = 10;
	public int DeBuff = 6168;
	public int DeBuffProb = 3333;
	public int TIMER_BUFF_DELAY = 33113;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(myself.getNpcId() == 22697)
		{
			Privates1="ragna_orc_healer_re:DenOfEvil.ai_nest_healer:1:0sec";
			Privates2="ragna_orc_warrior_re:DenOfEvil.ai_nest_warrior_buff:1:0sec";
		}
		if(myself.IsMyBossAlive() > 0)
		{
		}
		else
		{
			//maker0 = myself.GetMyMaker();
			//if((maker0.maximum_npc - maker0.i_ai0) >= 1)
			//{
				if(Rnd.get(100) < 70)
				{
					myself.CreatePrivates(Privates1);
				}
				else
				{
					myself.CreatePrivates(Privates2);
				}
			//}
		}
		myself.i_ai5 = 0;
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		if(GetAbnormalLevel(myself, Skill_GetAbnormalType(SkillTable.getInstance().getInfo(Buff, 2))) <= 0)
		{
			if(Skill_GetConsumeMP(Buff) < myself.getCurrentMp() && Skill_GetConsumeHP(Buff) < myself.getCurrentHp() - damage && Skill_InReuseDelay(Buff) == 0)
			{
				AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(Buff, 2), 1);
			}
		}
		if(Rnd.get(10000) < DeBuffProb)
		{
			if(Skill_GetConsumeMP(DeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(DeBuff) < myself.getCurrentHp() - damage && Skill_InReuseDelay(DeBuff) == 0)
			{
				AddUseSkillDesire(attacker, SkillTable.getInstance().getInfo(DeBuff, 9), 1);
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
				if(GetAbnormalLevel(victim, Skill_GetAbnormalType(SkillTable.getInstance().getInfo(Buff, 2))) <= 0)
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
