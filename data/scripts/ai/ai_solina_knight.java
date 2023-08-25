package ai;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

/**
 * @author: Diagod
 * Solina Knight Captain : 18910
 */
public class ai_solina_knight extends Fighter
{
	private L2Character myself = null;

	public ai_solina_knight(L2Character self)
	{
		super(self);
		myself = self;
	}

	public int Buff = 6313;
	public int TIMER = 555;

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(Rnd.get(100) < 20 && myself.getCurrentHpPercents() < 50 && myself.i_ai4 == 0)
		{
			myself.i_ai4 = 1;
			if(IsNullCreature(attacker) == 0)
			{
				int count=0;
				for(L2NpcInstance npc : L2World.getAroundNpc(getActor(), 2000, 100))
					if(npc != null && !npc.isDead())
						count++;
				if(count >= 70)
					return;
				CreateOnePrivateEx(1018909,"ai_solina_warrior",0,0,myself.getX(),myself.getY(),myself.getZ(),0,1000,GetIndexFromCreature(attacker),0);
				Say(60013);
				if(Skill_GetConsumeMP(Buff) < myself.getCurrentMp() && Skill_GetConsumeHP(Buff) < myself.getCurrentHp() - damage && Skill_InReuseDelay(Buff) == 0)
				{
					AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(Buff,1),1000000);
				}
			}
		}
		super.ATTACKED( attacker,  damage, skill);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 21140014 && Rnd.get(100) < 10 && myself.i_ai3 == 0)
		{
			switch(Rnd.get(3))
			{
				case 0:
					Say(60014);
					break;
				case 1:
					Say(60015);
					break;
				case 2:
					Say(60016);
					break;
			}
			myself.i_ai3 = 1;
			AddTimerEx(TIMER,(10 * 1000));
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER)
			myself.i_ai3 = 0;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}