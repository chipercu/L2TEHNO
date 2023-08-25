package quests._708_PathToBecomingALordGludio;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Util;
import l2open.util.Rnd;

/**
 * Запиздохал Diagod.
 * open-team.ru
 * Квест Путь Лорда Глудио, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _708_PathToBecomingALordGludio extends Quest implements ScriptFile
{
	private static final int Sayres = 35100;
	private static final int Pinter = 30298;
	private static final int Bathis = 30332;

	private static final int q_duahan_of_glodio = 27393;
	private static final int[] mobs = {20035, 20042, 20045, 20051, 20054, 20060, 20514, 20515};

	public _708_PathToBecomingALordGludio()
	{
		super(false);
		addStartNpc(Sayres);
		addTalkId(Pinter, Bathis);
		addKillId(q_duahan_of_glodio);
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
			case Sayres:
				if(st.HaveMemo(talker, 708) == 0 && npc.IsMyLord(talker) == 1)
					_code = 0;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 1 && npc.IsMyLord(talker) == 1)
					_code = 1;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 2)
					_code = 2;
				if(npc.IsMyLord(talker) == 0)
					_code = 3;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 3)
					_code = 4;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 4)
					_code = 5;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 5)
					_code = 6;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 <= 1 && st.GetMemoState(talker, 708) % 10 == 9)
					_code = 7;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 == 1 && st.GetMemoState(talker, 708) % 10 != 9)
					_code = 8;
				if(st.HaveMemo(talker, 708) == 1 && (st.GetMemoState(talker, 708) / 10 == 2 || st.GetMemoState(talker, 708) / 10 == 3) && st.GetMemoState(talker, 708) % 10 == 9)
					_code = 9;
				if(st.HaveMemo(talker, 708) == 1 && (st.GetMemoState(talker, 708) / 10 == 2 || st.GetMemoState(talker, 708) / 10 == 3) && st.GetMemoState(talker, 708) % 10 == 9)
					_code = 10;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 == 4 && st.GetMemoState(talker, 708) % 10 != 9)
					_code = 11;
				if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 == 4 && st.GetMemoState(talker, 708) % 10 == 9 && npc.IsMyLord(talker) == 1)
					_code = 12;

				switch(_code)
				{
					case 0:
						if(HaveMemo(talker,708) == 0 && npc.IsMyLord(talker) == 1)
							if(npc.IsDominionOfLord(81) == 0)
								ShowQuestPage(talker,"chamberlain_saius_q0708_01.htm",708);
							else
								ShowQuestPage(talker,"chamberlain_saius_q0708_03.htm",708);
						break;
					case 1:
					{
						if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 1 && npc.IsMyLord(talker) == 1)
						{
							if(npc.GetCurrentTick() - st.GetMemoStateEx(talker, 708, 1) < 60)
								npc.ShowPage(talker, "chamberlain_saius_q0708_05.htm");
							else
							{
								npc.SetMemoState(talker, 708, 2);
								npc.SetMemoStateEx(talker, 708, 1, 0);
								npc.ShowPage(talker, "chamberlain_saius_q0708_06.htm");
							}
						}
						break;
					}
					case 2:
					{
						if(st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 2)
							npc.ShowPage(talker, "chamberlain_saius_q0708_07.htm");
						break;
					}
					case 3:
					{
						if(npc.IsMyLord(talker) == 0)
						{
							if(talker.getClanId() != 0 && npc.Castle_GetPledgeId() == talker.getClanId())
							{
								c0 = npc.Pledge_GetLeader(talker);
								if(npc.IsNullCreature(c0) == 0)
								{
									if(st.HaveMemo(c0, 708) == 1 && st.GetMemoState(c0, 708) == 3)
									{
										if(npc.DistFromMe(c0) <= 1500)
											npc.ShowPage(talker, "chamberlain_saius_q0708_11.htm");
										else
											npc.ShowPage(talker, "chamberlain_saius_q0708_10.htm");
									}
									else if(st.HaveMemo(c0, 708) == 1 && st.GetMemoState(c0, 708) == 4)
										npc.ShowPage(talker, "chamberlain_saius_q0708_13a.htm");
									else
										npc.ShowPage(talker, "chamberlain_saius_q0708_09.htm");
								}
								else
									npc.ShowPage(talker, "chamberlain_saius_q0708_09.htm");
							}
							else if(npc.Castle_GetPledgeId() != talker.getClanId())
								npc.ShowPage(talker, "chamberlain_saius_q0708_09.htm");
						}
						break;
					}
					case 4:
					{
						if((st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 3))
							npc.ShowPage(talker, "chamberlain_saius_q0708_14.htm");
						break;
					}
					case 5:
					{
						if((st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 4))
							npc.ShowPage(talker, "chamberlain_saius_q0708_15.htm");
						break;
					}
					case 6:
					{
						if((st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) == 5))
						{
							i0 = st.GetMemoState(talker, 708);
							npc.SetMemoState(talker, 708, i0 + 10);
							npc.ShowPage(talker, "chamberlain_saius_q0708_16.htm");
							npc.SetFlagJournal(talker, 708, 5);
							npc.ShowQuestMark(talker, 708);
							npc.SoundEffect(talker, "ItemSound.quest_middle");
						}
						break;
					}
					case 7:
					{
						if((st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 <= 1 && st.GetMemoState(talker, 708) % 10 == 9))
						{
							i0 = st.GetMemoState(talker, 708) / 10;
							i1 = st.GetMemoState(talker, 708);
							if(i0 == 0)
							{
								npc.SetMemoState(talker, 708, i1 + 10);
								npc.SetFlagJournal(talker, 708, 5);
								npc.ShowQuestMark(talker, 708);
								npc.SoundEffect(talker, "ItemSound.quest_middle");
							}
							npc.ShowPage(talker, "chamberlain_saius_q0708_17.htm");
						}
						break;
					}
					case 8:
					{
						if((st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 == 1 && st.GetMemoState(talker, 708) % 10 != 9))
							npc.ShowPage(talker, "chamberlain_saius_q0708_18.htm");
						break;
					}
					case 9:
					{
						if((st.HaveMemo(talker, 708) == 1 && (st.GetMemoState(talker, 708) / 10 == 2 || st.GetMemoState(talker, 708) / 10 == 3) && st.GetMemoState(talker, 708) % 10 == 9))
							npc.ShowPage(talker, "chamberlain_saius_q0708_19.htm");
						break;
					}
					case 10:
					{
						if((st.HaveMemo(talker, 708) == 1 && (st.GetMemoState(talker, 708) / 10 == 2 || st.GetMemoState(talker, 708) / 10 == 3) && st.GetMemoState(talker, 708) % 10 == 9))
							npc.ShowPage(talker, "chamberlain_saius_q0708_20.htm");
						break;
					}
					case 11:
					{
						if((st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 == 4 && st.GetMemoState(talker, 708) % 10 != 9))
							npc.ShowPage(talker, "chamberlain_saius_q0708_21.htm");
						break;
					}
					case 12:
					{
						if((st.HaveMemo(talker, 708) == 1 && st.GetMemoState(talker, 708) / 10 == 4 && st.GetMemoState(talker, 708) % 10 == 9 && npc.IsMyLord(talker) == 1))
						{
							if(npc.GetDominionWarState(81) == 5 || npc.Castle_IsUnderSiege())
								npc.ShowPage(talker, "chamberlain_saius_q0708_22a.htm");
							else if(npc.Fortress_GetContractStatus(101) == -1 || npc.Fortress_GetContractStatus(102) == -1)
								npc.ShowPage(talker, "chamberlain_saius_q0708_22b.htm");
							else
								npc.ShowPage(talker, "chamberlain_saius_q0708_22.htm");
						}
						break;
					}
				}
				break;
			case Pinter:
				if(npc.IsMyLord(talker) == 0)
					_code = 19;
				if(npc.IsMyLord(talker) == 1)
					_code = 20;
				switch(_code)
				{
					case 19:
						if(npc.IsMyLord(talker) == 0)
						{
							c0 = npc.Pledge_GetLeader(talker);
							if(npc.IsNullCreature(c0) == 0)
							{
								if(HaveMemo(c0,708) == 1 && st.GetMemoState(c0,708) <= 3)
									ShowPage(talker,"blacksmith_pinter_q0708_02.htm");
								else if(st.GetMemoState(c0,708) == 4)
								{
									i1 = st.GetMemoStateEx(c0,708,1);
									if(talker.getObjectId() == i1)
										ShowPage(talker,"blacksmith_pinter_q0708_03.htm");
									else
										ShowPage(talker,"blacksmith_pinter_q0708_03a.htm");
								}
								else if(st.GetMemoState(c0,708)%10 == 5)
								{
									if(npc.OwnItemCount(talker,1867) >= 100 && npc.OwnItemCount(talker,1865) >= 100 && npc.OwnItemCount(talker,1869) >= 100 && npc.OwnItemCount(talker,1879) >= 50)
										ShowPage(talker,"blacksmith_pinter_q0708_08.htm");
									else
										ShowPage(talker,"blacksmith_pinter_q0708_07.htm");
								}
								else if(st.GetMemoState(c0,708)%10 == 9)
									ShowPage(talker,"blacksmith_pinter_q0708_12.htm");
							}
							else
								ShowPage(talker,"blacksmith_pinter_q0708_01.htm");
						}
						break;
					case 20:
						if(npc.IsMyLord(talker) == 1)
							ShowPage(talker,"blacksmith_pinter_q0708_13.htm");
						break;
				}
				break;
			case Bathis:
				if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 1)
					_code = 11;
				if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 2)
					_code = 12;
				if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 3)
					_code = 13;
				if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 4)
					_code = 14;

				switch(_code)
				{
					case 11:
						if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 1)
							ShowPage(talker,"captain_bathia_q0708_01.htm");
						break;
					case 12:
						if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 2)
							ShowPage(talker,"captain_bathia_q0708_03.htm");
						break;
					case 13:
						if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 3)
							ShowPage(talker,"captain_bathia_q0708_04.htm");
						break;
					case 14:
						if(HaveMemo(talker,708) == 1 && (st.GetMemoState(talker,708) / 10) == 4)
							ShowPage(talker,"captain_bathia_q0708_06.htm");
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
					if(st.HaveMemo(c0,708) == 1 && (st.GetMemoState(c0,708) / 10) == 2)
					{
						int i0 = st.GetMemoStateEx(c0,708,1);
						if(Rnd.get(100) < i0)
							npc.CreateOnePrivateEx(1027393, "q_duahan_of_glodio", 0, 0, npc.getX(), npc.getY(), npc.getZ(), 0, npc.GetIndexFromCreature(target), target.getObjectId(), npc.getNpcId());
						else
							st.SetMemoStateEx(c0,708,1,(i0 + 1));
					}
				}
			}
		}
		else if(npc.getNpcId() == 27393)
		{
			L2Player target = st.getPlayer();
			if(npc.IsNullCreature(target) == 0 && npc.DistFromMe(target) <= 1500)
			{
				L2Player c0 = npc.Pledge_GetLeader(target);
				if(npc.IsNullCreature(c0) == 0)
				{
					if(npc.DistFromMe(c0) <= 1500 && npc.HaveMemo(c0,708) == 1 && (npc.GetMemoState(c0,708) / 10) == 2)
					{
						npc.GiveItem1(c0,13848,1);
						int i0 = npc.GetMemoState(c0,708);
						npc.SetMemoState(c0,708,(i0 + 10));
						npc.SetFlagJournal(c0,708,7);
						npc.ShowQuestMark(c0,708);
						npc.SoundEffect(c0,"ItemSound.quest_middle");
						npc.Say(70856);
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