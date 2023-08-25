package ai.SeedOfAnnihilation;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.*;

/**
 * Zapizdoval AI Diagod.
 * open-team.ru
 **/
// 22756

public class tardion extends Fighter
{
	public tardion(L2NpcInstance self)
	{
		super(self);
	}

	/*@Override
	public void MY_DYING(L2Character last_attacker, L2Party lparty)
	{
		CCodeInfoList always_list;
		CCodeInfo code_info;
		int i0;
		int i1;
		int i9;
		int member;
		CCodeInfoList random1_list;
		L2Character target;
		always_list = gg.AllocCodeInfoList();
		random1_list = gg.AllocCodeInfoList();
		target = last_attacker;
		if(gg.HaveMemo(target,692) == 1 && gg.GetMemoState(target,692) == 3)
		{
			random1_list.SetInfo(0,target);
		}
		if(gg.HaveMemo(target,692) == 1 && gg.GetMemoState(target,692) == 3)
		{
			random1_list.SetInfo(0,target);
		}
		if(gg.IsNull(lparty) == 0)
		{
			for(i9 = 0; i9 < lparty.getMemberCount();i9++)
			{
				target = myself.GetMemberOfParty(lparty,i9);
				if(gg.HaveMemo(target,692) == 1 && gg.GetMemoState(target,692) == 3)
				{
					random1_list.SetInfo(0,target);
				}
			}
		}
		target = last_attacker;
		if(gg.HaveMemo(target,453) == 1 && gg.GetMemoState(target,453) == 1 && gg.GetMemoStateEx(target,453,1) == 2)
		{
			always_list.SetInfo(1,target);
		}
		if(gg.HaveMemo(target,453) == 1 && gg.GetMemoState(target,453) == 1 && gg.GetMemoStateEx(target,453,1) == 2)
		{
			always_list.SetInfo(1,target);
		}
		if(gg.IsNull(lparty) == 0)
		{
			for(i9 = 0; i9 < lparty.getMemberCount();i9++)
			{
				target = myself.GetMemberOfParty(lparty,i9);
				if(gg.HaveMemo(target,453) == 1 && gg.GetMemoState(target,453) == 1 && gg.GetMemoStateEx(target,453,1) == 2)
				{
					always_list.SetInfo(1,target);
				}
			}
		}
		while(gg.IsNull(code_info = always_list.Next()) == 0)
		{
			if(code_info.code == 1)
			{
				myself.SetCurrentQuestID(453);
				while(gg.IsNull(target = code_info.Next()) == 0)
				{
					if(myself.DistFromMe(target) <= 1500)
					{
						myself.SetCurrentQuestID(453);
						myself.IncreaseNPCLogByID(target,453,0,1022756,20);
						if(myself.GetNPCLogByID(target,453,0,1022754) >= 20 && myself.GetNPCLogByID(target,453,0,1022755) >= 20 && myself.GetNPCLogByID(target,453,0,1022756) >= 20)
						{
							myself.SetMemoState(target,453,2);
							myself.SetFlagJournal(target,453,5);
							myself.ShowQuestMark(target,453);
							myself.SoundEffect(target,"ItemSound.quest_middle");
						}
						else
						{
							myself.SoundEffect(target,"ItemSound.quest_itemget");
						}
					}
				}
			}
		}
		code_info = random1_list.RandomSelectOne();
		if(gg.IsNull(code_info) == 0)
		{
			if(code_info.code == 0)
			{
				myself.SetCurrentQuestID(692);
				while(gg.IsNull(target = code_info.Next()) == 0)
				{
					if(myself.DistFromMe(target) <= 1500)
					{
						myself.SetCurrentQuestID(692);
						if(myself.DistFromMe(target) <= 1500)
						{
							i0 = gg.Rand(1000);
							if(i0 < 165)
							{
								myself.GiveItem1(target,15536,1);
								myself.SoundEffect(target,"ItemSound.quest_itemget");
							}
						}
					}
				}
			}
		}
		super.MY_DYING(last_attacker, lparty);
	}*/
}
