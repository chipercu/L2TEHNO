package quests._351_BlackSwan;

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
 * Квест Черный Леблядь, нужен для квеста Путь Лорда Инадрил, сделан с ПТС АИ, тобиш 100% по гуфу...
 **/
public class _351_BlackSwan extends Quest implements ScriptFile
{
	private static final int captain_gosta = 30916;
	private static final int iason_haine = 30969;
	private static final int head_blacksmith_roman = 30897;

	private static final int[] tasaba_lizardman =
	{
			20784,
			21639
	};

	private static final int[] tasaba_lizardman_shaman =
	{
			20785,
			21640
	};

	public _351_BlackSwan()
	{
		super(false);
		addStartNpc(captain_gosta);
		addTalkId(iason_haine);
		addTalkId(head_blacksmith_roman);
		addKillId(tasaba_lizardman);
		addKillId(tasaba_lizardman_shaman);
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int _choiceN = 0;
		int _code = -1;

		L2Player talker = st.getPlayer();

		switch(npc.getNpcId())
		{
			case captain_gosta:
				if(npc.HaveMemo(talker,351) == 0 && talker.getLevel() < 32)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Black Swan");
				}
				if(npc.HaveMemo(talker,351) == 0 && talker.getLevel() >= 32)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Black Swan");
				}
				if(npc.HaveMemo(talker,351) == 1 && npc.GetMemoState(talker,351) >= 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 2;
					npc.AddChoice(2,"Black Swan (In Progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,351) == 0 && talker.getLevel() < 32))
						{
							npc.SetCurrentQuestID(351);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"captain_gosta_q0351_01.htm");
						}
						break;
					case 1:
						if((npc.HaveMemo(talker,351) == 0 && talker.getLevel() >= 32))
						{
							npc.SetCurrentQuestID(351);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							if(npc.GetMemoCount(talker) < 41)
							{
								npc.ShowQuestPage(talker,"captain_gosta_q0351_02.htm",351);
							}
							else
							{
								npc.ShowPage(talker,"fullquest.htm");
							}
						}
						break;
					case 2:
						if((npc.HaveMemo(talker,351) == 1 && npc.GetMemoState(talker,351) >= 0))
						{
							npc.SetCurrentQuestID(351);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"captain_gosta_q0351_05.htm");
						}
						break;
					}
				break;
			case head_blacksmith_roman:
				if((npc.HaveMemo(talker,351) == 1 || npc.HaveMemo(talker,345) == 1) && npc.OwnItemCount(talker,4407) >= 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Black Swan (In Progress)");
				}
				if((npc.HaveMemo(talker,351) == 1 || npc.HaveMemo(talker,345) == 1) && npc.OwnItemCount(talker,4407) == 0)
				{
					_choiceN = (_choiceN + 1);
					_code = 1;
					npc.AddChoice(1,"Black Swan (In Progress)");
				}

				switch(_code)
				{
					case 0:
						if(((npc.HaveMemo(talker,351) == 1 || npc.HaveMemo(talker,345) == 1) && npc.OwnItemCount(talker,4407) >= 1))
						{
							npc.SetCurrentQuestID(351);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"head_blacksmith_roman_q0351_01.htm");
						}
						break;
					case 1:
						if(((npc.HaveMemo(talker,351) == 1 || npc.HaveMemo(talker,345) == 1) && npc.OwnItemCount(talker,4407) == 0))
						{
							npc.SetCurrentQuestID(351);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"head_blacksmith_roman_q0351_02.htm");
						}
						break;
				}
				break;
			case iason_haine:
				if(npc.HaveMemo(talker,351) == 1 && npc.GetMemoState(talker,351) == 1)
				{
					_choiceN = (_choiceN + 1);
					_code = 0;
					npc.AddChoice(0,"Black Swan (In Progress)");
				}

				switch(_code)
				{
					case 0:
						if((npc.HaveMemo(talker,351) == 1 && npc.GetMemoState(talker,351) == 1))
						{
							npc.SetCurrentQuestID(351);
							if(npc.GetInventoryInfo(talker,0) >= (npc.GetInventoryInfo(talker,1) * 0.800000) || npc.GetInventoryInfo(talker,2) >= (npc.GetInventoryInfo(talker,3) * 0.800000))
							{
								npc.ShowSystemMessage(talker,1118);
								return null;
							}
							npc.ShowPage(talker,"iason_haine_q0351_01.htm");
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
		if(Util.contains_int(tasaba_lizardman, npc.getNpcId()))
		{
			L2Player target = st.getPlayer();
			if(npc.OwnItemCount(target,4296) > 0 && npc.HaveMemo(target,351) == 1)
			{
				if(npc.IsNull(target) == 0 && npc.DistFromMe(target) <= 1500)
				{
					int i0 = Rnd.get(20);
					if(i0 < 10)
					{
						target.quest_last_reward_time = npc.GetCurrentTick();
						npc.GiveItem1(target,4297,1);
						npc.SoundEffect(target,"ItemSound.quest_itemget");
						if(Rnd.get(20) == 0)
						{
							npc.GiveItem1(target,4298,1);
						}
					}
					else if(i0 < 15)
					{
						target.quest_last_reward_time = npc.GetCurrentTick();
						npc.GiveItem1(target,4297,2);
						npc.SoundEffect(target,"ItemSound.quest_itemget");
						if(Rnd.get(20) == 0)
						{
							npc.GiveItem1(target,4298,1);
						}
					}
					else if(Rnd.get(100) < 4)
					{
						target.quest_last_reward_time = npc.GetCurrentTick();
						npc.GiveItem1(target,4298,1);
						npc.SoundEffect(target,"ItemSound.quest_itemget");
					}
				}
			}
		}
		else if(Util.contains_int(tasaba_lizardman_shaman, npc.getNpcId()))
		{
			L2Player target = st.getPlayer();
			if(npc.OwnItemCount(target,4296) > 0 && npc.HaveMemo(target,351) == 1)
			{
				if(npc.IsNull(target) == 0 && npc.DistFromMe(target) <= 1500)
				{
					int i0 = Rnd.get(20);
					if(npc.OwnItemCount(target,4296) > 0 && npc.HaveMemo(target,351) == 1)
					{
						if(i0 < 10)
						{
							if((npc.GetCurrentTick() - target.quest_last_reward_time) > 1)
							{
								target.quest_last_reward_time = npc.GetCurrentTick();
								npc.GiveItem1(target,4297,1);
								npc.SoundEffect(target,"ItemSound.quest_itemget");
								if(Rnd.get(20) == 0)
								{
									npc.GiveItem1(target,4298,1);
								}
							}
						}
						else if(i0 < 15)
						{
							if((npc.GetCurrentTick() - target.quest_last_reward_time) > 1)
							{
								target.quest_last_reward_time = npc.GetCurrentTick();
								npc.GiveItem1(target,4297,2);
								npc.SoundEffect(target,"ItemSound.quest_itemget");
								if(Rnd.get(20) == 0)
								{
									npc.GiveItem1(target,4298,1);
								}
							}
						}
						else if(Rnd.get(100) < 3)
						{
							if((npc.GetCurrentTick() - target.quest_last_reward_time) > 1)
							{
								target.quest_last_reward_time = npc.GetCurrentTick();
								npc.GiveItem1(target,4298,1);
								npc.SoundEffect(target,"ItemSound.quest_itemget");
							}
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