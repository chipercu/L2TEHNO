package ai.DenOfEvil;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_nest_observer extends DefaultAI
{
	private L2NpcInstance myself = null;

	public ai_nest_observer(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int TIMER_CHECK_20SEC = 33120;
	public int TIMER_CHECK_30MIN = 33130;
	public int TIMER_DESPAWN = 33122;
	public int limit_count = 10;
	public int debug_mode = 0;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		myself.i_ai5 = 0;
		myself.i_ai8 = 0;
		myself.i_ai9 = 2;
		myself.i_ai6 = myself.param1;
		myself.i_ai7 = myself.param2;
		SendScriptEvent(getActor().getMyLeader(),2214010,getActor().param3,0);
		myself.AddTimerEx(TIMER_CHECK_30MIN,(60 * 1000));
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER_CHECK_30MIN)
		{
			myself.i_ai5 = (myself.i_ai5 + 1);
			BroadcastScriptEvent(2214002,(int)myself.i_ai6,(myself.i_ai9 - myself.i_ai5),4000);
			if(myself.i_ai5 == myself.i_ai9)
			{
				myself.i_ai8 = 1;
				myself.AddTimerEx(TIMER_DESPAWN,(3 * 1000));
			}
			if(myself.i_ai8 == 0)
			{
				myself.AddTimerEx(TIMER_CHECK_30MIN,(60 * 1000));
			}
		}
		else if(timer_id == TIMER_DESPAWN)
		{
			//myself.SetAbilityItemDrop(0);
			Suicide(myself);
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2214003)
		{
			if(myself.i_ai6 == script_event_arg2)
			{
				if(myself.i_ai8 == 0)
				{
					myself.AddTimerEx(TIMER_DESPAWN,(3 * 1000));
				}
			}
		}
		else if(script_event_arg1 == 2214011)
		{
			if(myself.i_ai6 == script_event_arg3)
			{
				myself.i_ai5 = 0;
			}
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		SendScriptEvent(getActor().getMyLeader(),2214004,(int)myself.i_ai6,myself.i_ai7);
		BroadcastScriptEvent(2214011,getActor().param3,(int)myself.i_ai6,4000);
		super.MY_DYING(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

}
