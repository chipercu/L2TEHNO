package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для мобов в селмахум
 **/

public class ai_xel_private_warrior extends Fighter
{
	private L2Character myself = null;
	public ai_xel_private_warrior(L2Character self)
	{
		super(self);
		myself = self;
	}

	public int minDistance = 100;
	public int maxDistance = 200;
	public int OHS_Weapon = 15280;
	public int THS_Weapon = 15281;
	public int i_quest0 = 0;

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		if(i_quest0 == 1)
			return false;
		else
			return true;
	}

	@Override
	public void NO_DESIRE()
	{
		if(myself.i_ai0 != 0)
		{
			return;
		}
		super.NO_DESIRE();
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(getActor().getRightHandItem() != THS_Weapon)
		{
			getActor().setRHandId(THS_Weapon);
			getActor().updateAbnormalEffect();
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		if(getActor().getRightHandItem() != THS_Weapon)
		{
			getActor().setRHandId(THS_Weapon);
			getActor().updateAbnormalEffect();
		}
		AddAttackDesire(attacker, 1, 5000);
		super.onEvtClanAttacked(attacked_member, attacker, damage);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		L2Character c0;
		Location pos0 = null;
		if(script_event_arg1 == 2219018 && !myself.isAttackingNow())
		{
			if(myself.i_ai3 == 0)
			{
				if(myself.i_ai0 == 1 && getActor().getRightHandItem() != THS_Weapon)
				{
					getActor().setRHandId(THS_Weapon);
					getActor().updateAbnormalEffect();
				}
				myself.i_ai0 = 1;
				myself.i_ai3 = 1;
				c0 = GetCreatureFromIndex(script_event_arg2);
				if(IsNullCreature(c0) == 1)
				{
				}
				else
				{
					ChangeMoveType(1);
					if(Rnd.get(3) < 1)
					{
						Say(MakeFString(1801118,"","","","",""));
					}
					else
					{
						Say(MakeFString(1801119,"","","","",""));
					}
					pos0 = GetRandomPosInCreature(c0,minDistance,(maxDistance - 100));
					myself.getAI().clearTasks();
					addTaskMove(pos0, true);
					doTask();
					//AddMoveToDesire(pos0.x, pos0.y, pos0.z, 100);
					myself.i_ai1 = pos0.x;
					myself.i_ai2 = pos0.y;
					myself.c_ai0 = c0;
				}
			}
		}
		if(script_event_arg1 == 2219021 && !myself.isAttackingNow())
		{
			if(myself.i_ai0 == 0)
			{
				myself.i_ai0 = 1;
				c0 = GetCreatureFromIndex(script_event_arg2);
				if(IsNullCreature(c0) == 1)
				{
				}
				else
				{
					ChangeMoveType(0);
					pos0 = GetRandomPosInCreature(c0,minDistance,maxDistance);
					myself.getAI().clearTasks();
					addTaskMove(pos0, true);
					doTask();
					//AddMoveToDesire(pos0.x, pos0.y, pos0.z, 100);
					myself.i_ai1 = pos0.x;
					myself.i_ai2 = pos0.y;
				}
			}
		}
		if(script_event_arg1 == 2219020 && !myself.isAttackingNow())
		{
			myself.getAI().clearTasks();
			myself.i_ai0 = 0;
			myself.i_ai3 = 0;
			if(getActor().getRightHandItem() != THS_Weapon)
			{
				getActor().setRHandId(THS_Weapon);
				getActor().updateAbnormalEffect();
			}
			myself.AddTimerEx(2219009,3000);
		}
		if(script_event_arg1 == 2219024)
		{
			c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) != 1)
			{
				myself.c_ai1 = c0;
				myself.i_ai4 = 1;
				myself.AddTimerEx(2219012,((3 * 60) * 1000));
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2219009)
		{
			if(myself.i_ai0 == 0 && myself.i_ai3 == 0)
			{
				AddMoveToDesire(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z, 100);
			}
		}
		if(timer_id == 2219010)
		{
			if(myself.i_ai0 == 1)
			{
				i_quest0 = 1;
				AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(6331,1), 1);
				getActor().setNpcState(2);
				ChangeMoveType(0);

				myself.AddTimerEx(2219011,((5 * 60) * 1000));
			}
			if(myself.i_ai3 == 1)
			{
				i_quest0 = 1;
				AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(6332,1), 1);
				getActor().setNpcState(1);
				ChangeMoveType(0);
				myself.AddTimerEx(2219011,((5 * 60) * 1000));
			}
		}
		if(timer_id == 2219011)
		{
			i_quest0 = 0;
			getActor().setNpcState(3);
			ChangeMoveType(1);
		}
		if(timer_id == 2219012)
		{
			myself.i_ai4 = 0;
		}
	}

	@Override
	protected void onEvtArrived()
	{
		if(myself.i_ai1 == getActor().getX() && myself.i_ai2 == getActor().getY())
		{
			if(IsNullCreature(myself.c_ai0) == 0)
			{
			}
			if(getActor().getRightHandItem() != OHS_Weapon)
			{
				getActor().setRHandId(OHS_Weapon);
				getActor().updateAbnormalEffect();
			}
			myself.AddTimerEx(2219010,3000);
		}
		super.onEvtArrived();
	}
}