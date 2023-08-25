package ai.hellbound;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.*;

/**
 * @author: Diagod
 * АИ мобов на ХБ.
 */
public class GreaterEvil extends Fighter
{
	public String SuperPointName = "";

	private Location[] path = null;

	static final Location[] malign_conveyor_1_1 =
	{
		new Location(27886,248609,-3208),
		new Location(28150,247726,-3272),
		new Location(27340,246649,-3680),
		new Location(27646,245922,-3672),
		new Location(28247,245907,-2552),
		new Location(29074,245889,-3672),
		new Location(29182,244811,-3688),
		new Location(28904,244456,-3696)
	};

	static final Location[] malign_conveyor_1_2 =
	{
		new Location(26144,246444,-3208),
		new Location(28063,247940,-3248),
		new Location(28276,247756,-3272),
		new Location(27344,246649,-3680),
		new Location(27652,245914,-3688),
		new Location(28255,245912,-2552)
	};

	static final Location[] malign_conveyor_1_3 =
	{
		new Location(27336,246216,-3656),
		new Location(27645,245921,-3672),
		new Location(28245,245904,-2552)
	};

	static final Location[] malign_conveyor_1_4 =
	{
		new Location(28476,245914,-3696),
		new Location(29058,245894,-3672),
		new Location(29433,245216,-3688),
		new Location(28942,244492,-3696)
	};

	static final Location[] malign_conveyor_2_1 =
	{
		//new Location(28771,244251,-2496),
		new Location(28680,244120,-3696),
		new Location(28495,243839,-3696),
		new Location(28727,242939,-3592),
		new Location(28188,242512,-3464),
		new Location(27248,242797,-3168),
		new Location(26850,243834,-2896),
		new Location(24636,246593,-1968),
		new Location(24519,248586,-1936),
		new Location(23094,250031,-1976),
		new Location(22673,251605,-1992),
		new Location(24560,254414,-1984),
		new Location(25392,255459,-1992)
	};

	private boolean _firstThought = true;
	private int current_point = 0;

	public GreaterEvil(L2NpcInstance actor)
	{
		super(actor);
		MaxPursueRange = 6000;
		AI_TASK_ACTIVE_DELAY = 1000;
		AI_TASK_DELAY = 1000;
	}

	@Override
	protected void onEvtSpawn()
	{
		if(SuperPointName.equals("malign_conveyor_1_1"))
			path = malign_conveyor_1_1;
		else if(SuperPointName.equals("malign_conveyor_1_2"))
			path = malign_conveyor_1_2;
		else if(SuperPointName.equals("malign_conveyor_1_3"))
			path = malign_conveyor_1_3;
		else if(SuperPointName.equals("malign_conveyor_1_4"))
			path = malign_conveyor_1_4;
		else if(SuperPointName.equals("malign_conveyor_2_1"))
			path = malign_conveyor_2_1;
		else
			System.out.println("GreaterEvil: !!!!!!!!!!!! 91 !!!!!!!!!!!!");
		super.onEvtSpawn();
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

	private int getIndex(Location loc)
	{
		for(int i = 0; i < path.length; i++)
			if(path[i] == loc)
				return i;
		return 0;
	}

	private synchronized void startMoveTask()
	{
		L2NpcInstance npc = getActor();
		if(_firstThought)
		{
			current_point = getIndex(Location.findNearest(npc, path));
			_firstThought = false;
		}
		else
			current_point++;
		if(current_point >= path.length)
		{
			current_point = 0;
			npc.teleToLocation(path[current_point]);
		}
		npc.setRunning();
		try
		{
			addTaskMove(Location.findPointToStay(path[current_point], 30, npc.getReflection().getGeoIndex()), true);
		}
		catch(Exception e)
		{
			_log.info("ErrorAI: -> GreaterEvil(173): SuperPointName=\""+SuperPointName+"\" current_point="+current_point+" path.length="+path.length);
			e.printStackTrace();
		}
		//addTaskMove(path[current_point], true);
		
		doTask();
		clearTasks();
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

	@Override
    protected void ATTACKED(L2Character attacker, int damage, L2Skill skill) 
	{
        _firstThought = true;
        super.ATTACKED(attacker, damage, skill);
    }
}