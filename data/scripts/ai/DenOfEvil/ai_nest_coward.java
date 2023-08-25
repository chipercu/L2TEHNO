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

public class ai_nest_coward extends Fighter
{
	private L2NpcInstance myself = null;

	public ai_nest_coward(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int reward_adena = 1000000;
	public int reward_adena_small = 10000;
	public int reward_num = 10;
	public int reward_prob = 10;
	public int reward_prob_small = 1000;
	public int stop_attack_sec = 10;
	public float stop_attack_hp = 0.300000f;
	public float allow_attack_hp = 0.100000f;
	public int SKILL_display = 6234;
	public int TIMER_SAY = 33311;
	public int TIMER_check_hp = 33312;
	public int TIMER_RUNAWAY = 33313;
	public int TIMER_DESPAWN = 33314;
	public int debug_reward_prob = 50000;

	@Override
	protected void onEvtSpawn()
	{
		myself.i_ai5 = 0;
		myself.i_ai6 = 0;
		myself.i_ai7 = 0;
		super.onEvtSpawn();
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		if(myself.i_ai5 == 0)
		{
			myself.i_ai5 = 1;
			myself.AddTimerEx(TIMER_SAY, ((Rnd.get(5) + 3) * 1000));
		}
		else if(myself.i_ai5 == 1)
		{
			if(myself.getCurrentHp() < (myself.getMaxHp() * stop_attack_hp))
			{
				Say(MakeFString(1800832, "", "", "", "", ""));
				myself.AddTimerEx(TIMER_check_hp, (stop_attack_sec * 1000));
				myself.i_ai5 = 2;
				myself.i_ai6 = (int)myself.getCurrentHp();
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER_SAY)
		{
			if(myself.i_ai5 == 1)
			{
				int i0 = (Rnd.get(2) + 1);
				if(i0 == 1)
				{
					Say(MakeFString(1800833, "", "", "", "", ""));
				}
				else if(i0 == 2)
				{
					Say(MakeFString(1800834, "", "", "", "", ""));
				}
			}
			myself.AddTimerEx(TIMER_SAY, ((Rnd.get(5) + 5) * 1000));
		}
		else if(timer_id == TIMER_check_hp)
		{
			if((myself.i_ai6 - myself.getCurrentHp()) < (myself.getMaxHp() * (stop_attack_hp - allow_attack_hp)))
			{
				if((Rnd.get(100000) < reward_prob && Rnd.get(100000) < debug_reward_prob))
				{
					int i0 = (Rnd.get(2) + 1);
					if(i0 == 1)
					{
						Say(MakeFString(1800835,"","","","",""));
					}
					else if(i0 == 2)
					{
						Say(MakeFString(1800836,"","","","",""));
					}
					RemoveAllDesire(myself);
					StopMove(myself);
					myself.i_ai7 = 1;
					AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SKILL_display,1),1);
					AddTimerEx(TIMER_RUNAWAY,(3 * 1000));
				}
				else if((Rnd.get(100000) < reward_prob_small && Rnd.get(100000) < debug_reward_prob))
				{
					int i0 = (Rnd.get(2) + 1);
					if(i0 == 1)
					{
						Say(MakeFString(1800835,"","","","",""));
					}
					else if(i0 == 2)
					{
						Say(MakeFString(1800871,"","","","",""));
					}
					RemoveAllDesire(myself);
					StopMove(myself);
					myself.i_ai7 = 2;
					AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SKILL_display, 1), 1);
					myself.AddTimerEx(TIMER_RUNAWAY,(3 * 1000));
				}
				int i0 = (Rnd.get(2) + 1);
				if(i0 == 1)
				{
					Say(MakeFString(1800837,"","","","",""));
				}
				else if(i0 == 2)
				{
					Say(MakeFString(1800838,"","","","",""));
				}
				myself.AddTimerEx(TIMER_RUNAWAY,1000);
			}
		}
		else if(timer_id == TIMER_RUNAWAY)
		{
			RemoveAllDesire(myself);
			StopMove(myself);
			L2Character c0 = myself.GetLastAttacker();
			if(IsNullCreature(c0) == 0)
			{
				AddFleeDesire(myself.GetLastAttacker(), 10000000);
			}
			myself.AddTimerEx(TIMER_DESPAWN, 3000);
		}
		else if(timer_id == TIMER_DESPAWN)
		{
			Despawn(myself);
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		super.onEvtFinishCasting(skill, caster, target);
		if(SKILL_display != 0)
		{
			if(skill.getId() == SKILL_display)
			{
				L2Character c0 = myself.GetLastAttacker();
				for(int i0 = 0; i0 < reward_num;i0++)
				{
					if(myself.i_ai7 == 1)
					{
						if(IsNullCreature(c0) == 0)
						{
							DropItem2(getActor(), 57, reward_adena, c0.getPlayer());
						}
						else
						{
							DropItem1(getActor(), 57, reward_adena);
						}
					}
					else if(myself.i_ai7 == 2)
					{
						if(IsNullCreature(c0) == 0)
						{
							DropItem2(getActor(), 57, reward_adena_small, c0.getPlayer());
						}
						else
						{
							DropItem1(getActor(), 57, reward_adena_small);
						}
					}
				}
			}
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		myself.i_ai5 = 3;
		int i0 = (Rnd.get(2) + 1);
		if(i0 == 1)
		{
			Say(MakeFString(1800839, "", "", "", "", ""));
		}
		else if(i0 == 2)
		{
			Say(MakeFString(1800840, "", "", "", "", ""));
		}
	}
}
