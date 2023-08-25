package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для мобов в селмахум
 **/

public class ai_xel_recruit_warrior extends ai_xel_recruit_war
{
	private L2Character myself = null;
	public ai_xel_recruit_warrior(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(Rnd.get(18) < 1)
		{
			myself.i_ai0 = 1;
			myself.AddTimerEx(2019999,1000);
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		L2Character c0;
		if(script_event_arg1 == (10016 + trainer_id))
		{
			c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				if(myself.alive() == 0)
				{
					return;
				}
				//RemoveAllDesire(myself);
				myself.clearHateList(false);
				if(c0.isPlayer() || c0.isPet() || c0.isSummon())
				{
					myself.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 1);
					myself.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c0, null);
					AddAttackDesire(c0,1,(1 * 100));
				}
				myself.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 1);
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, c0);
				AddAttackDesire(c0,1,5000);
			}
		}
		if(script_event_arg1 == (2219023 + trainer_id))
		{
			c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				myself.i_ai6 = 1;
				RemoveAllDesire(myself);
				AddFleeDesire(c0,50000000);
				if(Rnd.get(4) < 1)
				{
					if(Rnd.get(2) < 1)
					{
						Say(MakeFString(1801114,"","","","",""));
					}
					else
					{
						Say(MakeFString(1801115,"","","","",""));
					}
				}
				myself.c_ai1 = c0;
				myself.AddTimerEx(2019777,10);
				myself.AddTimerEx(2019888,(5 * 1000));
			}
		}
		if(script_event_arg2 == trainer_id && myself.i_ai6 == 0)
		{
			switch(script_event_arg1)
			{
				case 2219011:
					if(myself.i_ai0 == 0)
					{
						myself.i_ai2 = 70;
						myself.i_ai3 = 4;
						myself.i_ai4 = 2;
						myself.AddTimerEx(22201,100);
					}
					break;
				case 2219012:
					if(myself.i_ai0 == 0)
					{
						myself.i_ai2 = 130;
						myself.i_ai3 = 1;
						myself.i_ai4 = 2;
						myself.AddTimerEx(22201,100);
					}
					break;
				case 2219013:
					if(myself.i_ai0 != 1)
					{
						myself.i_ai2 = 30;
						myself.i_ai3 = 5;
						myself.i_ai4 = 4;
						myself.AddTimerEx(22201,100);
					}
					else
					{
						myself.i_ai2 = 30;
						myself.i_ai3 = 6;
						myself.i_ai4 = 4;
						myself.AddTimerEx(22201,100);
					}
					break;
				case 2219014:
					if(myself.i_ai0 == 0)
					{
						myself.i_ai2 = 30;
						myself.i_ai3 = 7;
						myself.i_ai4 = 2;
						myself.AddTimerEx(22201,100);
					}
					break;
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2019999)
		{
			myself.AddTimerEx(2019999,5000);
		}
		if(timer_id == 2019888)
		{
			myself.i_ai6 = 0;
		}
		if(timer_id == 2019777)
		{
			AddFleeDesire(myself.c_ai1,50000000);
			if(myself.i_ai6 == 1)
			{
				myself.AddTimerEx(2019777,1000);
			}
		}
		if(timer_id == 22201)
		{
			if(!myself.isDead() && !myself.isAttackingNow() && !myself.isCastingNow())
				AddEffectActionDesire(myself,myself.i_ai3,((myself.i_ai2 * 1000) / 30),500);
			if(myself.i_ai4 != 0)
			{
				myself.i_ai4 = (myself.i_ai4 - 1);
				myself.AddTimerEx(22201,((myself.i_ai2 * 1000) / 30));
			}
		}
	}
}
