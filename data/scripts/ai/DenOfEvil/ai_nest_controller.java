package ai.DenOfEvil;

import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Drizzy
 * @date: 27.02.2013
 */

public class ai_nest_controller extends DefaultAI
{
	private L2NpcInstance myself = null;

	public ai_nest_controller(L2Character self)
	{
		super(self);
		myself = (L2NpcInstance)self;
	}

	public int GroupNum = -1;
	public String my_trr = "";
	public int TIMER_CHECK_20SEC = 33120;
	public int TIMER_SPAWN_PRIVATE = 33124;
	public int TIMER_DESTROY_CAMP = 33125;
	public int SKILL_camp_destroy = 6149;
	public int Spot1_x = -1;
	public int Spot1_y = -1;
	public int Spot1_z = -1;
	public int Spot1_dir = -1;
	public int Spot2_x = -1;
	public int Spot2_y = -1;
	public int Spot2_z = -1;
	public int Spot2_dir = -1;
	public int Spot3_x = -1;
	public int Spot3_y = -1;
	public int Spot3_z = -1;
	public int Spot3_dir = -1;
	public int Spot4_x = -1;
	public int Spot4_y = -1;
	public int Spot4_z = -1;
	public int Spot4_dir = -1;
	public List<Integer> int_list = new ArrayList<Integer>();

	/*******************************************************************************/
	public String Area_a_pc_lv1 = "";
	public String Area_a_pc_lv2 = "";
	public String Area_a_pc_lv3 = "";
	public String Area_a_pc_lv4 = "";
	public String Area_b_pc_lv1 = "";
	public String Area_b_pc_lv2 = "";
	public String Area_b_pc_lv3 = "";
	public String Area_b_pc_lv4 = "";
	public String Area_c_pc_lv1 = "";
	public String Area_c_pc_lv2 = "";
	public String Area_c_pc_lv3 = "";
	public String Area_c_pc_lv4 = "";
	public String Area_a_npc_lv1 = "";
	public String Area_a_npc_lv2 = "";
	public String Area_a_npc_lv3 = "";
	public String Area_a_npc_lv4 = "";
	public String Area_b_npc_lv1 = "";
	public String Area_b_npc_lv2 = "";
	public String Area_b_npc_lv3 = "";
	public String Area_b_npc_lv4 = "";
	public String Area_c_npc_lv1 = "";
	public String Area_c_npc_lv2 = "";
	public String Area_c_npc_lv3 = "";
	public String Area_c_npc_lv4 = "";
	/*******************************************************************************/

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		int_list.clear();
		for(int i0 = 0; i0 < 4;i0++)
		{
			int_list.add(i0);
		}
		myself.AddTimerEx(TIMER_SPAWN_PRIVATE, 1);
		myself.AddTimerEx(TIMER_CHECK_20SEC, 1000);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		int i0=0;
		int i1=0;
		int i2=0;
		int i3=0;
		int i4=0;
		if(timer_id == TIMER_CHECK_20SEC)
		{
			if(GetDateTime(0,5) == 0 || GetDateTime(0, 5) == 20 || GetDateTime(0, 5) == 40)
				myself.AddTimerEx(TIMER_SPAWN_PRIVATE,1);
			myself.AddTimerEx(TIMER_CHECK_20SEC, 1000);
		}
		if(timer_id == TIMER_SPAWN_PRIVATE)
		{
			if(int_list.size() > 0)
			{
				for(i0 = 0; i0 < int_list.size();i0++)
				{
					switch(int_list.get(i0))
					{
						case 0:
							i1 = Spot1_x;
							i2 = Spot1_y;
							i3 = Spot1_z;
							i4 = Spot1_dir;
							break;
						case 1:
							i1 = Spot2_x;
							i2 = Spot2_y;
							i3 = Spot2_z;
							i4 = Spot2_dir;
							break;
						case 2:
							i1 = Spot3_x;
							i2 = Spot3_y;
							i3 = Spot3_z;
							i4 = Spot3_dir;
							break;
						case 3:
							i1 = Spot4_x;
							i2 = Spot4_y;
							i3 = Spot4_z;
							i4 = Spot4_dir;
							break;
					}
					switch((Rnd.get(3)+1))
					{
						case 1:
							myself.CreateOnePrivateEx(1018812, "DenOfEvil.ai_nest_observer", 0, 0, i1, i2, i3, i4, GroupNum, int_list.get(i0), 1);
							break;
						case 2:
							myself.CreateOnePrivateEx(1018813, "DenOfEvil.ai_nest_observer", 0, 0, i1, i2, i3, i4, GroupNum, int_list.get(i0), 2);
							break;
						case 3:
							myself.CreateOnePrivateEx(1018814, "DenOfEvil.ai_nest_observer", 0, 0, i1, i2, i3, i4, GroupNum, int_list.get(i0), 3);
							break;
					}
				}
				int_list.clear();
			}
		}
		else if(timer_id == TIMER_DESTROY_CAMP)
		{
			if(Skill_GetConsumeMP(SKILL_camp_destroy) < myself.getCurrentMp() && Skill_GetConsumeHP(SKILL_camp_destroy) < myself.getCurrentHp() && Skill_InReuseDelay(SKILL_camp_destroy) == 0)
			{
				AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(SKILL_camp_destroy, 1), 1);
			}
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2214004)
		{
			if(GroupNum == script_event_arg2)
			{
				int_list.add(script_event_arg3);
			}
		}
		else if(script_event_arg1 == 2214010)
		{
			switch(script_event_arg2)
			{
				case 1:
					switch(myself.i_ai2)
					{
						case 0:
							Area_SetOnOff(Area_a_pc_lv1,1);
							Area_SetOnOff(Area_a_npc_lv1,1);
							myself.i_ai2 = 1;
							break;
						case 1:
							Area_SetOnOff(Area_a_pc_lv1,0);
							Area_SetOnOff(Area_a_npc_lv1,0);
							Area_SetOnOff(Area_a_pc_lv2,1);
							Area_SetOnOff(Area_a_npc_lv2,1);
							myself.i_ai2 = 2;
							break;
						case 2:
							Area_SetOnOff(Area_a_pc_lv2,0);
							Area_SetOnOff(Area_a_npc_lv2,0);
							Area_SetOnOff(Area_a_pc_lv3,1);
							Area_SetOnOff(Area_a_npc_lv3,1);
							myself.i_ai2 = 3;
							break;
						case 3:
							Area_SetOnOff(Area_a_pc_lv3,0);
							Area_SetOnOff(Area_a_npc_lv3,0);
							Area_SetOnOff(Area_a_pc_lv4,1);
							Area_SetOnOff(Area_a_npc_lv4,1);
							myself.i_ai2 = 4;
							break;
					}
					break;
				case 2:
					switch(myself.i_ai3)
					{
						case 0:
							Area_SetOnOff(Area_b_pc_lv1,1);
							Area_SetOnOff(Area_b_npc_lv1,1);
							myself.i_ai3 = 1;
							break;
						case 1:
							Area_SetOnOff(Area_b_pc_lv1,0);
							Area_SetOnOff(Area_b_npc_lv1,0);
							Area_SetOnOff(Area_b_pc_lv2,1);
							Area_SetOnOff(Area_b_npc_lv2,1);
							myself.i_ai3 = 2;
							break;
						case 2:
							Area_SetOnOff(Area_b_pc_lv2,0);
							Area_SetOnOff(Area_b_npc_lv2,0);
							Area_SetOnOff(Area_b_pc_lv3,1);
							Area_SetOnOff(Area_b_npc_lv3,1);
							myself.i_ai3 = 3;
							break;
						case 3:
							Area_SetOnOff(Area_b_pc_lv3,0);
							Area_SetOnOff(Area_b_npc_lv3,0);
							Area_SetOnOff(Area_b_pc_lv4,1);
							Area_SetOnOff(Area_b_npc_lv4,1);
							myself.i_ai3 = 4;
							break;
					}
					break;
				case 3:
					switch(myself.i_ai4)
					{
						case 0:
							Area_SetOnOff(Area_c_pc_lv1,1);
							Area_SetOnOff(Area_c_npc_lv1,1);
							myself.i_ai4 = 1;
							break;
						case 1:
							Area_SetOnOff(Area_c_pc_lv1,0);
							Area_SetOnOff(Area_c_npc_lv1,0);
							Area_SetOnOff(Area_c_pc_lv2,1);
							Area_SetOnOff(Area_c_npc_lv2,1);
							myself.i_ai4 = 2;
							break;
						case 2:
							Area_SetOnOff(Area_c_pc_lv2,0);
							Area_SetOnOff(Area_c_npc_lv2,0);
							Area_SetOnOff(Area_c_pc_lv3,1);
							Area_SetOnOff(Area_c_npc_lv3,1);
							myself.i_ai4 = 3;
							break;
						case 3:
							Area_SetOnOff(Area_c_pc_lv3,0);
							Area_SetOnOff(Area_c_npc_lv3,0);
							Area_SetOnOff(Area_c_pc_lv4,1);
							Area_SetOnOff(Area_c_npc_lv4,1);
							myself.i_ai4 = 4;
							break;
					}
					break;
			}
		}
		else if(script_event_arg1 == 2214011)
		{
			switch(script_event_arg2)
			{
				case 1:
					switch(myself.i_ai2)
					{
						case 1:
							Area_SetOnOff(Area_a_pc_lv1,0);
							Area_SetOnOff(Area_a_npc_lv1,0);
							myself.i_ai2 = 0;
							break;
						case 2:
							Area_SetOnOff(Area_a_pc_lv2,0);
							Area_SetOnOff(Area_a_npc_lv2,0);
							Area_SetOnOff(Area_a_pc_lv1,1);
							Area_SetOnOff(Area_a_npc_lv1,1);
							myself.i_ai2 = 1;
							break;
						case 3:
							Area_SetOnOff(Area_a_pc_lv3,0);
							Area_SetOnOff(Area_a_npc_lv3,0);
							Area_SetOnOff(Area_a_pc_lv2,1);
							Area_SetOnOff(Area_a_npc_lv2,1);
							myself.i_ai2 = 2;
							break;
						case 4:
							Area_SetOnOff(Area_a_pc_lv4,0);
							Area_SetOnOff(Area_a_npc_lv4,0);
							Area_SetOnOff(Area_a_pc_lv3,1);
							Area_SetOnOff(Area_a_npc_lv3,1);
							myself.i_ai2 = 3;
							break;
					}
					break;
				case 2:
					switch(myself.i_ai3)
					{
						case 1:
							Area_SetOnOff(Area_b_pc_lv1,0);
							Area_SetOnOff(Area_b_npc_lv1,0);
							myself.i_ai3 = 0;
							break;
						case 2:
							Area_SetOnOff(Area_b_pc_lv2,0);
							Area_SetOnOff(Area_b_npc_lv2,0);
							Area_SetOnOff(Area_b_pc_lv1,1);
							Area_SetOnOff(Area_b_npc_lv1,1);
							myself.i_ai3 = 1;
							break;
						case 3:
							Area_SetOnOff(Area_b_pc_lv3,0);
							Area_SetOnOff(Area_b_npc_lv3,0);
							Area_SetOnOff(Area_b_pc_lv2,1);
							Area_SetOnOff(Area_b_npc_lv2,1);
							myself.i_ai3 = 2;
							break;
						case 4:
							Area_SetOnOff(Area_b_pc_lv4,0);
							Area_SetOnOff(Area_b_npc_lv4,0);
							Area_SetOnOff(Area_b_pc_lv3,1);
							Area_SetOnOff(Area_b_npc_lv3,1);
							myself.i_ai3 = 3;
							break;
					}
					break;
				case 3:
					switch(myself.i_ai4)
					{
						case 1:
							Area_SetOnOff(Area_c_pc_lv1,0);
							Area_SetOnOff(Area_c_npc_lv1,0);
							myself.i_ai4 = 0;
							break;
						case 2:
							Area_SetOnOff(Area_c_pc_lv2,0);
							Area_SetOnOff(Area_c_npc_lv2,0);
							Area_SetOnOff(Area_c_pc_lv1,1);
							Area_SetOnOff(Area_c_npc_lv1,1);
							myself.i_ai4 = 1;
							break;
						case 3:
							Area_SetOnOff(Area_c_pc_lv3,0);
							Area_SetOnOff(Area_c_npc_lv3,0);
							Area_SetOnOff(Area_c_pc_lv2,1);
							Area_SetOnOff(Area_c_npc_lv2,1);
							myself.i_ai4 = 2;
							break;
						case 4:
							Area_SetOnOff(Area_c_pc_lv4,0);
							Area_SetOnOff(Area_c_npc_lv4,0);
							Area_SetOnOff(Area_c_pc_lv3,1);
							Area_SetOnOff(Area_c_npc_lv3,1);
							myself.i_ai4 = 3;
							break;
					}
					break;
			}
		}
		else if(script_event_arg1 == 2214002)
		{
			if(GroupNum == script_event_arg2)
			{
				if(script_event_arg3 == 0)
				{
					BroadcastScriptEvent(2214003, GroupNum, 4000);
					myself.AddTimerEx(TIMER_DESTROY_CAMP, (3 * 1000));
				}
				else if(script_event_arg3 == 3)
				{
					ShowMsgInTerritory(0, my_trr, 3022);
				}
				else if(script_event_arg3 == 2)
				{
					ShowMsgInTerritory(0, my_trr, 3023);
				}
				else if(script_event_arg3 == 1)
				{
					ShowMsgInTerritory(0, my_trr, 3024);
				}
			}
		}
	}
}
