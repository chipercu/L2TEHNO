package quests._715_PathToBecomingALordGoddard;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Годдард, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _715_PathToBecomingALordGoddard extends Quest implements ScriptFile
{
	private static final int chamberlain_alfred = 35363;

	private static final int flame_spirit_nastron = 25306;
	private static final int water_spirit_ashutar = 25316;

	public _715_PathToBecomingALordGoddard()
	{
		super(false);
		addStartNpc(chamberlain_alfred);
		addKillId(water_spirit_ashutar, flame_spirit_nastron);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _choiceN = 0;
		int _code = -1;
		int i0;
		int i1;

		L2Player talker = st.getPlayer();

		switch(npc.getNpcId())
		{
			case chamberlain_alfred:
				if(npc.HaveMemo(talker,715) == 0 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Goddard");
				}
				if(npc.HaveMemo(talker,715) == 1 && npc.GetMemoState(talker,715) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Goddard (In progress)");
				}
				if(npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 100) == 1 && (npc.GetMemoState(talker,715) % 100) != 21)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Goddard (In progress)");
				}
				if(npc.HaveMemo(talker,715) == 1 && npc.GetMemoState(talker,715) == 11)
				{
					_choiceN = (_choiceN + 1);
					_code = 3;
					npc.AddChoice(3,"Path to Becoming a Lord - Goddard (In progress)");
				}
				if(npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 100) == 2 && (npc.GetMemoState(talker,715) / 10) != 22)
				{
					_choiceN = (_choiceN + 1);
					_code = 4;
					npc.AddChoice(4,"Path to Becoming a Lord - Goddard (In progress)");
				}
				if((npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 10) == 1 || (npc.GetMemoState(talker,715) / 10) == 2))
				{
					_choiceN = (_choiceN + 1);
					_code = 5;
					npc.AddChoice(5,"Path to Becoming a Lord - Goddard (In progress)");
				}
				if(npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 10) == 22 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 6;
					npc.AddChoice(6,"Path to Becoming a Lord - Goddard (In progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,715) == 0 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(715);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(87) == 0)
								{
									npc.ShowQuestPage(talker,"chamberlain_alfred_q0715_01.htm",715);
								}
								else
								{
									npc.ShowQuestPage(talker,"chamberlain_alfred_q0715_03.htm",715);
								}
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,715) == 1 && npc.GetMemoState(talker,715) == 1))
						{
							npc.SetCurrentQuestID(715);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_alfred_q0715_04a.htm");
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 100) == 1 && (npc.GetMemoState(talker,715) % 100) != 21))
						{
							npc.SetCurrentQuestID(715);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_alfred_q0715_07.htm");
						}
						break;
					case 3:
						if((npc.HaveMemo(talker,715) == 1 && npc.GetMemoState(talker,715) == 11))
						{
							npc.SetCurrentQuestID(715);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_alfred_q0715_08.htm");
						}
						break;
					case 4:
						if((npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 100) == 2 && (npc.GetMemoState(talker,715) / 10) != 22))
						{
							npc.SetCurrentQuestID(715);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_alfred_q0715_09.htm");
							i0 = npc.GetMemoState(talker,715);
							if(i0 == 201)
							{
								npc.SetMemoState(talker,715,(i0 + 10));
							}
							npc.SetFlagJournal(talker,715,6);
							npc.ShowQuestMark(talker,715);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 5:
						if(((npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 10) == 1 || (npc.GetMemoState(talker,715) / 10) == 2)))
						{
							npc.SetCurrentQuestID(715);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							i0 = npc.GetMemoState(talker,715);
							i1 = (i0 / 10);
							if(i1 == 2)
							{
								npc.SetMemoState(talker,715,(i0 + 100));
							}
							npc.ShowPage(talker,"chamberlain_alfred_q0715_10.htm");
							npc.SetFlagJournal(talker,715,7);
							npc.ShowQuestMark(talker,715);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 6:
						if((npc.HaveMemo(talker,715) == 1 && (npc.GetMemoState(talker,715) / 10) == 22 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(715);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetDominionWarState(87) == 5 || npc.Castle_IsUnderSiege())
							{
								npc.ShowPage(talker,"chamberlain_alfred_q0715_11a.htm");
							}
							else if(npc.Fortress_GetContractStatus(109) == -1)
							{
								npc.ShowPage(talker,"chamberlain_alfred_q0715_11b.htm");
							}
							else
							{
								npc.ShowPage(talker,"chamberlain_alfred_q0715_11.htm");
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
		switch(npc.getNpcId())
		{
			case flame_spirit_nastron:
				L2Player target = st.getPlayer();
				if(npc.IsNull(target) == 0 && npc.DistFromMe(target) <= 1500)
				{
					npc.Say(npc.MakeFString(71551,target.getName(),"","","",""));
					L2Player c0 = npc.Pledge_GetLeader(target);
					if(npc.IsNullCreature(c0) == 0)
					{
						if(npc.HaveMemo(c0,715) == 1 && (npc.GetMemoState(c0,715) / 100) == 1)
						{
							if((npc.GetMemoState(c0,715) / 10) == 12)
							{
								int i0 = npc.GetMemoState(c0,715);
								npc.SetMemoState(c0,715,(i0 + 100));
								npc.SetFlagJournal(c0,715,8);
								npc.ShowQuestMark(c0,715);
								npc.SoundEffect(c0,"ItemSound.quest_middle");
							}
							else if((npc.GetMemoState(c0,715) / 10) == 10)
							{
								int i0 = npc.GetMemoState(c0,715);
								npc.SetMemoState(c0,715,(i0 + 100));
								npc.SetFlagJournal(c0,715,4);
								npc.ShowQuestMark(c0,715);
								npc.SoundEffect(c0,"ItemSound.quest_middle");
							}
						}
					}
				}
				break;
			case water_spirit_ashutar:
				target = st.getPlayer();
				if(npc.IsNull(target) == 0 && npc.DistFromMe(target) <= 1500)
				{
					npc.Say(npc.MakeFString(71551,target.getName(),"","","",""));
					L2Player c0 = npc.Pledge_GetLeader(target);
					if(npc.IsNullCreature(c0) == 0)
					{
						if(npc.HaveMemo(c0,715) == 1 && ((npc.GetMemoState(c0,715) / 10) % 10) == 1)
						{
							if((npc.GetMemoState(c0,715) / 10) == 21)
							{
								int i0 = npc.GetMemoState(c0,715);
								npc.SetMemoState(c0,715,(i0 + 10));
								npc.SetFlagJournal(c0,715,9);
								npc.ShowQuestMark(c0,715);
								npc.SoundEffect(c0,"ItemSound.quest_middle");
							}
							else if((npc.GetMemoState(c0,715) / 10) == 1)
							{
								int i0 = npc.GetMemoState(c0,715);
								npc.SetMemoState(c0,715,(i0 + 10));
								npc.SetFlagJournal(c0,715,5);
								npc.ShowQuestMark(c0,715);
								npc.SoundEffect(c0,"ItemSound.quest_middle");
							}
						}
					}
				}
				break;
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