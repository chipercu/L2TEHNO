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

public class ai_raid_baranka_protector extends Fighter
{
	private L2NpcInstance myself = null;

	public ai_raid_baranka_protector(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int DeBuff = 6168;
	public int DeBuffProb = 2000;

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(IsNullCreature(attacker) == 0)
		{
			if(Rnd.get(10000) < DeBuffProb)
			{
				if(Skill_GetConsumeMP(DeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(DeBuff) < myself.getCurrentHp() - damage && Skill_InReuseDelay(DeBuff) == 0)
				{
					AddUseSkillDesire(attacker, SkillTable.getInstance().getInfo(DeBuff, 9), 1);
				}
			}
		}
		super.ATTACKED(attacker, damage, skill);
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
	protected void onEvtClanAttacked(L2Character victim, L2Character attacker, int damage)
	{
		super.onEvtClanAttacked(victim, attacker, damage);
		if(victim == myself.getMyLeader())
		{
			MakeAttackEvent(attacker, 500, 0);
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		super.SCRIPT_EVENT( script_event_arg1,  script_event_arg2,  script_event_arg3);
		if(script_event_arg1 == 2214005)
		{
			Despawn(myself);
		}
		else if(script_event_arg1 == 2214007)
		{
			Despawn(myself);
		}
		else if(script_event_arg1 == 2214008)
		{
			if(myself.IsMyBossAlive() > 0)
			{
				if(myself.getMyLeader() == GetCreatureFromIndex(script_event_arg2))
				{
					MakeAttackEvent(GetCreatureFromIndex(script_event_arg3),500,0);
				}
			}
		}
	}
}