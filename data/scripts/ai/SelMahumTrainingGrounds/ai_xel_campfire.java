package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2TerrainObjectInstance;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для костра в селмахум по ПТС.
 **/

public class ai_xel_campfire extends DefaultAI
{
	private L2Character myself = null;

	public ai_xel_campfire(L2Character self)
	{
		super(self);
		myself = self;
	}

	public int campfire_range = 600;
	public int Skill01_ID = -1;
	public int Skill02_ID = -1;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		myself.i_ai0 = 0;
		myself.AddTimerEx(2219001, 1000);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		L2Character c0;
		if(script_event_arg1 == 2219017)
		{
			myself.i_ai0 = 2;
			c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				SendScriptEvent(c0, 2119019, 0);
			}
			getActor().setNpcState(1);
			L2NpcInstance npc = CreateOnePrivateEx(18933,"SelMahumTrainingGrounds.ai_xel_campfire_dummy", "L2Monster",myself.getX(),myself.getY(),myself.getZ() + 100,0);
			npc.setHideName(true);
			myself.AddTimerEx(2219002,3000);
		}
		if(script_event_arg1 == 2219022)
		{
			c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				myself.c_ai0 = c0;
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		int i0;
		int i1;
		if(timer_id == 2219001)
		{
			myself.AddTimerEx(2219001,((30 * 1000) + Rnd.get(5000)));
			getActor().setNpcState(2);
			i0 = GetL2Time(2);
			if(i0 > 0)
				i1 = 2;
			else
				i1 = 4;
			if(Rnd.get(i1) < 1)
			{
				myself.i_ai0 = 1;
				getActor().setNpcState(1);
				BroadcastScriptEvent(2219021,GetIndexFromCreature(myself),campfire_range);
			}
			else
			{
				myself.i_ai0 = 0;
				getActor().setNpcState(2);
				BroadcastScriptEvent(2219020,0,campfire_range);
				if(IsNullCreature(myself.c_ai0) == 0)
					SendScriptEvent(myself.c_ai0,2219022,1);
			}
		}
		if(timer_id == 2219002)
		{
			BroadcastScriptEvent(2219018, GetIndexFromCreature(myself), campfire_range);
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}
}
