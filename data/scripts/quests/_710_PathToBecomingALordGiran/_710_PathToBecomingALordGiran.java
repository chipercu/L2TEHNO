package quests._710_PathToBecomingALordGiran;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Гиран, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _710_PathToBecomingALordGiran extends Quest implements ScriptFile
{
	private static final int[] mobs = { 21631, 20835, 20832, 20843, 21625, 21604, 21607, 20833, 21610, 20847, 20839, 21613, 20988, 20987, 20986, 21619, 20845, 20836, 20842, 21628, 20844, 21622, 21616, 20841, 20846, 20840, 21637, 21634 };

	private static final int wharf_manager_felton = 30879;
	private static final int warehouse_chief_gesto = 30511;
	private static final int chamberlain_saul = 35184;
	private static final int box_of_secret_q065 = 32243;

	public _710_PathToBecomingALordGiran()
	{
		super(false);
		addStartNpc(chamberlain_saul, warehouse_chief_gesto);
		addTalkId(wharf_manager_felton, box_of_secret_q065);
		addKillId(mobs);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int i0 = 0;
		int i1 = 0;
		int _code = 0;

		L2Player c0 = null;
		L2Player talker = st.getPlayer();

		switch(npc.getNpcId())
		{
			case wharf_manager_felton:
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 4 && npc.IsMyLord(talker) == 1)
					_code = 4;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 5 && npc.IsMyLord(talker) == 1)
					_code = 5;

				switch(_code)
				{
					case 4:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 4 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"wharf_manager_felton_q0710_01.htm");
						}
						break;
					case 5:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 5 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"wharf_manager_felton_q0710_03.htm");
						}
						break;
				}
				break;
			case warehouse_chief_gesto:
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 3 && npc.IsMyLord(talker) == 1)
					_code = 3;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 3 && npc.GetMemoState(talker,710) < 6 && npc.IsMyLord(talker) == 1)
					_code = 4;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 5 && npc.GetMemoState(talker,710) < 9 && npc.IsMyLord(talker) == 1)
					_code = 5;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 9 && npc.IsMyLord(talker) == 1)
					_code = 6;
				if(npc.IsMyLord(talker) == 0)
					_code = 7;

				switch(_code)
				{
					case 3:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 3 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_gesto_q0710_01.htm");
						}
						break;
					case 4:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 3 && npc.GetMemoState(talker,710) < 6 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_gesto_q0710_04.htm");
						}
						break;
					case 5:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 5 && npc.GetMemoState(talker,710) < 9 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_gesto_q0710_05.htm");
						}
						break;
					case 6:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 9 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.OwnItemCount(talker,13013) >= 300)
							{
								npc.DeleteItem1(talker,13013,npc.OwnItemCount(talker,13013));
								npc.ShowPage(talker,"warehouse_chief_gesto_q0710_08.htm");
								npc.SetFlagJournal(talker,710,9);
								npc.ShowQuestMark(talker,710);
								npc.SoundEffect(talker,"ItemSound.quest_middle");
								npc.SetMemoState(talker,710,10);
							}
							else
							{
								npc.ShowPage(talker,"warehouse_chief_gesto_q0710_09.htm");
							}
						}
						break;
					case 7:
						if(npc.IsMyLord(talker) == 0)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							c0 = npc.Pledge_GetLeader(talker);
							if(npc.IsNullCreature(c0) == 0)
							{
								if(npc.IsMyLord(c0) == 1)
								{
									if(npc.HaveMemo(c0,710) == 1 && npc.GetMemoState(c0,710) == 6)
									{
										if(npc.OwnItemCount(talker,13014) >= 1)
										{
											i1 = talker.getObjectId();
											npc.SetMemoState(c0,710,7);
											npc.SetMemoStateEx(c0,710,1,i1);
											npc.SetFlagJournal(c0,710,6);
											npc.ShowQuestMark(c0,710);
											npc.SoundEffect(c0,"ItemSound.quest_middle");
											npc.ShowPage(talker,"warehouse_chief_gesto_q0710_06.htm");
											npc.DeleteItem1(talker,13014,npc.OwnItemCount(talker,13014));
										}
										else
										{
											npc.ShowPage(talker,"warehouse_chief_gesto_q0710_06a.htm");
										}
									}
									else if(npc.HaveMemo(c0,710) == 1 && npc.GetMemoState(c0,710) > 6 && npc.GetMemoState(c0,710) <= 8)
									{
										npc.ShowPage(talker,"warehouse_chief_gesto_q0710_07.htm");
									}
								}
							}
						}
						break;
				}
				break;
			case chamberlain_saul:
				if(npc.HaveMemo(talker,710) == 0 && npc.IsMyLord(talker) == 1)
					_code = 0;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 1)
					_code = 1;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 3 && npc.IsMyLord(talker) == 1)
					_code = 2;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 3 && npc.GetMemoState(talker,710) < 10 && npc.IsMyLord(talker) == 1)
					_code = 3;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 10 && npc.IsMyLord(talker) == 1)
					_code = 4;

				switch(_code)
				{
					case 0:
						if(npc.HaveMemo(talker,710) == 0 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(83) == 0)
								{
									npc.ShowQuestPage(talker,"chamberlain_saul_q0710_01.htm",710);
								}
								else
								{
									npc.ShowQuestPage(talker,"chamberlain_saul_q0710_03.htm",710);
								}
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 1:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if((npc.GetCurrentTick() - npc.GetMemoStateEx(talker,710,1)) < 60)
							{
								npc.ShowPage(talker,"chamberlain_saul_q0710_05.htm");
							}
							else
							{
								npc.SetMemoState(talker,710,3);
								npc.SetMemoStateEx(talker,710,1,0);
								npc.ShowPage(talker,"chamberlain_saul_q0710_06.htm");
								npc.SetFlagJournal(talker,710,2);
								npc.ShowQuestMark(talker,710);
								npc.SoundEffect(talker,"ItemSound.quest_middle");
							}
						}
						break;
					case 2:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 3 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_saul_q0710_08.htm");
						}
						break;
					case 3:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 3 && npc.GetMemoState(talker,710) < 10 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_saul_q0710_09.htm");
						}
						break;
					case 4:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 10 && npc.IsMyLord(talker) == 1)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetDominionWarState(83) == 5 || npc.Castle_IsUnderSiege())
							{
								npc.ShowPage(talker,"chamberlain_saul_q0710_10a.htm");
							}
							else if(npc.Fortress_GetContractStatus(104) == -1)
							{
								npc.ShowPage(talker,"chamberlain_saul_q0710_10b.htm");
							}
							else
							{
								npc.ShowPage(talker,"chamberlain_saul_q0710_10.htm");
							}
						}
						break;
				}
				break;
			case box_of_secret_q065:
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 5)
					_code = 3;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 6)
					_code = 4;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 7)
					_code = 5;
				if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 7)
					_code = 6;

				switch(_code)
				{
					case 3:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 5)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.GiveItem1(talker,13014,1);
							npc.SetMemoState(talker,710,6);
							npc.ShowPage(talker,"box_of_secret_q065_q0710_01.htm");
							npc.SetFlagJournal(talker,710,5);
							npc.ShowQuestMark(talker,710);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 4:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 6)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.OwnItemCount(talker,13014) > 0)
							{
								npc.ShowPage(talker,"box_of_secret_q065_q0710_02.htm");
							}
							else if((npc.GetCurrentTick() - talker.quest_last_reward_time) > 600)
							{
								talker.quest_last_reward_time = npc.GetCurrentTick();
								npc.ShowPage(talker,"box_of_secret_q065_q0710_02a.htm");
								npc.GiveItem1(talker,13014,1);
							}
							else
							{
								npc.ShowPage(talker,"box_of_secret_q065_q0710_02b.htm");
							}
						}
						break;
					case 5:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) == 7)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.SetMemoState(talker,710,8);
							npc.SetMemoStateEx(talker,710,1,0);
							npc.ShowPage(talker,"box_of_secret_q065_q0710_03.htm");
							npc.SetFlagJournal(talker,710,7);
							npc.ShowQuestMark(talker,710);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 6:
						if(npc.HaveMemo(talker,710) == 1 && npc.GetMemoState(talker,710) > 7)
						{
							npc.SetCurrentQuestID(710);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"box_of_secret_q065_q0710_05.htm");
						}
						break;
				}
				break;
		}
		return null;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(Util.contains_int(mobs, npc.getNpcId()))
		{
			L2Player target = st.getPlayer();
			if(npc.IsNullCreature(target) == 0 && npc.DistFromMe(target) <= 1500)
			{
				L2Player c0 = npc.Pledge_GetLeader(target);
				if(npc.IsNullCreature(c0) == 0)
				{
					int i1 = npc.GetMemoStateEx(c0,710,1);
					if(npc.HaveMemo(c0,710) == 1 && npc.GetMemoState(c0,710) >= 8)
					{
						if(i1 >= 299)
						{
							if(npc.GetMemoState(c0,710) == 8)
							{
								npc.SetFlagJournal(c0,710,8);
								npc.ShowQuestMark(c0,710);
								npc.SoundEffect(c0,"ItemSound.quest_middle");
								npc.SetMemoState(c0,710,9);
								npc.SetMemoStateEx(c0,710,1,(i1 + 1));
							}
						}
						else
						{
							npc.SetMemoStateEx(c0,710,1,(i1 + 1));
							npc.SoundEffect(c0,"ItemSound.quest_itemget");
						}
						npc.GiveItem1(target,13013,1);
					}
				}
			}
		}
		return null;
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}