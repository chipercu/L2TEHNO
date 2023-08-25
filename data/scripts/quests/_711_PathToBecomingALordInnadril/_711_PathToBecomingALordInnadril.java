package quests._711_PathToBecomingALordInnadril;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Инадрил, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _711_PathToBecomingALordInnadril extends Quest implements ScriptFile
{
	private static final int chamberlain_neurath = 35316;
	private static final int iason_haine = 30969;

	private static final int[] mobs = { 20993, 20991, 20808, 20807, 20792, 20805, 20791, 20806, 20804, 20992, 20135 };

	public _711_PathToBecomingALordInnadril()
	{
		super(false);
		addStartNpc(chamberlain_neurath);
		addTalkId(iason_haine);
		addKillId(mobs);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _choiceN = 0;
		int i0 = 0;
		int _code = 0;

		L2Player c0 = null;
		L2Player talker = st.getPlayer();
		switch(npc.getNpcId())
		{
			case chamberlain_neurath:
				if(npc.HaveMemo(talker,711) == 0 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Innadril");
				}
				if(npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 1 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Innadril (In progress)");
				}
				if(npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 2 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Innadril (In progress)");
				}
				if(npc.IsMyLord(talker) == 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 3;
					npc.AddChoice(3,"Path to Becoming a Lord - Innadril (In progress)");
				}
				if(npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 3 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 4;
					npc.AddChoice(4,"Path to Becoming a Lord - Innadril (In progress)");
				}
				if(npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 4 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 5;
					npc.AddChoice(5,"Path to Becoming a Lord - Innadril (In progress)");
				}
				if(npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 6;
					npc.AddChoice(6,"Path to Becoming a Lord - Innadril (In progress)");
				}
				if(npc.HaveMemo(talker,711) == 1 && (((npc.GetMemoState(talker,711) / 1000) >= 1) && (npc.GetMemoState(talker,711) / 1000) <= 100) && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 7;
					npc.AddChoice(7,"Path to Becoming a Lord - Innadril (In progress)");
				}
				if(npc.HaveMemo(talker,711) == 1 && (npc.GetMemoState(talker,711) / 1000) >= 101 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 8;
					npc.AddChoice(8,"Path to Becoming a Lord - Innadril (In progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,711) == 0 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(86) == 0)
								{
									npc.ShowQuestPage(talker,"chamberlain_neurath_q0711_01.htm",711);
								}
								else
								{
									npc.ShowQuestPage(talker,"chamberlain_neurath_q0711_03.htm",711);
								}
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 1 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if((npc.GetCurrentTick() - npc.GetMemoStateEx(talker,711,1)) < 60)
							{
								npc.ShowPage(talker,"chamberlain_neurath_q0711_05.htm");
							}
							else
							{
								npc.SetMemoState(talker,711,2);
								npc.SetMemoStateEx(talker,711,1,0);
								npc.ShowPage(talker,"chamberlain_neurath_q0711_06.htm");
							}
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 2 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_neurath_q0711_07.htm");
						}
						break;
					case 3:
						if(npc.IsMyLord(talker) == 0)
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
							{
								c0 = npc.Pledge_GetLeader(talker);
								if(npc.IsNullCreature(c0) == 0)
								{
									if(npc.HaveMemo(c0,711) == 1 && npc.GetMemoState(c0,711) == 3)
									{
										if(npc.DistFromMe(c0) <= 1500)
										{
											npc.ShowPage(talker,"chamberlain_neurath_q0711_11.htm");
										}
										else
										{
											npc.ShowPage(talker,"chamberlain_neurath_q0711_10.htm");
										}
									}
									else if(npc.HaveMemo(c0,711) == 1 && npc.GetMemoState(c0,711) == 4)
									{
										npc.ShowPage(talker,"chamberlain_neurath_q0711_13a.htm");
									}
									else
									{
										npc.ShowPage(talker,"chamberlain_neurath_q0711_09.htm");
									}
								}
								else
								{
									npc.ShowPage(talker,"chamberlain_neurath_q0711_09.htm");
								}
							}
							else if(npc.Castle_GetPledgeId() != talker.getClanId())
							{
								npc.ShowPage(talker,"chamberlain_neurath_q0711_09.htm");
							}
						}
						break;
					case 4:
						if((npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 3 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_neurath_q0711_14.htm");
						}
						break;
					case 5:
						if((npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 4 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_neurath_q0711_15.htm");
						}
						break;
					case 6:
						if((npc.HaveMemo(talker,711) == 1 && npc.GetMemoState(talker,711) == 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							i0 = npc.GetMemoState(talker,711);
							npc.SetMemoState(talker,711,(i0 + 1000));
							npc.ShowPage(talker,"chamberlain_neurath_q0711_16.htm");
							npc.SetFlagJournal(talker,711,5);
							npc.ShowQuestMark(talker,711);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 7:
						if((npc.HaveMemo(talker,711) == 1 && (((npc.GetMemoState(talker,711) / 1000) >= 1) && (npc.GetMemoState(talker,711) / 1000) <= 100) && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if((npc.GetMemoState(talker,711) % 100) >= 15)
							{
								npc.ShowPage(talker,"chamberlain_neurath_q0711_17.htm");
							}
							else
							{
								npc.ShowPage(talker,"chamberlain_neurath_q0711_18.htm");
							}
						}
						break;
					case 8:
						if((npc.HaveMemo(talker,711) == 1 && (npc.GetMemoState(talker,711) / 1000) >= 101 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if((npc.GetMemoState(talker,711) % 100) >= 15)
							{
								if(npc.GetDominionWarState(86) == 5 || npc.Castle_IsUnderSiege())
								{
									npc.ShowPage(talker,"chamberlain_neurath_q0711_20a.htm");
								}
								else if(npc.Fortress_GetContractStatus(108) == -1)
								{
									npc.ShowPage(talker,"chamberlain_neurath_q0711_20b.htm");
								}
								else
								{
									npc.ShowPage(talker,"chamberlain_neurath_q0711_20.htm");
								}
							}
							else
							{
								npc.ShowPage(talker,"chamberlain_neurath_q0711_19.htm");
								npc.SetFlagJournal(talker,711,7);
								npc.ShowQuestMark(talker,711);
								npc.SoundEffect(talker,"ItemSound.quest_middle");
							}
						}
						break;
				}
				break;
			case iason_haine:
				if(npc.IsMyLord(talker) == 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Innadril (In progress)");
				}
				switch(_code)
				{
					case 1:
						if(npc.IsMyLord(talker) == 0)
						{
							npc.SetCurrentQuestID(711);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							c0 = npc.Pledge_GetLeader(talker);
							if(npc.IsNullCreature(c0) == 0)
							{
								if(npc.HaveMemo(c0,711) == 1 && npc.GetMemoState(c0,711) <= 3)
								{
									npc.ShowPage(talker,"iason_haine_q0711_02.htm");
								}
								else if(npc.GetMemoState(c0,711) == 4)
								{
									if(talker.getObjectId() == npc.GetMemoStateEx(c0,711,1))
									{
										npc.ShowPage(talker,"iason_haine_q0711_03.htm");
									}
									else
									{
										npc.ShowPage(talker,"iason_haine_q0711_03a.htm");
									}
								}
								else if(((npc.GetMemoState(c0,711) % 100) >= 5) && (npc.GetMemoState(c0,711) % 100) < 15)
								{
									npc.ShowPage(talker,"iason_haine_q0711_07.htm");
								}
								else if((npc.GetMemoState(c0,711) % 100) >= 15)
								{
									if((npc.GetMemoState(c0,711) / 1000) < 101)
									{
										npc.ShowPage(talker,"iason_haine_q0711_08.htm");
									}
									else
									{
										npc.ShowPage(talker,"iason_haine_q0711_09.htm");
									}
								}
							}
							else
							{
								npc.ShowPage(talker,"iason_haine_q0711_01.htm");
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
					if(npc.HaveMemo(c0,711) == 1 && (npc.GetMemoState(c0,711) / 1000) >= 1 && (npc.GetMemoState(c0,711) / 1000) < 101 && npc.DistFromMe(c0) <= 1500)
					{
						int i0 = npc.GetMemoState(c0,711);
						if((npc.GetMemoState(c0,711) / 1000) < 100)
						{
							npc.SetMemoState(c0,711,(i0 + 1000));
						}
						else
						{
							npc.SetMemoState(c0,711,(i0 + 1000));
							npc.SetFlagJournal(c0,711,6);
							npc.ShowQuestMark(c0,711);
							npc.SoundEffect(c0,"ItemSound.quest_middle");
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