package quests._709_PathToBecomingALordDion;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import l2open.util.Util;

/**
 * Запиздохал Diagod.
 * open-team.ru 
 * Квест Путь Лорда Дион, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _709_PathToBecomingALordDion extends Quest implements ScriptFile
{
	private static final int Crosby = 35142;
	private static final int Rouke = 31418;
	private static final int Sophia = 30735;

	private static final int q_bloody_senior = 27392;

	private static final int[] ol_mahum_chief_leader =
	{
			20211, 20207, 20208, 20210, 20209
	};

	private static final int[] manadragora =
	{		
			20154, 20155, 20156, 20223, 20549, 20063, 20547
	};

	private static final int DionCastle = 2;

	public _709_PathToBecomingALordDion()
	{
		super(false);
		addStartNpc(Crosby);
		addTalkId(Sophia, Rouke);
		addKillId(ol_mahum_chief_leader);
		addKillId(manadragora);
		addKillId(q_bloody_senior);
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
			case Crosby:
				if(npc.HaveMemo(talker, 709) == 0 && npc.IsMyLord(talker) == 1)
					_code = 0;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 1 && npc.IsMyLord(talker) == 1)
					_code = 1;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 2 && npc.IsMyLord(talker) == 1)
					_code = 2;
				if(npc.IsMyLord(talker) == 0)
					_code = 3;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 3)
					_code = 4;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 4)
					_code = 5;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 5)
					_code = 6;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 <= 1 && npc.GetMemoState(talker, 709) % 10 == 9)
					_code = 7;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 == 1 && npc.GetMemoState(talker, 709) % 10 != 9)
					_code = 8;
				if(npc.HaveMemo(talker, 709) == 1 && (npc.GetMemoState(talker, 709) / 10 == 2 || npc.GetMemoState(talker, 709) / 10 == 3) && npc.GetMemoState(talker, 709) % 10 != 9)
					_code = 9;
				if(npc.HaveMemo(talker, 709) == 1 && (npc.GetMemoState(talker, 709) / 10 == 2 || npc.GetMemoState(talker, 709) / 10 == 3) && npc.GetMemoState(talker, 709) % 10 == 9)
					_code = 10;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 == 4 && npc.GetMemoState(talker, 709) % 10 != 9)
					_code = 11;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 == 4 && npc.GetMemoState(talker, 709) % 10 == 9 && npc.IsMyLord(talker) == 1)
					_code = 12;
				switch(_code)
				{
					case 0:
					{
						if(npc.HaveMemo(talker, 709) == 0 && npc.IsMyLord(talker) == 1)
						{
							if(GetMemoCount(talker) < 41)
							{
								if(npc.IsDominionOfLord(82) == 0)
									npc.ShowQuestPage(talker, "chamberlain_crosby_q0709_01.htm", 709);
								else
									npc.ShowQuestPage(talker, "chamberlain_crosby_q0709_03.htm", 709);
							}
							else
								npc.ShowPage(talker, "fullquest.htm");
						}
						break;
					}
					case 1:
					{
						if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 1 && npc.IsMyLord(talker) == 1)
						{
							if(npc.GetCurrentTick() - npc.GetMemoStateEx(talker, 709, 1) < 60)
								npc.ShowPage(talker, "chamberlain_crosby_q0709_05.htm");
							else
							{
								npc.SetMemoState(talker, 709, 2);
								npc.SetMemoStateEx(talker, 709, 1, 0);
								npc.ShowPage(talker, "chamberlain_crosby_q0709_06.htm");
							}
						}
						break;
					}
			        case 2:
			        {
						if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 2 && npc.IsMyLord(talker) == 1)
							npc.ShowPage(talker, "chamberlain_crosby_q0709_07.htm");
							break;
			        }
			        case 3:
			        {
						if(npc.IsMyLord(talker) == 0)
						{
							if(npc.Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
							{
								c0 = npc.Pledge_GetLeader(talker);
								if(npc.IsNullCreature(c0) == 0)
								{
									if(npc.HaveMemo(c0, 709) == 1 && npc.GetMemoState(c0, 709) == 3)
									{
										if(npc.DistFromMe(c0) <= 1500)
											npc.ShowPage(talker, "chamberlain_crosby_q0709_11.htm");
										else
											npc.ShowPage(talker, "chamberlain_crosby_q0709_10.htm");
									}
									else if(npc.HaveMemo(c0, 709) == 1 && npc.GetMemoState(c0, 709) == 4)
										npc.ShowPage(talker, "chamberlain_crosby_q0709_13a.htm");
									else
										npc.ShowPage(talker, "chamberlain_crosby_q0709_09c.htm");
								}
								else
									npc.ShowPage(talker, "chamberlain_crosby_q0709_09b.htm");
							}
							else
								npc.ShowPage(talker, "chamberlain_crosby_q0709_09a.htm");
						}
						break;
					}
			        case 4:
						if((npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 3))
							npc.ShowPage(talker, "chamberlain_crosby_q0709_14.htm");
						break;
			        case 5:
						if((npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 4))
							npc.ShowPage(talker, "chamberlain_crosby_q0709_15.htm");
						break;
			        case 6:
			        {
						if((npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) == 5))
						{
							i0 = npc.GetMemoState(talker, 709);
							npc.SetMemoState(talker, 709, i0 + 10);
							npc.ShowPage(talker, "chamberlain_crosby_q0709_16.htm");
							npc.SetFlagJournal(talker, 709, 5);
							npc.ShowQuestMark(talker, 709);
							npc.SoundEffect(talker, "ItemSound.quest_middle");
						}
						break;
					}
			        case 7:
			        {
						if((npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 <= 1 && npc.GetMemoState(talker, 709) % 10 == 9))
						{
							i0 = npc.GetMemoState(talker, 709) / 10;
							i1 = npc.GetMemoState(talker, 709);
							if(i0 == 0)
							{
								npc.SetMemoState(talker, 709, i1 + 10);
								npc.SetFlagJournal(talker, 709, 5);
								npc.ShowQuestMark(talker, 709);
								npc.SoundEffect(talker, "ItemSound.quest_middle");
							}
							npc.ShowPage(talker, "chamberlain_crosby_q0709_17.htm");
						}
						break;
					}
			        case 8:
						if((npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 == 1 && npc.GetMemoState(talker, 709) % 10 != 9))
							npc.ShowPage(talker, "chamberlain_crosby_q0709_18.htm");
						break;
			        case 9:
						if((npc.HaveMemo(talker, 709) == 1 && (npc.GetMemoState(talker, 709) / 10 == 2 || npc.GetMemoState(talker, 709) / 10 == 3) && npc.GetMemoState(talker, 709) % 10 != 9))
							npc.ShowPage(talker, "chamberlain_crosby_q0709_19.htm");
						break;
			        case 10:
						if((npc.HaveMemo(talker, 709) == 1 && (npc.GetMemoState(talker, 709) / 10 == 2 || npc.GetMemoState(talker, 709) / 10 == 3) && npc.GetMemoState(talker, 709) % 10 == 9))
							npc.ShowPage(talker, "chamberlain_crosby_q0709_20.htm");
						break;
			        case 11:
						if((npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 == 4 && npc.GetMemoState(talker, 709) % 10 != 9))
							npc.ShowPage(talker, "chamberlain_crosby_q0709_21.htm");
						break;
			        case 12:
						if((npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) / 10 == 4 && npc.GetMemoState(talker, 709) % 10 == 9 && npc.IsMyLord(talker) == 1))
						{
							if(npc.GetDominionWarState(82) == 5 || npc.Castle_IsUnderSiege())
								npc.ShowPage(talker, "chamberlain_crosby_q0709_22a.htm");
							else if(npc.Fortress_GetContractStatus(103) == -1)
								npc.ShowPage(talker, "chamberlain_crosby_q0709_22b.htm");
							else
								npc.ShowPage(talker, "chamberlain_crosby_q0709_22.htm");
						}
						break;
				}
				break;
			case Rouke:
				if(npc.IsMyLord(talker) == 0)
					_code = 7;
				if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) % 10 == 9)
					_code = 8;
				if(npc.IsMyLord(talker) == 1)
					_code = 9;

				switch(_code)
				{
					case 7:
					{
						if(npc.IsMyLord(talker) == 0)
						{
							c0 = npc.Pledge_GetLeader(talker);
							if(npc.IsNullCreature(c0) == 0)
							{
								if(npc.HaveMemo(c0, 709) == 1 && npc.GetMemoState(c0, 709) == 4)
								{
									i0 = npc.GetMemoStateEx(c0, 709, 1);
									if(talker.getObjectId() == i0)
										npc.ShowPage(talker, "scroll_seller_rouke_q0709_03.htm");
									else
										npc.ShowPage(talker, "scroll_seller_rouke_q0709_02a.htm");
								}
								else if(npc.HaveMemo(c0, 709) == 1 && npc.GetMemoState(c0, 709) % 10 == 5)
								{
									i0 = npc.GetMemoStateEx(c0, 709, 1);
									if(talker.getObjectId() == i0)
									{
										if(npc.OwnItemCount(talker, 13849) >= 100)
											npc.ShowPage(talker, "scroll_seller_rouke_q0709_08.htm");
										else
											npc.ShowPage(talker, "scroll_seller_rouke_q0709_07.htm");
									}
									else
										npc.ShowPage(talker, "scroll_seller_rouke_q0709_07a.htm");
								}
								else
									npc.ShowPage(talker, "scroll_seller_rouke_q0709_02.htm");
							}
							else
								npc.ShowPage(talker, "scroll_seller_rouke_q0709_01.htm");
						}
						break;
					}
					case 8:
						if(npc.HaveMemo(talker, 709) == 1 && npc.GetMemoState(talker, 709) % 10 == 9)
							npc.ShowPage(talker, "scroll_seller_rouke_q0709_12.htm");
						  break;
					case 9:
						if(npc.IsMyLord(talker) == 1)
							npc.ShowPage(talker, "scroll_seller_rouke_q0709_13.htm");
						  break;
				}
				break;
			case Sophia:
				if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 1 && npc.IsMyLord(talker) == 1)
					_code = 8;
				if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 2 && npc.IsMyLord(talker) == 1)
					_code = 9;
				if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 3 && npc.IsMyLord(talker) == 1)
					_code = 10;
				if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 4 && npc.IsMyLord(talker) == 1)
					_code = 11;

				switch(_code)
				{
					case 8:
						if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 1 && npc.IsMyLord(talker) == 1)
							npc.ShowPage(talker,"sophia_q0709_01.htm");
						break;
					case 9:
						if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 2 && npc.IsMyLord(talker) == 1)
							npc.ShowPage(talker,"sophia_q0709_03.htm");
						break;
					case 10:
						if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 3 && npc.IsMyLord(talker) == 1)
							npc.ShowPage(talker,"sophia_q0709_04.htm");
						break;
					case 11:
						if(npc.HaveMemo(talker,709) == 1 && (npc.GetMemoState(talker,709) / 10) == 4 && npc.IsMyLord(talker) == 1)
							npc.ShowPage(talker,"sophia_q0709_07.htm");
						break;
					}
					break;
		}
		return null;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(Util.contains_int(manadragora, npc.getNpcId()))
		{
			L2Player target = st.getPlayer();
			if(npc.IsNullCreature(target) == 0 && npc.DistFromMe(target) <= 1500)
			{
				L2Player c0 = npc.Pledge_GetLeader(target);
				if(npc.IsNullCreature(c0) == 0)
				{
					if(npc.HaveMemo(c0,709) == 1 && (npc.GetMemoState(c0,709) % 10) == 5)
					{
						npc.GiveItem1(target,13849,1);
						npc.SoundEffect(target,"ItemSound.quest_itemget");
					}
				}
			}
		}
		else if(Util.contains_int(ol_mahum_chief_leader, npc.getNpcId()))
		{
			L2Player target = st.getPlayer();
			if(npc.IsNullCreature(target) == 0 && npc.DistFromMe(target) <= 1500)
			{
				L2Player c0 = npc.Pledge_GetLeader(target);
				if(npc.IsNullCreature(c0) == 0)
				{
					if(npc.DistFromMe(c0) <= 1500 && npc.HaveMemo(c0,709) == 1 && (npc.GetMemoState(c0,709) / 10) == 2)
					{
						int i0 = c0.param1;
						if(i0 >= 0)
						{
							if(Rnd.get(100) < c0.param1)
								npc.CreateOnePrivateEx(1027392,"q_bloody_senior",0,0,npc.getX(),npc.getY(),npc.getZ(),0,npc.GetIndexFromCreature(target),target.getObjectId(),npc.getNpcId());
							else
								c0.param1 = i0 + 1;
						}
						else
							c0.param1 = 1;
					}
				}
			}
		}
		else if(q_bloody_senior == npc.getNpcId())
		{
			L2Player target = st.getPlayer();
			if(npc.IsNullCreature(target) == 0 && npc.DistFromMe(target) <= 1500)
			{
				L2Player c0 = npc.Pledge_GetLeader(target);
				if(npc.IsNullCreature(c0) == 0)
				{
					if(npc.DistFromMe(c0) <= 1500 && npc.HaveMemo(c0,709) == 1 && (npc.GetMemoState(c0,709) / 10) == 2)
					{
						npc.GiveItem1(c0,13850,1);
						int i0 = npc.GetMemoState(c0,709);
						npc.SetMemoState(c0,709,(i0 + 10));
						npc.SetFlagJournal(c0,709,7);
						npc.ShowQuestMark(c0,709);
						npc.SoundEffect(c0,"ItemSound.quest_middle");
						npc.Say(npc.MakeFString(70956,"","","","",""));
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