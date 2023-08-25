package ai.DenOfEvil;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.util.*;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */
public class ai_nest_protector_of_altar extends DefaultAI
{
	private L2NpcInstance myself = null;

	public ai_nest_protector_of_altar(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int DeBuff = 402915329;
	public int ITEM_Pendant = 14848;
	public int TIMER_check_focus = 31152;
	public int TIMER_check_debuff = 31153;
	public int TIMER_decide_spawn = 31154;
	public int Pos_X = 71976;
	public int Pos_Y = -103426;
	public int Pos_Z = -968;
	public int Pos_baranka_X = 74710;
	public int Pos_baranka_Y = -101918;
	public int Pos_baranka_Z = -960;
	public int Pos_inv_X = 72717;
	public int Pos_inv_Y = -102009;
	public int Pos_inv_Z = -960;
	public int debug_mode = 0;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		myself.i_ai5 = 0;
		myself.i_ai6 = 0;
		myself.i_ai7 = 0;
		myself.c_ai0 = null;
		myself.c_ai1 = null;
		//SetMaxHateListSize(200);
		AddTimerEx(TIMER_check_debuff,(10 * 1000));
		myself.InstantTeleportInMyTerritory(Pos_X,Pos_Y,Pos_Z,100);
		myself.CreateOnePrivateEx(1032656,"DenOfEvil.ai_raid_baranka_observer",0,0,Pos_inv_X,Pos_inv_Y,Pos_inv_Z,0,0,0,0);
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		super.ATTACKED(attacker, damage, skill);
		L2Character c0 = null;
		if(attacker.isPlayer())
		{
			c0 = attacker;
		}
		else if(attacker.isPet() || attacker.isSummon())
		{
			c0 = attacker.getPet().getPlayer();
		}
		if(IsNullCreature(myself.c_ai0) == 1)
		{
			if(myself.OwnItemCount(c0,ITEM_Pendant) > 0)
			{
				myself.c_ai0 = c0;
				myself.i_ai5 = 1;
				myself.i_ai6 = myself.GetCurrentTick();
			}
		}
		if(c0 != myself.c_ai0)
		{
			if(IsNullCreature(c0) == 0)
			{
				myself.CastBuffForQuestReward(c0,DeBuff);
			}
		}
		else if(myself.OwnItemCount(c0,ITEM_Pendant) <= 0)
		{
		}
		else if(myself.getCurrentHp() < (myself.getMaxHp() * 0.050000))
		{
			if(myself.i_ai7 == 0)
			{
				if(c0 == myself.c_ai0)
				{
					if(IsNullCreature(myself.c_ai1) > 0)
					{
						myself.i_ai7 = 1;
						myself.c_ai1 = myself.c_ai0;
						myself.AddTimerEx(TIMER_decide_spawn,1000);
					}
				}
			}
		}
	}

	@Override
	public void SEE_CREATURE(L2Character creature)
	{
		L2Character c0 = null;
		//AddHateInfo(creature,1,0,1,1);
		creature.addDamageHate(myself, 0, 1);
		if(creature.isPlayer() || creature.isPet() || creature.isSummon())
		{
			if(creature.isPlayer())
			{
				c0 = creature;
			}
			else if(creature.getPet() != null)
			{
				c0 = creature.getPet().getPlayer();
			}
		}
		if(myself.i_ai5 == 0)
		{
			if(c0 != null && myself.OwnItemCount(c0,ITEM_Pendant) > 0)
			{
				myself.c_ai0 = c0;
				myself.i_ai5 = 1;
				myself.i_ai6 = myself.GetCurrentTick();
				myself.AddTimerEx(TIMER_check_focus,(10 * 1000));
				myself.ShowOnScreenMsgStr(myself.c_ai0,5,0,1,0,1,0,5000,0,MakeFString(1800831,"","","","",""));
			}
			else
			{
				if(IsNullCreature(c0) == 0)
				{
					myself.CastBuffForQuestReward(c0,DeBuff);
					myself.InstantTeleportInMyTerritory(Pos_X,Pos_Y,Pos_Z,0);
				}
			}
		}
		else if(myself.i_ai5 == 1)
		{
			if(c0 != myself.c_ai0 && IsNullCreature(c0) == 0)
			{
				myself.CastBuffForQuestReward(c0,DeBuff);
				myself.InstantTeleportInMyTerritory(Pos_X,Pos_Y,Pos_Z,0);
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		int i0;
		int i1;
		if(timer_id == TIMER_check_focus)
		{
			if( myself.i_ai7 == 1 )
			{
			}
			else
			{
				if(IsNullCreature(myself.c_ai0) == 1 || myself.c_ai0.isDead() || DistFromMe(myself.c_ai0) >= 3000 || (myself.GetCurrentTick() - myself.i_ai6) >= 300)
				{
					Despawn(myself);
				}
				else
				{
					myself.AddTimerEx(TIMER_check_focus,(10 * 1000));
				}
			}
		}
		else if(timer_id == TIMER_decide_spawn)
		{
			myself.CreateOnePrivateEx(1018808,"DenOfEvil.ai_raid_baranka",0,0,Pos_baranka_X,Pos_baranka_Y,Pos_baranka_Z,0,1000,GetIndexFromCreature(myself.c_ai1),0);
		}
		else if(timer_id == TIMER_check_debuff)
		{
			if(myself.getAggroMap().size() > 0)
			{
				for(AggroInfo aggro : myself.getAggroMap().values())
				{
					if(aggro != null && aggro.attacker != null)
					{
						if(IsNullCreature(aggro.attacker) == 1 || DistFromMe(aggro.attacker) >= 2500)
						{
							//myself.getAggroMap().remove(aggro);
							myself.RemoveHateInfoByCreature(aggro.attacker);
						}
						else if(myself.c_ai0 == aggro.attacker)
						{
							//myself.getAggroMap().remove(aggro);
							myself.RemoveHateInfoByCreature(aggro.attacker);
						}
						else if((aggro.attacker.isPet() || aggro.attacker.isSummon()) && myself.c_ai0 == aggro.attacker.getPlayer())
						{
							//myself.getAggroMap().remove(aggro);
							myself.RemoveHateInfoByCreature(aggro.attacker);
						}
						else
						{
							myself.CastBuffForQuestReward(aggro.attacker,DeBuff);
						}
					}
				}
			}
			myself.AddTimerEx(TIMER_check_debuff,(10 * 1000));
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2214007)
		{
			Suicide(myself);
		}
		else if(script_event_arg1 == 2214009)
		{
			L2Character c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				if(myself.OwnItemCount(c0,ITEM_Pendant) <= 0 || (myself.OwnItemCount(c0,ITEM_Pendant) > 0 && IsNullCreature(myself.c_ai0) == 0))
				{
					//AddHateInfo(c0,1,0,1,1);
					c0.addDamageHate(myself, 0, 1);
				}
			}
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}
