package ai.CryptsOfDisgrace;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 21.08.2012
 */

public class ai_legend_orc_ev_leader extends Mystic
{
	private L2Character myself = null;
	private L2Character c_ai0 = null;
	public ai_legend_orc_ev_leader(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		Shout(1800863);
		AddTimerEx(2114001,((2 * 60) * 1000));
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 100);
		getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, creature, null);
		AddUseSkillDesire(creature, SkillTable.getInstance().getInfo(5313,9),100);
		c_ai0 = creature;
		AddTimerEx(2114009,1000);
		super.SEE_CREATURE(creature);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
		getActor().getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker, null);
		AddUseSkillDesire(attacker,SkillTable.getInstance().getInfo(5313,9),100);
		c_ai0 = attacker;
		AddTimerEx(2114009,1000);
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2114001)
		{
			CreateOnePrivateEx(18816, "CryptsOfDisgrace.ai_legend_orc_treasure", "L2Monster", getActor().getX(), getActor().getY(), getActor().getZ(), 0);
			Suicide(myself);
		}
		if(timer_id == 2114009)
		{
			if(IsNullCreature(getActor().getAI().getAttackTarget()) == 0 && IsNullCreature(c_ai0) == 0)
			{
				AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(5313,9),100);
				AddTimerEx(2114009,3500);
			}
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		Shout(1800864);
		super.MY_DYING(killer);
	}
}
