package ai.TheGiantsCave;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date 17.01.14
 * @AI for all mob in The Giant's Cave
 **/

public class lesser_warrior_passive extends Fighter
{
	private L2Character myself = null;
	public lesser_warrior_passive(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	public void NO_DESIRE()
	{
		//AddMoveAroundLimitedDesire(5,5,200);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{	
		if(script_event_arg1 == 10016 && Rnd.get(100) < 50)
		{
			L2Character c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				if(myself.alive() == 0)
				{
					return;
				}
				RemoveAllDesire(myself);
				if(c0.is_pc() != 0 || c0.isSummon() || c0.isPet())
				{
					myself.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 1);
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, c0);
					AddAttackDesire(c0,1,100);
				}
			}
		}
	}
}
