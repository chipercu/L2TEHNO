package quests._714_PathToBecomingALordSchuttgart;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Шутгард, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _714_PathToBecomingALordSchuttgart extends Quest implements ScriptFile
{
	private static final int chamberlain_august = 35555;
	private static final int head_blacksmith_newyear = 31961;
	private static final int warehouse_chief_yaseni = 31958;

	private static final int[] mobs =
	{
			22809,
			22810,
			22811,
			22812
	};

	public _714_PathToBecomingALordSchuttgart()
	{
		super(false);
		addStartNpc(chamberlain_august);
		addTalkId(head_blacksmith_newyear, warehouse_chief_yaseni);
		addKillId(mobs);
		//addQuestItem(17162);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _choiceN = 0;
		int _code = 0;

		L2Player talker = st.getPlayer();

		switch(npc.getNpcId())
		{
			case chamberlain_august:
				if(npc.HaveMemo(talker,714) == 0 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Schuttgart");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 2)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) > 2 && npc.GetMemoState(talker,714) < 5)
				{
					_choiceN = (_choiceN + 1);
					_code = 3;
					npc.AddChoice(3,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 5)
				{
					_choiceN = (_choiceN + 1);
					_code = 4;
					npc.AddChoice(4,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) > 5 && npc.GetMemoState(talker,714) < 8)
				{
					_choiceN = (_choiceN + 1);
					_code = 5;
					npc.AddChoice(5,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 8 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 6;
					npc.AddChoice(6,"Path to Becoming a Lord - Schuttgart (In progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,714) == 0 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(89) == 0)
								{
									npc.ShowQuestPage(talker,"chamberlain_august_q0714_01.htm",714);
								}
								else
								{
									npc.ShowQuestPage(talker,"chamberlain_august_q0714_03.htm",714);
								}
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if((npc.GetCurrentTick() - npc.GetMemoStateEx(talker,714,1)) < 60)
							{
								npc.ShowPage(talker,"chamberlain_august_q0714_05.htm");
							}
							else
							{
								npc.SetMemoState(talker,714,2);
								npc.ShowPage(talker,"chamberlain_august_q0714_06.htm");
								npc.SetMemoStateEx(talker,714,1,0);
							}
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 2))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_august_q0714_07.htm");
						}
						break;
					case 3:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) > 2 && npc.GetMemoState(talker,714) < 5))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_august_q0714_09.htm");
						}
						break;
					case 4:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 5))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_august_q0714_10.htm");
						}
						break;
					case 5:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) > 5 && npc.GetMemoState(talker,714) < 8))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_august_q0714_11.htm");
						}
						break;
					case 6:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 8 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetDominionWarState(89) == 5 || npc.Castle_IsUnderSiege())
							{
								npc.ShowPage(talker,"chamberlain_august_q0714_12a.htm");
							}
							else if(npc.Fortress_GetContractStatus(111) == -1)
							{
								npc.ShowPage(talker,"chamberlain_august_q0714_12b.htm");
							}
							else
							{
								npc.ShowPage(talker,"chamberlain_august_q0714_12.htm");
							}
						}
						break;
				}
				break;
			case head_blacksmith_newyear:
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) >= 3 && npc.GetMemoState(talker,714) <= 4 && npc.GetOneTimeQuestFlag(talker,120) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 18;
					npc.AddChoice(18,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 3 && npc.GetOneTimeQuestFlag(talker,120) == 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 19;
					npc.AddChoice(19,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 4 && npc.GetOneTimeQuestFlag(talker,120) == 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 20;
					npc.AddChoice(20,"Path to Becoming a Lord - Schuttgart (In progress)");
				}

				switch(_code)
				{
					case 18:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) >= 3 && npc.GetMemoState(talker,714) <= 4 && npc.GetOneTimeQuestFlag(talker,120) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.SetMemoState(talker,714,5);
							npc.ShowPage(talker,"head_blacksmith_newyear_q0714_01.htm");
							npc.SetFlagJournal(talker,714,4);
							npc.ShowQuestMark(talker,714);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 19:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 3 && npc.GetOneTimeQuestFlag(talker,120) == 0))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"head_blacksmith_newyear_q0714_02.htm");
						}
						break;
					case 20:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 4 && npc.GetOneTimeQuestFlag(talker,120) == 0))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetOneTimeQuestFlag(talker,121) == 0)
							{
								npc.ShowPage(talker,"head_blacksmith_newyear_q0714_07.htm");
							}
							else if(npc.GetOneTimeQuestFlag(talker,114) == 0)
							{
								npc.ShowPage(talker,"head_blacksmith_newyear_q0714_08.htm");
							}
							else if(npc.GetOneTimeQuestFlag(talker,120) == 0)
							{
								npc.ShowPage(talker,"head_blacksmith_newyear_q0714_09.htm");
							}
						}
						break;
				}
				break;
			case warehouse_chief_yaseni:
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 6 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 7 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 3;
					npc.AddChoice(3,"Path to Becoming a Lord - Schuttgart (In progress)");
				}
				if(npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 8 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 4;
					npc.AddChoice(4,"Path to Becoming a Lord - Schuttgart (In progress)");
				}

				switch(_code)
				{
					case 1:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_yaseni_q0714_01.htm");
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 6 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_yaseni_q0714_03.htm");
						}
						break;
					case 3:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 7 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.SetMemoState(talker,714,8);
							npc.ShowPage(talker,"warehouse_chief_yaseni_q0714_04.htm");
							npc.SetFlagJournal(talker,714,7);
							npc.ShowQuestMark(talker,714);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 4:
						if((npc.HaveMemo(talker,714) == 1 && npc.GetMemoState(talker,714) == 8 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(714);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"warehouse_chief_yaseni_q0714_05.htm");
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
					if(npc.HaveMemo(c0,714) == 1 && npc.GetMemoState(c0,714) == 6 && npc.DistFromMe(c0) <= 1500)
					{
						int i0 = npc.GetMemoStateEx(c0,714,1);
						if(i0 < 300)
						{
							npc.SetMemoStateEx(c0,714,1,(i0 + 1));
						}
						else
						{
							npc.SetMemoState(c0,714,7);
							npc.SetFlagJournal(c0,714,6);
							npc.ShowQuestMark(c0,714);
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
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}