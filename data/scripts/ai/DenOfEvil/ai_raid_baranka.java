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

public class ai_raid_baranka extends Mystic
{
	private L2NpcInstance myself = null;

	public ai_raid_baranka(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public String Privates1 = "baranka_confidential_re:DenOfEvil.ai_raid_baranka_confi:1:0sec;baranka_chamberlain_re:DenOfEvil.ai_nest_healer:1:0sec";
	public String Privates2 = "baranka_protecter_re:DenOfEvil.ai_raid_baranka_protector:1:0sec";
	public String Privates3 = "ragna_orc_hero_re:DenOfEvil.ai_raid_baranka_orc_warrior:1:0sec;ragna_orc_seer_re:DenOfEvil.ai_raid_baranka_orc_wizard:1:0sec";
	public int SKILL_dmg_shield = 6147;
	public int SKILL_dmg_shield_Prob = 500;
	public int SKILL_slow = 4203;
	public int SKILL_DD_ID = 4253;
	public int SKILL_DD_ID_Prob = 2000;
	public int SKILL_teleport_Prob = 500;
	public int SKILL_target_cancel = 302645249;
	public int ITEM_Pendant = 14848;
	public int TIMER_random_teleport = 31123;
	public int TIMER_spawn_protector = 31124;
	public int TIMER_regular_timer = 31125;
	public int TIMER_check_trr = 31126;
	public int TIMER_init = 31127;
	public int TIMER_spawn_orc = 31128;
	public int TIMER_repeat_shield = 31129;
	public int Pos_X1 = 74450;
	public int Pos_Y1 = -102185;
	public int Pos_Z1 = -960;
	public int Pos_X2 = 74445;
	public int Pos_Y2 = -101578;
	public int Pos_Z2 = -960;
	public int Pos_X3 = 73797;
	public int Pos_Y3 = -101913;
	public int Pos_Z3 = -960;
	public long i_quest8 = 0;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		myself.i_ai5 = 0;
		myself.i_ai6 = 0;
		myself.i_ai7 = 0;
		myself.i_ai8 = 0;
		myself.i_ai9 = 0;
		myself.i_ai4 = 0;
		i_quest8 = 0;
		myself.CreatePrivates(Privates1);
		if(myself.param1 == 1000)
		{
			L2Character c0 = GetCreatureFromIndex(myself.param2);
			if(IsNullCreature(c0) == 0)
			{
				if(c0.isPlayer())
				{
					MakeAttackEvent(c0, 2000, 0);
				}
				else if(c0.isPet() || c0.isSummon())
				{
					MakeAttackEvent(c0.getPet().getPlayer(), 2000, 0);
				}
			}
		}
		myself.AddTimerEx(TIMER_regular_timer,(60 * 1000));
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		int i0;
		int i1;
		i_quest8 = System.currentTimeMillis();
		if(myself.i_ai5 == 0)
		{
			myself.i_ai5 = 1;
			myself.AddTimerEx(TIMER_spawn_protector,(60 * 1000));
			myself.AddTimerEx(TIMER_check_trr,(10 * 1000));
		}
		if(myself.getCurrentHp() < (myself.getMaxHp() * 0.750000) && myself.i_ai6 == 0)
		{
			myself.i_ai6 = 1;
			myself.AddTimerEx(TIMER_spawn_orc,1);
		}
		else if(myself.getCurrentHp() < (myself.getMaxHp() * 0.500000) && myself.i_ai6 == 1)
		{
			myself.i_ai6 = 2;
			myself.AddTimerEx(TIMER_spawn_orc,1);
		}
		else if(myself.getCurrentHp() < (myself.getMaxHp() * 0.250000) && myself.i_ai6 == 2)
		{
			myself.i_ai6 = 3;
			myself.AddTimerEx(TIMER_spawn_orc,1);
		}
		else if(myself.i_ai4 == 1)
		{
			myself.i_ai4 = 0;
			MakeAttackEvent(myself.c_ai0, 100, 0);
		}
		else
		{
			AddAttackDesire(attacker, 1, (damage * 2));
			myself.c_ai0 = myself.top_desire_target();
			if(SKILL_DD_ID != 458752001)
			{
				if(Rnd.get(10000) < SKILL_DD_ID_Prob)
				{
					if(Skill_GetConsumeMP(SKILL_DD_ID) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_DD_ID) < myself.getCurrentHp() - damage && Skill_InReuseDelay(SKILL_DD_ID) == 0)
					{
						AddUseSkillDesire(myself.top_desire_target(), SkillTable.getInstance().getInfo(SKILL_DD_ID, 10), 1);
					}
				}
			}
			if(Rnd.get(10000) < SKILL_teleport_Prob)
			{
				RemoveAllDesire(myself);
				StopMove(myself);
				i0 = Rnd.get(3);
				if(myself.i_ai9 == i0)
				{
					i1 = Rnd.get(2);
					switch(myself.i_ai9)
					{
						case 0:
							if(i1 == 0)
							{
								myself.InstantTeleport(myself, Pos_X2, Pos_Y2, Pos_Z2);
								myself.i_ai9 = 1;
							}
							else if(i1 == 1)
							{
								myself.InstantTeleport(myself, Pos_X3, Pos_Y3, Pos_Z3);
								myself.i_ai9 = 2;
							}
							break;
						case 1:
							if(i1 == 0)
							{
								myself.InstantTeleport(myself, Pos_X1, Pos_Y1, Pos_Z1);
								myself.i_ai9 = 0;
							}
							else if(i1 == 1)
							{
								myself.InstantTeleport(myself, Pos_X3, Pos_Y3, Pos_Z3);
								myself.i_ai9 = 2;
							}
							break;
						case 2:
							if(i1 == 0)
							{
								myself.InstantTeleport(myself, Pos_X1, Pos_Y1, Pos_Z1);
								myself.i_ai9 = 0;
							}
							else if(i1 == 1)
							{
								myself.InstantTeleport(myself, Pos_X2, Pos_Y2, Pos_Z2);
								myself.i_ai9 = 1;
							}
							break;
					}
				}
				else
				{
					myself.i_ai9 = i0;
					switch(i0)
					{
						case 0:
							myself.InstantTeleport(myself, Pos_X1, Pos_Y1, Pos_Z1);
							break;
						case 1:
							myself.InstantTeleport(myself, Pos_X2, Pos_Y2, Pos_Z2);
							break;
						case 2:
							myself.InstantTeleport(myself, Pos_X3, Pos_Y3, Pos_Z3);
							break;
					}
				}
				if(IsNullCreature(myself.c_ai0) == 0)
				{
					myself.CastBuffForQuestReward(myself.c_ai0,SKILL_target_cancel);
				}
				if(myself.getCurrentHp() < (myself.getMaxHp() * 0.500000))
				{
					if(Skill_GetConsumeMP(SKILL_slow) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_slow) < myself.getCurrentHp() - damage && Skill_InReuseDelay(SKILL_slow) == 0)
					{
						AddUseSkillDesire(myself.c_ai0, SkillTable.getInstance().getInfo(SKILL_slow, 10), 1);
					}
				}
				myself.i_ai4 = 1;
				if(Skill_GetConsumeMP(SKILL_DD_ID) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_DD_ID) < myself.getCurrentHp() - damage && Skill_InReuseDelay(SKILL_DD_ID) == 0)
				{
					AddUseSkillDesire(myself.c_ai0, SkillTable.getInstance().getInfo(SKILL_DD_ID, 10), 1);
				}
			}
			if(myself.getCurrentHp() < (myself.getMaxHp() * 0.300000))
			{
				if(Rnd.get(10000) < SKILL_dmg_shield_Prob)
				{
					if(Skill_GetConsumeMP(SKILL_dmg_shield) < myself.getCurrentMp() &&Skill_GetConsumeHP(SKILL_dmg_shield) < myself.getCurrentHp() - damage && Skill_InReuseDelay(SKILL_dmg_shield) == 0)
					{
						AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(SKILL_dmg_shield,1),1);
					}
				}
			}
		}
		if(IsNullCreature(myself.top_desire_target()) == 0)
		{
			BroadcastScriptEventEx(2214008,myself.GetIndexFromCreature(myself),myself.GetIndexFromCreature(myself.top_desire_target()),3000);
		}
		else if(IsNullCreature(myself.c_ai0) == 0)
		{
			BroadcastScriptEventEx(2214008,myself.GetIndexFromCreature(myself),myself.GetIndexFromCreature(myself.c_ai0),3000);
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		if(skill.getId() == SKILL_DD_ID)
		{
			if(myself.i_ai4 == 1)
			{
				if(Skill_GetConsumeMP(SKILL_DD_ID) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_DD_ID) < myself.getCurrentHp() && Skill_InReuseDelay(SKILL_DD_ID) == 0)
				{
					AddUseSkillDesire(myself.c_ai0,SkillTable.getInstance().getInfo(SKILL_DD_ID,10),1);
				}
			}
		}
		super.onEvtFinishCasting(skill, caster,target);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TIMER_spawn_protector)
		{
			if(myself.i_ai5 == 1)
			{
				//maker0 = myself.GetMyMaker();
				//if((maker0.maximum_npc - maker0.i_ai0) >= 3)
				//{
					myself.CreatePrivates(Privates2);
				//}
				myself.AddTimerEx(TIMER_spawn_protector,(60 * 1000));
			}
		}
		else if(timer_id == TIMER_regular_timer)
		{
			if(myself.i_ai5 == 1)
			{
				if(((System.currentTimeMillis() - i_quest8) >= 300) && myself.c_ai0 != myself.GetLastAttacker())
				{
					myself.c_ai0 = null;
				}
				if(IsNullCreature(myself.c_ai0) == 0 || myself.c_ai0.isDead() || DistFromMe(myself.c_ai0) >= 3000)
				{
					myself.AddTimerEx(TIMER_init,1);
				}
			}
			myself.AddTimerEx(TIMER_regular_timer,(60 * 1000));
		}
		else if(timer_id == TIMER_check_trr)
		{
			if(myself.i_ai5 == 1)
			{
				// TODO: !!!
				/*if(InMyTerritory(myself) == 0)
				{
					Say(MakeFString(1800844, "", "", "", "", ""));
					myself.AddTimerEx(TIMER_init,1);
				}*/
				myself.AddTimerEx(TIMER_check_trr,(10 * 1000));
			}
			if(IsNullCreature(myself.top_desire_target()) == 0)
			{
				BroadcastScriptEventEx(2214008, GetIndexFromCreature(myself), GetIndexFromCreature(myself.top_desire_target()), 3000);
			}
			else if(IsNullCreature(myself.c_ai0) == 0)
			{
				BroadcastScriptEventEx(2214008, GetIndexFromCreature(myself), GetIndexFromCreature(myself.c_ai0), 3000);
			}
		}
		else if(timer_id == TIMER_spawn_orc)
		{
			RemoveAllDesire(myself);
			StopMove(myself);
			myself.InstantTeleport(myself, myself.getSpawnedLoc().x, myself.getSpawnedLoc().y, myself.getSpawnedLoc().z);
			if(Skill_GetConsumeMP(SKILL_dmg_shield) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_dmg_shield) < myself.getCurrentHp() && Skill_InReuseDelay(SKILL_dmg_shield) == 0)
			{
				AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SKILL_dmg_shield, 1), 1);
			}
			myself.i_ai7 = 1;
			myself.i_ai4 = 1;
			myself.CreatePrivates(Privates3);
			if(Skill_GetConsumeMP(SKILL_DD_ID) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_DD_ID) < myself.getCurrentHp() && Skill_InReuseDelay(SKILL_DD_ID) == 0)
			{
				AddUseSkillDesire(myself.c_ai0, SkillTable.getInstance().getInfo(SKILL_DD_ID, 10), 1);
			}
			myself.AddTimerEx(TIMER_repeat_shield,(10 * 1000));
		}
		else if(timer_id == TIMER_repeat_shield)
		{
			if(myself.i_ai7 == 1)
			{
				if(Skill_GetConsumeMP(SKILL_dmg_shield) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_dmg_shield)  < myself.getCurrentHp() && Skill_InReuseDelay(SKILL_dmg_shield) == 0)
				{
					AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SKILL_dmg_shield, 1), 1);
				}
				myself.AddTimerEx(TIMER_repeat_shield,(10 * 1000));
			}
		}
		else if(timer_id == TIMER_init)
		{
			BroadcastScriptEvent(2214007, 0, 4000);
			Despawn(myself);
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2214006)
		{
			myself.i_ai8 = (myself.i_ai8 + 1);
			if(myself.i_ai8 == 2)
			{
				myself.i_ai7 = 0;
				myself.i_ai8 = 0;
				myself.i_ai4 = 0;
				AddAttackDesire(myself.c_ai0, 1, 10000);
			}
		}
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		if(IsNullCreature(creature) == 0 && myself.i_ai4 != 1)
		{
			MakeAttackEvent(creature, 100, 0);
		}
	}

	@Override
	public void MY_DYING(L2Character c0)
	{
		if(IsNullCreature(myself.c_ai0) == 0)
		{
			if(myself.c_ai0.isPet() || myself.c_ai0.isSummon())
			{
				c0 = myself.c_ai0.getPet().getPlayer();
			}
			else
			{
				c0 = myself.c_ai0;
			}
			if(IsNullCreature(c0) == 0)
			{
				if(myself.OwnItemCount(c0, ITEM_Pendant) > 0)
				{
					myself.DeleteItem1(c0, ITEM_Pendant, 1);
				}
				//myself.SetAbilityItemDrop(0);
			}
		}
		BroadcastScriptEvent(2214007,0,4000);
	}
}
