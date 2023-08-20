package ai.dragonvalley;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;

public abstract class Patrollers extends Fighter
{
	protected Location[] _points;
	private int[] _teleporters = {22857, 22833};

	protected int _lastPoint = 0;
	protected boolean _firstThought = true;

	public Patrollers(L2NpcInstance actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 1000;
		AI_TASK_DELAY = 1000;
		MaxPursueRange = Integer.MAX_VALUE - 10;
	}

	// мини хук...
	@Override
	public void setMaxPursueRange(int range)
	{
		MaxPursueRange = range;
		//canSeeInSilentMove = true;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	public void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		L2Character target;
		if((target = prepareTarget()) == null)
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		if(target.isDead())
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		super.thinkAttack();
	}

	@Override
	public void checkAggression(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || target == null || target.isPlayable() && !canSeeInSilentMove((L2Playable) target))
			return;
		if(target.isPlayable() && !target.isDead() && !target.isInvisible() && actor.isInRange(target, actor.getAggroRange()) && getActor().getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
		{
			actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
		else
		{
		
			
		}
		super.checkAggression(target);
	}

	@Override
	protected boolean thinkActive()
	{
		if(super.thinkActive())
			return true;

		if(!getActor().isMoving && getActor().getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
			startMoveTask();

		return true;
	}

	@Override
	protected void onEvtArrived()
	{
		if(getActor().getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
			startMoveTask();
		super.onEvtArrived();
	}

	protected void startMoveTask()
	{
		L2NpcInstance npc = getActor();
		if(_firstThought)
		{
			_lastPoint = getIndex(Location.findNearest(npc, _points));
			_firstThought = false;
		}
		else
			_lastPoint++;
		if(_lastPoint >= _points.length)
		{
			_lastPoint = 0;
			if(Util.contains_int(_teleporters, npc.getNpcId()))
				npc.teleToLocation(_points[_lastPoint]);
		}
		npc.setRunning();
		if(Rnd.chance(30))
			npc.altOnMagicUseTimer(npc, SkillTable.getInstance().getInfo(6757, 1));
		try
		{
			addTaskMove(Location.findPointToStay(_points[_lastPoint], 30, npc.getReflection().getGeoIndex()), true);
		}
		catch(Exception e)
		{
			_log.info(toString()+"(117): _lastPoint="+_lastPoint+" _points.length="+_points.length);
		}
		//addTaskMove(_points[_lastPoint], true);
		doTask();
		clearTasks();
	}

	protected int getIndex(Location loc)
	{
		for(int i = 0; i < _points.length; i++)
			if(_points[i] == loc)
				return i;
		return 0;
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
		_firstThought = true;
		startMoveTask();
	}
}
