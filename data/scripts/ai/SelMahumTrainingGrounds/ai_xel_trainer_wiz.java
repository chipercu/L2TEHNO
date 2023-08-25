package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для тренеров в селмахум
 **/

public class ai_xel_trainer_wiz extends Fighter
{
	private L2Character myself = null;
	public ai_xel_trainer_wiz(L2Character self)
	{
		super(self);
		myself = self;
	}

	public int trainer_id = -1;
	public int trainning_range = 1000;
	public int direction = -1;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		myself.AddTimerEx(2219001,1000);
	}

	@Override
	public void NO_DESIRE()
	{
		myself.i_ai0 = 0;
		if(myself.i_ai5 == 1)
			return;
		if(myself.getX() == getActor().getSpawnedLoc().x && getActor().getSpawnedLoc().y == myself.getY())
		{
			if(myself.getHeading() != direction)
			{
				myself.setHeading(direction);
				myself.updateAbnormalEffect();
			}
		}
		else
		{
			InstantTeleport(myself,getActor().getSpawnedLoc().x,getActor().getSpawnedLoc().y,getActor().getSpawnedLoc().z);
			myself.updateAbnormalEffect();
		}
		super.NO_DESIRE();
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(myself.i_ai0 == 0)
		{
			myself.c_ai0 = attacker;
			BroadcastScriptEvent((10016 + trainer_id), GetIndexFromCreature(attacker), trainning_range);
			if(IsNullCreature(getActor().getMostHated()) == 0)
			{
				myself.i_ai0 = 1;
				myself.i_ai1 = 1;
				myself.AddTimerEx(2219002,(60 * 1000));
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2219002)
		{
			myself.i_ai1 = 0;
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(myself.i_ai1 == 1)
		{
			if(IsNullCreature(myself.c_ai0) == 0)
			{
				BroadcastScriptEvent((2219023 + trainer_id), GetIndexFromCreature(myself.c_ai0), trainning_range);
			}
		}
		myself.i_ai5 = 1;
		super.MY_DYING(killer);
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
				if(Rnd.get(10) < 1)
				{
					if(Rnd.get(2) < 1)
					{
						Say(MakeFString(1801112,"","","","",""));
					}
					else
					{
						Say(MakeFString(1801113,"","","","",""));
					}
				}
				if(myself.i_ai0 == 0)
				{
					myself.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 1);
					myself.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, c0, null);
					AddAttackDesire(c0, 1, 5000);
				}
			}
		}
	}
}
