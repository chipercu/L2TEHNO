package ai.ForgeOfTheGods;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 19.08.2012
 * AI for Tar Beetle - 100% PTS.
 */

public class default_tar_forge extends DefaultAI
{
	private L2Character myself = null;
	private int Shot_num_til_dsp = 5;
	private int TID_LONELY_TOO_LONG = 78001;
	private int TIME_LONELY_TOO_LONG = 300;
	private int TID_LOOK_NEIGHBOR = 78002;
	private int TIME_LOOK_NEIGHBOR = 10;
	private int i_ai0 = 0;
	private int i_ai1 = 0;

	public default_tar_forge(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		i_ai0 = 0;
		i_ai1 = Shot_num_til_dsp;
		AddTimerEx(TID_LOOK_NEIGHBOR,( TIME_LOOK_NEIGHBOR * 1000 ));
		AddTimerEx(TID_LONELY_TOO_LONG, (TIME_LONELY_TOO_LONG * 1000));
		BroadcastScriptEvent(78010081,myself.getObjectId(),3000);
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		if(creature.isPlayer() && DistFromMe(creature) <= 300 && i_ai1 > 0 && Skill_GetConsumeMP(6142, 1) < myself.getCurrentMp() && Skill_GetConsumeMP(6142, 2) < myself.getCurrentMp() && Skill_GetConsumeMP(6142, 3) < myself.getCurrentMp() && Skill_InReuseDelay(6142) == 0)
		{
			if(creature.getEffectList().getEffectBySkillId(6142) != null)
			{
				if(creature.getEffectList().getEffectBySkillId(6142).getSkill().getAbnormalLv() >= 2)
				{
					AddUseSkillDesire(creature, SkillTable.getInstance().getInfo(6142,3),100);
				}
				else if(creature.getEffectList().getEffectBySkillId(6142).getSkill().getAbnormalLv() >= 1)
				{
					AddUseSkillDesire(creature,SkillTable.getInstance().getInfo(6142,2),100);
				}
			}
			else
			{
				AddUseSkillDesire(creature,SkillTable.getInstance().getInfo(6142,1),100);
			}
		}
		super.SEE_CREATURE(creature);
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		if(skill.getId() == 6142)
		{
			i_ai1 = (i_ai1 - 1);
			if((i_ai1 <= 0 || Skill_GetConsumeMP(6142) > myself.getCurrentMp()) && i_ai0 == 0)
			{
				i_ai0 = 1;
				Despawn(myself);
			}
		}
		super.onEvtFinishCasting(skill, caster,target);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_LOOK_NEIGHBOR)
		{
			AddTimerEx(TID_LOOK_NEIGHBOR,(TIME_LOOK_NEIGHBOR * 1000));
		}
		else if(timer_id == TID_LONELY_TOO_LONG)
		{
			if(i_ai1 >= Shot_num_til_dsp && i_ai0 == 0)
			{
				i_ai0 = 1;
				Despawn(myself);
			}
			else if(i_ai1 < Shot_num_til_dsp)
			{
				i_ai1 = (i_ai1 + 1);
			}
			AddTimerEx(TID_LONELY_TOO_LONG,(TIME_LONELY_TOO_LONG * 1000));
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 78010081 && script_event_arg2 != myself.getObjectId() && i_ai0 == 0 )
		{
			i_ai0 = 1;
			Despawn(myself);
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		i_ai0 = 0;
		i_ai1 = 0;
		super.MY_DYING(killer);
	}
}
