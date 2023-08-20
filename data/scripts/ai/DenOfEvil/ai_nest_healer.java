package ai.DenOfEvil;

import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_nest_healer extends Fighter
{
	private L2NpcInstance myself = null;

	public ai_nest_healer(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int Heal = 4065;
	public int HealProb = 3333;
	public int SelfRangeHeal = 4613;
	public int SelfRangeHealProb = 1500;
	public int SelfRangeBuff = 7000;
	public int HELP_PROB = 2500;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(SelfRangeBuff != 7000)
		{
			if(Skill_GetConsumeMP(SelfRangeBuff) < myself.getCurrentMp() && Skill_GetConsumeHP(SelfRangeBuff) < myself.getCurrentHp() && Skill_InReuseDelay(SelfRangeBuff) == 0)
			{
				AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SelfRangeBuff, 1), 1);
			}
		}
	}

	@Override
	public void NO_DESIRE()
	{
		if(getActor().getMyLeader() != null)
		{
			AddFollowDesire(getActor().getMyLeader(), 5);
		}
		else
		{
			AddMoveAroundDesire(5, 5);
		}
		super.NO_DESIRE();
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		if(myself.getCurrentHp() < (myself.getMaxHp() * 0.700000))
		{
			if(Rnd.get(10000) < HealProb)
			{
				if(Skill_GetConsumeMP(Heal) < myself.getCurrentMp() && Skill_GetConsumeHP(Heal) < myself.getCurrentHp() && Skill_InReuseDelay(Heal) == 0)
				{
					AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(Heal, 9), 1);
				}
			}
		}
	}

	@Override
	protected void onEvtClanAttacked(L2Character victim, L2Character attacker, int damage)
	{
		super.onEvtClanAttacked(victim, attacker, damage);
		if(IsNullCreature(victim) == 0 && victim != myself)
		{
			if(IsNullCreature(attacker) == 0)
			{
				int i0 = Rnd.get(10000);
				if(i0 <= HELP_PROB)
				{
					MakeAttackEvent(attacker, damage, 0);
				}
			}
		}
		if(victim.getCurrentHp() < (victim.getMaxHp() * 0.700000))
		{
			if(Rnd.get(10000) < HealProb)
			{
				if(Skill_GetConsumeMP(Heal) < myself.getCurrentMp() && Skill_GetConsumeHP(Heal) < myself.getCurrentHp() && Skill_InReuseDelay(Heal) == 0)
				{
					AddUseSkillDesire(victim, SkillTable.getInstance().getInfo(Heal, 9), 1);
				}
			}
		}
		if(victim.getCurrentHp() < (victim.getMaxHp() * 0.300000))
		{
			if(Rnd.get(10000) < SelfRangeHealProb)
			{
				if(Skill_GetConsumeMP(SelfRangeHeal) < myself.getCurrentMp() && Skill_GetConsumeHP(SelfRangeHeal) < myself.getCurrentHp() && Skill_InReuseDelay(SelfRangeHeal) == 0)
				{
					AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SelfRangeHeal, 9), 1);
				}
			}
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
			if(getActor().getMyLeader() != null)
			{
				if(getActor().getMyLeader() == GetCreatureFromIndex(script_event_arg2))
				{
					MakeAttackEvent(GetCreatureFromIndex(script_event_arg3), 500, 0);
				}
			}
		}
	}
}
