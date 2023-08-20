package ai.Latana;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;

/**
 * @author Drizzy
 * @date 01.02.2014
 * open-team.ru
 **/

public class ai_ratana_skilluse extends DefaultAI
{
	private L2Character myself;
	public ai_ratana_skilluse(L2Character self)
	{
		super(self);
		myself = self;
	}


	@Override
	public void onEvtSpawn()
	{
		L2Character c0;
		myself.AddTimerEx(1002,10);
		if(myself.param1 != 0)
		{
			c0 = GetCreatureFromIndex(myself.param1);
			if(IsNullCreature(c0) == 0)
			{
				myself.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 1);
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, c0);
				AddAttackDesire(c0,1,10000000);
			}
		}
		super.onEvtSpawn();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 1002)
		{
			BroadcastScriptEvent(2316002,GetIndexFromCreature(myself),2000);
			myself.AddTimerEx(2002,(5 * 1000));
		}
		if(timer_id == 2002)
		{
			Suicide(myself);
		}
	}
}
