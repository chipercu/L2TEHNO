package events.TheFlowOfTheHorror;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

public class MonstersAI extends Fighter
{
	private GArray<Location> _points = new GArray<Location>();
	private int current_point = -1;

	public void setPoints(GArray<Location> points)
	{
		_points = points;
	}

	public MonstersAI(L2Character actor)
	{
		super(actor);
		AI_TASK_DELAY = 500;
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getMaxPursueRange()
	{
		return 30000;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(_globalAggro < 0)
			_globalAggro++;
		else if(_globalAggro > 0)
			_globalAggro--;

		if(_def_think)
		{
			doTask();
			return true;
		}

		if(current_point > -1 || Rnd.chance(5))
		{
			if(current_point >= _points.size() - 1)
			{
				L2Character target = L2ObjectsStorage.getByNpcId(30754);
				if(target != null && !target.isDead())
				{
					clearTasks();
					// TODO actor.addDamageHate(target, 0, 1000);
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					return true;
				}
				return true;
			}

			current_point++;

			actor.setRunning();

			clearTasks();

			// Добавить новое задание
			addTaskMove(_points.get(current_point), true);
			doTask();
			return true;
		}

		if(randomAnimation())
			return true;

		return false;
	}
}