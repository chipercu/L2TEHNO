package ai;

import l2open.gameserver.ai.DefaultAI.Task;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;

public class FeralHound extends Fighter
{
	private boolean newTask = false;

	public FeralHound(L2Character actor)
	{
		super(actor);
		actor.setIsInvul(true);
	}

	@Override
	protected boolean doTask()
	{
		if(newTask)
		{
			L2NpcInstance npc = getActor();
			if(npc == null)
				return false;
			if(_task_list.size() == 0)
			{
				clearTasks();
				return true;
			}
			Task tasks = null;
			try
			{
				tasks = _task_list.first();
			}
			catch (Exception e)
			{
			}
			if(tasks == null)
				clearTasks();
			if(!_def_think)
				return true;
			assert tasks != null;
			switch(tasks.type)
			{
				case MOVE:
					if(npc.isMoving)
						return false;
					if(!npc.moveToLocation(tasks.loc, 0, true))
					{
						clientStopMoving();
						_pathfind_fails = 0;
						npc.teleToLocation(tasks.loc);
						return maybeNextTask(tasks);
					}
			}
			return false;
		}
		return super.doTask();
	}

	@Override
	protected boolean createNewTask()
	{
		return newTask ? false : super.createNewTask();
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance npc = getActor();
		if(npc == null || skill == null || npc.isDead())
			return;
		if(skill.getId() == 5909)
		{
			clearTasks();
			addTaskMove(npc.getSpawnedLoc(), false);
			newTask = true;
		}
		else if(skill.getId() == 5910)
		{
			for(L2Playable playable : L2World.getAroundPlayables(npc))
				if(playable != null && !playable.isAlikeDead() && !playable.isInvul() && playable.isVisible())
					checkAggression(playable);
			newTask = false;
		}
		super.onEvtSeeSpell(skill, caster);
	}

	@Override
	protected void onIntentionAttack(L2Character target)
	{
		if(newTask)
			return;
		super.onIntentionAttack(target);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		if(newTask)
			return;
		super.checkAggression(target);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		if(newTask)
			return;
		super.onEvtClanAttacked(attacked_member, attacker, damage);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(newTask)
			return;
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		if(newTask)
			return;
		super.onEvtAggression(target, aggro);
	}
}