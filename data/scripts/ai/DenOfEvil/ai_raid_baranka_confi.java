package ai.DenOfEvil;

import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_raid_baranka_confi extends Fighter
{
	private L2NpcInstance myself = null;

	public ai_raid_baranka_confi(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int SelfRangeBuff = 4030;
	public int TIMER_SKILL_RESET = 33112;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(Skill_GetConsumeMP(SelfRangeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(SelfRangeBuff) < myself.getCurrentHp() && Skill_InReuseDelay(SelfRangeBuff) == 0)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SelfRangeBuff, 2), 1);
		}
		myself.AddTimerEx(TIMER_SKILL_RESET,((5 * 60) * 1000));
	}

	@Override
	public void NO_DESIRE()
	{
		if(myself.IsMyBossAlive() > 0)
		{
			AddFollowDesire(myself.getMyLeader(), 5);
		}
		else
		{
			Despawn(myself);
		}
		super.NO_DESIRE();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER_SKILL_RESET)
		{
			if(Skill_GetConsumeMP(SelfRangeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(SelfRangeBuff) < myself.getCurrentHp() && Skill_InReuseDelay(SelfRangeBuff) == 0)
			{
				AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SelfRangeBuff, 2), 1);
			}
			myself.AddTimerEx(TIMER_SKILL_RESET,((5 * 60) * 1000));
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2214005 || script_event_arg1 == 2214007)
		{
			Despawn(myself);
		}
		else if(script_event_arg1 == 2214008)
		{
			if(myself.IsMyBossAlive() > 0)
			{
				if(myself.getMyLeader() == GetCreatureFromIndex(script_event_arg2))
				{
					MakeAttackEvent(GetCreatureFromIndex(script_event_arg3), 500, 0);
				}
			}
		}
	}
}