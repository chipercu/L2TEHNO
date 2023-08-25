package quests._716_PathToBecomingALordRune;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Руна, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _716_PathToBecomingALordRune extends Quest implements ScriptFile
{
	private static final int chamberlain_frederick = 35509;
	private static final int falsepriest_agripel = 31348;
	private static final int highpriest_innocentin = 31328;

	private static final int[] mobs = 
	{
		22176,
		22146,
		22151,
		22138,
		22141,
		22175,
		22155,
		22159,
		22163,
		22167,
		22171,
		22143,
		22137,
		22194,
		22164,
		22156,
		22166,
		22173,
		22170,
		22157,
		22160,
		22165,
		22168,
		22174,
		22158,
		22162,
		22149,
		22147,
		22154,
		22161,
		22169,
		22172,
		22145,
		22152,
		22153,
		22136,
		22150,
		22148,
		22142,
		22144,
		22139,
		22140
	};

	public _716_PathToBecomingALordRune()
	{
		super(false);
		addStartNpc(chamberlain_frederick);
		addTalkId(falsepriest_agripel, highpriest_innocentin);
		addKillId(mobs);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _choiceN = 0;
		int _code = 0;
		int i0;

		L2Player talker = st.getPlayer();
		L2Player c0 = null;

		switch(npc.getNpcId())
		{
			case falsepriest_agripel:
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 2 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 14;
					npc.AddChoice(14,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 3 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 15;
					npc.AddChoice(15,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 1 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 16;
					npc.AddChoice(16,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 2 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 17;
					npc.AddChoice(17,"Path to Becoming a Lord - Rune (In progress)");
				}

				switch(_code)
				{
					case 14:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 2 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"falsepriest_agripel_q0716_01.htm");
						}
						break;
					case 15:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 3 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"falsepriest_agripel_q0716_04.htm");
						}
						break;
					case 16:
						if((npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 1 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"falsepriest_agripel_q0716_05.htm");
						}
						break;
					case 17:
						if((npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 2 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"falsepriest_agripel_q0716_11.htm");
						}
						break;
				}
				break;
			case highpriest_innocentin:
				if(npc.IsMyLord(talker) == 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 24;
					npc.AddChoice(24,"Path to Becoming a Lord - Rune (In progress)");
				}

				if(_code == 24)
				{
					if(npc.IsMyLord(talker) == 0)
					{
						npc.SetCurrentQuestID(716);
						if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
						{
							npc.ShowSystemMessage(talker,1118);
							return null;
						}
						c0 = npc.Pledge_GetLeader(talker);
						if(npc.IsNullCreature(c0) == 0)
						{
							if(npc.HaveMemo(c0,716) == 1 && npc.GetMemoState(c0,716) == 5)
							{
								i0 = npc.GetMemoStateEx(c0,716,1);
								if(talker.getObjectId() == i0)
								{
									npc.ShowPage(talker,"highpriest_innocentin_q0716_03.htm");
								}
								else
								{
									npc.ShowPage(talker,"highpriest_innocentin_q0716_03a.htm");
								}
							}
							else if(npc.HaveMemo(c0,716) == 1 && npc.GetMemoState(c0,716) < 5)
							{
								npc.ShowPage(talker,"highpriest_innocentin_q0716_02.htm");
							}
							else if(npc.HaveMemo(c0,716) == 1 && (npc.GetMemoState(c0,716) % 10) == 6)
							{
								npc.ShowPage(talker,"highpriest_innocentin_q0716_06.htm");
							}
						}
						else
						{
							npc.ShowPage(talker,"highpriest_innocentin_q0716_01.htm");
						}
					}
				}

				break;
			case chamberlain_frederick:
				if(npc.HaveMemo(talker,716) == 0 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Path to Becoming a Lord - Rune");
				}
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 1 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 2 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 3 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 3;
					npc.AddChoice(3,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 4)
				{
					_choiceN = (_choiceN + 1);
					_code = 4;
					npc.AddChoice(4,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.IsMyLord(talker) == 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 5;
					npc.AddChoice(5,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 5 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 6;
					npc.AddChoice(6,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 6 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 7;
					npc.AddChoice(7,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 8;
					npc.AddChoice(8,"Path to Becoming a Lord - Rune (In progress)");
				}
				if(npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 2 && npc.IsMyLord(talker) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 9;
					npc.AddChoice(9,"Path to Becoming a Lord - Rune (In progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,716) == 0 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(88) == 0)
								{
									npc.ShowQuestPage(talker,"chamberlain_frederick_q0716_01.htm",716);
								}
								else
								{
									npc.ShowQuestPage(talker,"chamberlain_frederick_q0716_03.htm",716);
								}
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 1 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetOneTimeQuestFlag(talker,25) == 0)
							{
								npc.ShowPage(talker,"chamberlain_frederick_q0716_06.htm");
							}
							else
							{
								npc.SetMemoState(talker,716,2);
								npc.SetMemoStateEx(talker,716,1,0);
								npc.ShowPage(talker,"chamberlain_frederick_q0716_07.htm");
								npc.SetFlagJournal(talker,716,2);
								npc.ShowQuestMark(talker,716);
								npc.SoundEffect(talker,"ItemSound.quest_middle");
							}
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 2 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_frederick_q0716_08.htm");
						}
						break;
					case 3:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 3 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.SetMemoState(talker,716,4);
							npc.ShowPage(talker,"chamberlain_frederick_q0716_09.htm");
							npc.SetFlagJournal(talker,716,4);
							npc.ShowQuestMark(talker,716);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 4:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 4))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_frederick_q0716_10.htm");
						}
						break;
					case 5:
						if(npc.IsMyLord(talker) == 0)
						{
							npc.SetCurrentQuestID(716);
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
									if(npc.HaveMemo(c0,716) == 1 && npc.GetMemoState(c0,716) == 4)
									{
										if(npc.DistFromMe(c0) <= 1500)
										{
											npc.ShowPage(talker,"chamberlain_frederick_q0716_16.htm");
										}
										else
										{
											npc.ShowPage(talker,"chamberlain_frederick_q0716_15.htm");
										}
									}
									else
									{
										npc.ShowPage(talker,"chamberlain_frederick_q0716_13.htm");
									}
								}
								else
								{
									npc.ShowPage(talker,"chamberlain_frederick_q0716_12.htm");
								}
							}
							else if(npc.Castle_GetPledgeId() != talker.getClanId())
							{
								npc.ShowPage(talker,"chamberlain_frederick_q0716_11.htm");
							}
						}
						break;
					case 6:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 5 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_frederick_q0716_19.htm");
						}
						break;
					case 7:
						if((npc.HaveMemo(talker,716) == 1 && npc.GetMemoState(talker,716) == 6 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							i0 = npc.GetMemoState(talker,716);
							npc.SetMemoState(talker,716,(i0 + 10));
							npc.ShowPage(talker,"chamberlain_frederick_q0716_20.htm");
							npc.SetFlagJournal(talker,716,7);
							npc.ShowQuestMark(talker,716);
							npc.SoundEffect(talker,"ItemSound.quest_middle");
						}
						break;
					case 8:
						if((npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"chamberlain_frederick_q0716_21.htm");
						}
						break;
					case 9:
						if((npc.HaveMemo(talker,716) == 1 && (npc.GetMemoState(talker,716) / 10) == 2 && npc.IsMyLord(talker) == 1))
						{
							npc.SetCurrentQuestID(716);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetDominionWarState(88) == 5 || npc.Castle_IsUnderSiege())
							{
								npc.ShowPage(talker,"chamberlain_frederick_q0716_22a.htm");
							}
							else if(npc.Fortress_GetContractStatus(110) == -1)
							{
								npc.ShowPage(talker,"chamberlain_frederick_q0716_22b.htm");
							}
							else
							{
								npc.Say(npc.MakeFString(71659,talker.getName(),"","","",""));
								npc.DeclareLord(88,talker);
								npc.RemoveMemo(talker,716);
								npc.SoundEffect(talker,"ItemSound.quest_finish");
								npc.AddLog(2,talker,716);
								npc.ShowPage(talker,"chamberlain_frederick_q0716_22.htm");
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
					if(npc.HaveMemo(c0,716) == 1 && (npc.GetMemoState(c0,716) % 10) == 6 && (npc.GetMemoState(c0,716) / 10) == 1)
					{
						int i0 = npc.GetMemoStateEx(c0,716,1);
						if(i0 < 100)
						{
							npc.SetMemoStateEx(c0,716,1,(i0 + 1));
						}
					}
				}
			}
		}
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}