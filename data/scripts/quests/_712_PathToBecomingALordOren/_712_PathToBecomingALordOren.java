package quests._712_PathToBecomingALordOren;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Орен, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _712_PathToBecomingALordOren extends Quest implements ScriptFile
{
	private static final int chamberlain_brasseur = 35226;
	private static final int warehouse_chief_croop = 30676;
	private static final int marty = 30169;
	private static final int yan = 30176;

	private static final int[] mobs = { 21261, 20576, 20575, 20161 };

	public _712_PathToBecomingALordOren()
	{
		super(false);
		addStartNpc(chamberlain_brasseur, marty);
		addTalkId(warehouse_chief_croop, yan);
		addKillId(mobs);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _code = -1;
		int _choiceN = 0;

		L2Player talker = st.getPlayer();
		switch(npc.getNpcId())
		{
			case chamberlain_brasseur:
				if(npc.HaveMemo(talker,712) == 0 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Oren");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 1 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 2 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 3 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 3;
					npc.AddChoice(3,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) > 3 && npc.GetMemoState(talker,712) < 9 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 4;
					npc.AddChoice(4,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 9 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 5;
					npc.AddChoice(5,"Path to Becoming a Lord - Oren (In progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,712) == 0 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(84) == 0)
								{
									npc.ShowQuestPage(talker,"chamberlain_brasseur_q0712_01.htm",712);
								}
								else
								{
									npc.ShowQuestPage(talker,"chamberlain_brasseur_q0712_03.htm",712);
								}
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 1 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if((npc.GetCurrentTick() - npc.GetMemoStateEx(talker,712,1)) < 60)
							{
								npc.ShowPage(talker,"chamberlain_brasseur_q0712_05.htm");
							}
							else
							{
								npc.SetMemoState(talker,712,3);
								npc.SetMemoStateEx(talker,712,1,0);
								npc.ShowPage(talker,"chamberlain_brasseur_q0712_06.htm");
								npc.SetFlagJournal(talker,712,2);
								npc.ShowQuestMark(talker,712);
								npc.SoundEffect(talker,"ItemSound.quest_middle");
							}
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 2 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.SetMemoState(talker,712,3);
							npc.ShowPage(talker,"chamberlain_brasseur_q0712_07.htm");
							npc.SetFlagJournal(talker,712,2);
							npc.ShowQuestMark(talker,712);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 3:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 3 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_brasseur_q0712_08.htm");
						}
						break;
					case 4:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) > 3 && npc.GetMemoState(talker,712) < 9 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_brasseur_q0712_09.htm");
						}
						break;
					case 5:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 9 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetDominionWarState(84) == 5 || npc.Castle_IsUnderSiege())
							{
								npc.ShowPage(talker,"chamberlain_brasseur_q0712_10a.htm");
							}
							else if(npc.Fortress_GetContractStatus(105) == -1)
							{
								npc.ShowPage(talker,"chamberlain_brasseur_q0712_10b.htm");
							}
							else
							{
								npc.ShowPage(talker,"chamberlain_brasseur_q0712_10.htm");
							}
						}
						break;
				}
				break;
			case warehouse_chief_croop:
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 3 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 4 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 6 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 3;
					npc.AddChoice(3,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 7 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 4;
					npc.AddChoice(4,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 8 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 5;
					npc.AddChoice(5,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 9 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 6;
					npc.AddChoice(6,"Path to Becoming a Lord - Oren (In progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 3 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_croop_q0712_01.htm");
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 4 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_croop_q0712_04.htm");
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_croop_q0712_05.htm");
						}
						break;
					case 3:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 6 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_croop_q0712_08.htm");
						}
						break;
					case 4:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 7 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.OwnItemCount(talker,13851) == 0)
							{
								npc.GiveItem1(talker,13851,1);
								npc.ShowPage(talker,"warehouse_chief_croop_q0712_10.htm");
							}
							else
							{
								npc.ShowPage(talker,"warehouse_chief_croop_q0712_11.htm");
							}
						}
						break;
					case 5:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 8 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_croop_q0712_12.htm");
						}
						break;
					case 6:
						if((npc.HaveMemo(talker,712) == 1 && npc.GetMemoState(talker,712) == 9 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_croop_q0712_14.htm");
						}
						break;
				}
				break;
			case marty:
				if(npc.IsMyLord(talker) == 0 && npc.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Oren (In progress)");
				}
				if(_code == 0)
				{
					if(npc.IsMyLord(talker) == 0 && npc.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
					{
						npc.SetCurrentQuestID(712);
						if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
						{
							npc.ShowSystemMessage(talker,1118);
							return null;
						}
						L2Player c0 = npc.Pledge_GetLeader(talker);
						if(npc.IsNullCreature(c0) == 0)
						{
							if(npc.HaveMemo(c0,712) == 1 && npc.GetMemoState(c0,712) == 4)
							{
								npc.ShowPage(talker,"marty_q0712_01.htm");
							}
							else if(npc.HaveMemo(c0,712) == 1 && npc.GetMemoState(c0,712) == 5)
							{
								npc.ShowPage(talker,"marty_q0712_03.htm");
							}
						}
					}
				}
				break;
			case yan:
				if(npc.IsMyLord(talker) == 0 && npc.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 5;
					npc.AddChoice(5,"Path to Becoming a Lord - Oren (In progress)");
				}

				switch(_code)
				{
					case 5:
						if(npc.IsMyLord(talker) == 0 && npc.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
						{
							npc.SetCurrentQuestID(712);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							L2Player c0 = npc.Pledge_GetLeader(talker);
							if(npc.IsNullCreature(c0) == 0)
							{
								if(npc.HaveMemo(c0,712) == 1 && npc.GetMemoState(c0,712) == 5)
								{
									npc.ShowPage(talker,"yan_q0712_01.htm");
								}
								else if(npc.GetMemoState(c0,712) >= 5)
								{
									npc.ShowPage(talker,"yan_q0712_03.htm");
								}
							}
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
			if(npc.IsNull(target) == 0 && npc.DistFromMe(target) <= 1500)
			{
				L2Player c0 = npc.Pledge_GetLeader(target);
				if(npc.IsNullCreature(c0) == 0)
				{
					if(npc.HaveMemo(c0,712) == 1 && npc.GetMemoState(c0,712) == 7 && npc.DistFromMe(c0) <= 1500)
					{
						if(npc.OwnItemCount(c0,13851) >= 299)
						{
							npc.GiveItem1(c0,13851,1);
							npc.SetMemoState(c0,712,8);
							npc.SetFlagJournal(c0,712,7);
							npc.ShowQuestMark(c0,712);
							npc.SoundEffect(c0,"ItemSound.quest_middle");
						}
						else
						{
							npc.GiveItem1(c0,13851,1);
							npc.SoundEffect(c0,"ItemSound.quest_itemget");
						}
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
