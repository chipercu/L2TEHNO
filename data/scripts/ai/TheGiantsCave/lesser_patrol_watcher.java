package ai.TheGiantsCave;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date 17.01.14
 * @AI for Patrol in the Giant's Cave
 **/

public class lesser_patrol_watcher extends Fighter
{
	private L2Character myself = null;

	public lesser_patrol_watcher(L2Character self)
	{
		super(self);
		myself = self;
		AI_TASK_ACTIVE_DELAY = 5100;
		AI_TASK_DELAY = 1000;
	}

	public String SuperPointName = "";
	public int SuperPointMethod = 2;
	public int SuperPointDesire = 50;
	public int BroadCastRange = 450;
	public Location[] points = null;
	private int _lastPoint = 0;
	private boolean lastPoint = false;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		myself.i_ai0 = 0;
		myself.i_ai4 = 0;
		if(SuperPointName.equals("giant_sp_24"))
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(192391,60460,-6096),new Location(192369,62825,-6096),new Location(193729,62806,-6096),new Location(193769,61647,-6096),new Location(189415,61641,-6104),new Location(193777,61573,-6096),new Location(193794,60448,-6096)};
		else if(SuperPointName.equals("giant_sp_27"))
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(187126,62637,-7232),new Location(187918,62709,-7232),new Location(187880,59989,-7232),new Location(186858,59997,-7232),new Location(187188,60445,-7232)};
		else if(SuperPointName.equals("giant_sp_28"))
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(186348,60158,-7232),new Location(182591,60217,-7232),new Location(182537,59724,-7232),new Location(186183,59787,-7232)};
		else if(SuperPointName.equals("giant_sp_36"))
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(190221,60035,-7232),new Location(190193,61829,-7232),new Location(191916,61811,-7232),new Location(191963,59719,-7232),new Location(193536,59724,-7232),new Location(193649,61768,-7232),new Location(191848,61768,-7232),new Location(192061,59796,-7232),new Location(190223,59791,-7232)};
		else if(SuperPointName.equals("giant_sp_38"))
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(187740,55516,-7232),new Location(187028,55338,-7232),new Location(185995,55483,-7232),new Location(185818,56887,-7232),new Location(186652,57316,-7232),new Location(187806,57075,-7232),new Location(188054,56055,-7232)};
	}

	private synchronized void startMoveTask()
	{
		if(points != null)
		{
			if(!lastPoint)
				_lastPoint++;

			if(_lastPoint >= points.length)
				lastPoint = true;

			if(lastPoint)
				_lastPoint--;

			if(_lastPoint < 0)
				_lastPoint = 0;

			if(_lastPoint == 0 && lastPoint)
				lastPoint = false;

			getActor().setWalking();
			addTaskMove(Location.findPointToStay(points[_lastPoint], 30, getActor().getReflection().getGeoIndex()), true);
			doTask();
			clearTasks();
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}

	@Override
	public void returnHome(boolean clearAggro)
	{
		super.returnHome(clearAggro);
		clearTasks();
		startMoveTask();
	}

	@Override
	public void NO_DESIRE()
	{
		if(!getActor().isMoving && getActor().getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && !SuperPointName.equals(""))
			startMoveTask();

		super.NO_DESIRE();
	}

	@Override
	protected void onEvtArrived()
	{
		ChangeMoveType(0);
		super.onEvtArrived();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(attacker.is_pc() == 1 && myself.i_ai0 == 0)
		{
			myself.i_ai0 = 1;
			myself.c_ai0 = attacker;
			AddTimerEx(2519011,(6 * 1000));
			AddTimerEx(2519012,((2 * 60) * 1000));
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		if(creature.is_pc() == 1 && myself.i_ai0 == 0)
		{
			myself.i_ai0 = 1;
			myself.c_ai0 = creature;
			if(Rnd.get(2) < 1)
			{
				Say(MakeFString(1800875,"","","","",""));
			}
			else
			{
				Say(MakeFString(1800876,"","","","",""));
			}
			AddTimerEx(2519011,(6 * 1000));
			AddTimerEx(2519012,((2 * 60) * 1000));
		}
		super.SEE_CREATURE(creature);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2519010)
		{
			//LookNeighbor(450);
			AddTimerEx(2519010,(30 * 1000));
		}
		if(timer_id == 2519011)
		{
			if(myself.i_ai4 == 1)
			{
				return;
			}
			if(myself.class_id() == 1022668)
			{
				Shout(1800865);
			}
			else
			{
				Shout(1800861);
			}
			if(IsNullCreature(myself.c_ai0) == 0)
			{
				BroadcastScriptEvent(10016,GetIndexFromCreature(myself.c_ai0),BroadCastRange);
			}
		}
		if(timer_id == 2519012)
		{
			myself.i_ai0 = 0;
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		myself.i_ai4 = 1;
		super.MY_DYING(killer);
	}
}
