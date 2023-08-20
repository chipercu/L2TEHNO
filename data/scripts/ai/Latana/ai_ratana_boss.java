package ai.Latana;

import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.*;

/**
 * @author Drizzy
 * @date 01.02.2014
 * open-team.ru
 **/

public class ai_ratana_boss extends DefaultAI
{
	private L2Character myself;
	public ai_ratana_boss(L2Character self)
	{
		super(self);
		myself = self;
		myself.p_block_move(true, null);
	}


	@Override
	public void onEvtSpawn()
	{
		myself.i_ai0 = 0;
		myself.i_ai1 = 0;
		myself.i_ai2 = 0;
		myself.AddTimerEx(1000,1000);
		super.onEvtSpawn();
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{

		if(myself.i_ai2 == 0)
		{
			if(creature.is_pc() == 1)
			{
				myself.c_ai0 = creature;
			}
			if(creature.isPet() || creature.isSummon())
			{
				return;
			}
			else
			{
				EffectMusic(myself, 10000, "BS08_A");
				BroadcastScriptEvent(2316004, 1, 4000);
				myself.AddTimerEx(500,1000);
				myself.i_ai2 = 1;
			}
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		myself.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		AddAttackDesire(target,0,100);
		super.onEvtFinishCasting(skill, caster, target);
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(myself.i_ai2 == 0)
		{
			if(attacker.is_pc() == 1)
			{
				myself.c_ai0 = attacker;
			}
			if(attacker.isPet() || attacker.isSummon())
			{
				myself.c_ai0 = attacker.getPlayer();
			}
			else
			{
				EffectMusic(myself, 10000, "BS08_A");
				BroadcastScriptEvent(2316004, 2, 4000);
				myself.AddTimerEx(600,1000);
				myself.i_ai2 = 1;
			}
		}
		myself.c_ai0 = attacker;
		if(myself.getCurrentHp() < (myself.getMaxHp() * 0.300000f) && myself.i_ai0 == 0)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(5718, 1),50000000);
			myself.i_ai0 = 1;
			myself.AddTimerEx(4000,(120 * 1000));
		}
		super.ATTACKED( attacker,  damage, skill);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		L2Character c0;
		if(timer_id == 500)
		{
			CreateOnePrivateEx(1018605, "Latana.ai_wdragon_target", 0, 0, 105465, -41817, -1768, 0, 0, 0, 0, true);
			myself.AddTimerEx(501,3000);
		}
		if(timer_id == 501)
		{
			AddEffectActionDesire(myself, 0, ((91 * 1000) / 30), 10000);
			myself.AddTimerEx(502,3000);
		}
		if(timer_id == 502)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(5759, 1), 500);
			myself.AddTimerEx(503,9700);
		}
		if(timer_id == 503)
		{
			if(IsNullCreature(myself.c_ai1) == 1)
			{
			}
			else
			{
				AddUseSkillDesire(myself.c_ai1, SkillTable.getInstance().getInfo(5716, 1),5000);
			}
			myself.AddTimerEx(504,6030);
		}
		if(timer_id == 504)
		{
			myself.AddTimerEx(505,4000);
		}
		if(timer_id == 505)
		{
			myself.AddTimerEx(2000,1000);
		}
		if(timer_id == 600)
		{
			CreateOnePrivateEx(1018605, "Latana.ai_wdragon_target", 0, 0, 105465, -41817, -1768, 0, 0, 0, 0, true);
			myself.AddTimerEx(602,2000);
		}
		if(timer_id == 602)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(5715, 1),50000);
			myself.AddTimerEx(603,2500);
		}
		if(timer_id == 603)
		{
			if(IsNullCreature(myself.c_ai1) == 1)
			{
			}
			else
			{
				AddUseSkillDesire(myself.c_ai1, SkillTable.getInstance().getInfo(5716, 1),50000);
			}
			myself.AddTimerEx(604,6030);
		}
		if(timer_id == 604)
		{
			myself.AddTimerEx(2000,6000);
		}
		if(timer_id == 1000)
		{
			myself.AddTimerEx(1000,(30 * 1000));
		}
		if(timer_id == 2000)
		{
			c0 = myself.c_ai0;
			if(IsNullCreature(c0) == 1)
			{
				myself.AddTimerEx(2000,3000);
			}
			if(DistFromMe(c0) < 100)
			{
				if(Rnd.get(100) < 30)
				{
					AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(5715, 1),500000);
				}
				else
				{
					myself.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c0, 1);
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, c0);
					AddAttackDesire(c0, 0, 1000);
				}
			}
			else if(Rnd.get(100) < 50)
			{
				if(getZone().isInside(c0))
				{
					CreateOnePrivateEx(1018661,"Latana.ai_ratana_skilluse",0,0,c0.getX(),c0.getY(),c0.getZ(),0,GetIndexFromCreature(myself),0,0, true);
				}
			}
			else
			{
				AddUseSkillDesire(c0, SkillTable.getInstance().getInfo(5717, 1),500000);
			}
			myself.AddTimerEx(2000,6000);
		}
		if(timer_id == 4000)
		{
			myself.i_ai0 = 0;
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		L2Character c0;
		if(script_event_arg1 == 2316002)
		{
			c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0 && DistFromMe(c0) <= 900)
			{
				AddUseSkillDesire(c0, SkillTable.getInstance().getInfo(5716, 1),500000);
			}
		}
		if(script_event_arg1 == 2316005)
		{
			myself.c_ai1 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(myself.c_ai1) == 0)
			{
			}
		}
	}

	public L2Territory getZone()
	{
		return TerritoryTable.getInstance().getLocation(96038);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		CreateOnePrivateEx(1018604, "Latana.ai_wdragon_camera02", 0, 0, 105974, -41794, -1784, 32768, 0, 0, 0, true);
		super.MY_DYING(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}
}
