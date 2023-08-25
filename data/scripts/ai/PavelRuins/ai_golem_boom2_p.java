package ai.PavelRuins;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 16.08.2012
 * @АИ для Boom Golem (100% PTS).
 */

public class ai_golem_boom2_p extends DefaultAI
{
	private L2Character myself = null;
	private int Skill01_ID = 6265;
	private int Skill02_ID = 6264;
	private int i_ai0 = 0;

	public ai_golem_boom2_p(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		AddTimerEx(1000,1000);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 1000)
		{
			getActor().doCast(SkillTable.getInstance().getInfo(Skill01_ID,1), myself, true);
			AddTimerEx(1000, 3000);
		}
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(DistFromMe(attacker) > 50 && DistFromMe(attacker) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
		{
			AddFollowDesire(attacker, 10);
		}
		else if(DistFromMe(attacker) <= 50 && !attacker.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
		{
			if(i_ai0 == 0)
			{
				getActor().doCast(SkillTable.getInstance().getInfo(Skill02_ID,1), myself, true);
				i_ai0 = 1;
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}


	@Override
	public void SEE_CREATURE(L2Character target)
	{
		if(DistFromMe(target) > 50 && DistFromMe(target) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), target, false))
		{
			AddFollowDesire(target, 10);
		}
		else if(DistFromMe(target) <= 50 && GeoEngine.canAttacTarget(getActor(), target, false))
		{
			getActor().doCast(SkillTable.getInstance().getInfo(Skill02_ID,1), myself, true);
		}
		super.SEE_CREATURE(target);
	}

	@Override
	public void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage)
	{
		if(party_member_attacked == ((L2MinionInstance)myself).getLeader())
		{
			if(DistFromMe(attacker) > 50 && DistFromMe(attacker) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
			{
				AddFollowDesire(attacker, 10);
			}
			else if(DistFromMe(attacker) <= 50 && !attacker.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
			{
				if(i_ai0 == 0)
				{
					getActor().doCast(SkillTable.getInstance().getInfo(Skill02_ID,1), myself, true);
					i_ai0 = 1;
				}
			}
		}
		else if(party_member_attacked.getNpcId() != myself.getNpcId())
		{
			if(DistFromMe(attacker) > 50 && DistFromMe(attacker) < 2000 && !myself.isDead() && GeoEngine.canAttacTarget(getActor(), attacker, false))
			{
				AddFollowDesire(attacker, 10);
			}
			else if(DistFromMe(attacker) <= 50 && GeoEngine.canAttacTarget(getActor(), attacker, false))
			{
				if(i_ai0 == 0)
				{
					getActor().doCast(SkillTable.getInstance().getInfo(Skill02_ID,1), myself, true);
					i_ai0 = 1;
				}
			}
		}
		super.PARTY_ATTACKED(attacker, party_member_attacked, damage);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 10029)
		{
			Despawn(myself);
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		i_ai0 = 0;
		super.onEvtFinishCasting(skill, caster,target);
	}
}
