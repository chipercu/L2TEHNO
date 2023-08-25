package ai;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

/**
 * @author: Diagod
 * AI для Solina Knights ID: 18909
 */
public class ai_solina_warrior extends Fighter
{
	private L2Character myself = null;

	public ai_solina_warrior(L2Character self)
	{
		super(self);
		myself = self;
		SelfAggressive = 100;
	}

	public int PhysicalSpecial = 6311;
	public int TIMER = 555;

	@Override
	public void onEvtSpawn()
	{
		AddTimerEx(TIMER,(500));
		myself.setRunning();
		super.onEvtSpawn();
	}

	@Override
	public void SEE_CREATURE(L2Character target)
	{
		final L2NpcInstance actor = getActor();
		if (target.isPlayer() && actor.isInRange(target.getLoc(), 300))
		{
			L2Player player = target.getPlayer();	
			actor.setRunning();
			actor.setTarget(player);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
			player.addDamageHate(actor,20,10);
		}
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(Rnd.get(100) < 10)
			if(Skill_GetConsumeMP(PhysicalSpecial) < myself.getCurrentMp() && Skill_GetConsumeHP(PhysicalSpecial) < myself.getCurrentHp() - damage && Skill_InReuseDelay(PhysicalSpecial) == 0)
				AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(PhysicalSpecial,1),1000000);
		super.ATTACKED( attacker,  damage, skill);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 21140014)
			if(Rnd.get(100) < 30)
				AddAttackDesireEx(script_event_arg2,1,1,100);
		super.SCRIPT_EVENT( script_event_arg1,  script_event_arg2, script_event_arg3);
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		AddAttackDesire(attacker,1,1000);
		if(Rnd.get(100) < 10)
			if(Skill_GetConsumeMP(PhysicalSpecial) < myself.getCurrentMp() && Skill_GetConsumeHP(PhysicalSpecial) < myself.getCurrentHp() - damage && Skill_InReuseDelay(PhysicalSpecial) == 0)
				AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(PhysicalSpecial,1),1000000);
		super.onEvtClanAttacked(attacked_member, attacker, damage);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER)
			if(myself.param1 == 1000)
				if(IsNullCreature(GetCreatureFromIndex(myself.param2)) == 0)
					AddAttackDesireEx(myself.param2,1,1,100000);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}