package quests._713_PathToBecomingALordAden;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Аден, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _713_PathToBecomingALordAden extends Quest implements ScriptFile
{
	private static final int chamberlain_logan = 35274;
	private static final int highpriest_orven = 30857;
	private static final int[] mobs =
	{
			20666,
			20669
	};

	public _713_PathToBecomingALordAden()
	{
		super(false);
		addStartNpc(chamberlain_logan);
		addTalkId(highpriest_orven);
		addKillId(mobs);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _choiceN = 0;
		int _code = 0;

		L2Player talker = st.getPlayer();

		switch(npc.getNpcId())
		{
			case chamberlain_logan:
				if(npc.HaveMemo(talker,713) == 0 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Aden");
				}
				if(npc.HaveMemo(talker,713) == 1 && npc.GetMemoState(talker,713) > 0 && npc.GetMemoState(talker,713) < 1000)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Aden (In progress)");
				}
				if(npc.HaveMemo(talker,713) == 1 && npc.GetMemoState(talker,713) == 1000 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Aden (In progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,713) == 0 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(85) == 0)
								{
									StringBuilder fhtml0 = new StringBuilder();
									npc.FHTML_SetFileName(fhtml0,"chamberlain_logan_q0713_01.htm");
									npc.FHTML_SetInt(fhtml0,"quest_id",713);
									npc.ShowQuestFHTML(talker,fhtml0,713);
								}
								else
								{
									npc.ShowQuestPage(talker,"chamberlain_logan_q0713_02.htm",713);
								}
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,713) == 1 && npc.GetMemoState(talker,713) > 0 && npc.GetMemoState(talker,713) < 1000))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_logan_q0713_04.htm");
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,713) == 1 && npc.GetMemoState(talker,713) == 1000 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetDominionWarState(85) == 5 || npc.Castle_IsUnderSiege())
							{
								npc.ShowPage(talker,"chamberlain_logan_q0713_05a.htm");
							}
							else if(npc.Fortress_GetContractStatus(106) == -1 || npc.Fortress_GetContractStatus(107) == -1)
							{
								npc.ShowPage(talker,"chamberlain_logan_q0713_05b.htm");
							}
							else
							{
								npc.ShowPage(talker,"chamberlain_logan_q0713_05.htm");
							}
						}
						break;
				}
				break;
			case highpriest_orven:
				if(npc.HaveMemo(talker,713) == 1 && npc.GetMemoState(talker,713) == 1 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 10;
					npc.AddChoice(10,"Path to Becoming a Lord - Aden (In progress)");
				}
				if(npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 2 && (npc.GetMemoState(talker,713) / 100) < 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 11;
					npc.AddChoice(11,"Path to Becoming a Lord - Aden (In progress)");
				}
				if(npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 12 && (npc.GetMemoState(talker,713) / 100) < 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 12;
					npc.AddChoice(12,"Path to Becoming a Lord - Aden (In progress)");
				}
				if(npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 2 && (npc.GetMemoState(talker,713) / 100) >= 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 13;
					npc.AddChoice(13,"Path to Becoming a Lord - Aden (In progress)");
				}
				if(npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 12 && (npc.GetMemoState(talker,713) / 100) >= 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 14;
					npc.AddChoice(14,"Path to Becoming a Lord - Aden (In progress)");
				}

				switch(_code)
				{
					case 10:
						if((npc.HaveMemo(talker,713) == 1 && npc.GetMemoState(talker,713) == 1 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"highpriest_orven_q0713_01.htm");
						}
						break;
					case 11:
						if((npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 2 && (npc.GetMemoState(talker,713) / 100) < 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"highpriest_orven_q0713_04.htm");
						}
						break;
					case 12:
						if((npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 12 && (npc.GetMemoState(talker,713) / 100) < 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"highpriest_orven_q0713_05.htm");
						}
						break;
					case 13:
						if((npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 2 && (npc.GetMemoState(talker,713) / 100) >= 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"highpriest_orven_q0713_06.htm");
						}
						break;
					case 14:
						if((npc.HaveMemo(talker,713) == 1 && (npc.GetMemoState(talker,713) % 100) == 12 && (npc.GetMemoState(talker,713) / 100) >= 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(713);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.SetMemoState(talker,713,1000);
							npc.ShowPage(talker,"highpriest_orven_q0713_07.htm");
							npc.SetFlagJournal(talker,713,7);
							npc.ShowQuestMark(talker,713);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
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
					if(npc.HaveMemo(c0,713) == 1 && (npc.GetMemoState(c0,713) % 100) == 2)
					{
						int i0 = npc.GetMemoState(c0,713);
						int i1 = npc.GetMemoStateEx(c0,713,1);
						if(i1 >= 99)
						{
							npc.SetMemoState(c0,713,(i0 + 10));
							if((npc.GetMemoState(c0,713) / 100) < 5)
							{
								npc.SetFlagJournal(c0,713,3);
							}
							else if((npc.GetMemoState(c0,713) / 100) >= 5)
							{
								npc.SetFlagJournal(c0,713,5);
							}
							npc.ShowQuestMark(c0,713);
							npc.SoundEffect(c0,"ItemSound.quest_middle");
						}
						npc.SetMemoStateEx(c0,713,1,(i1 + 1));
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