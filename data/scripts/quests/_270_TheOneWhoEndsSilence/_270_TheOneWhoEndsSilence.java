package quests._270_TheOneWhoEndsSilence;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._10288_SecretMission._10288_SecretMission;

public class _270_TheOneWhoEndsSilence extends Quest implements ScriptFile
{
	private static int[] FirstReward = { 10374, 10375, 10376, 10373, 10380, 10381, 10379, 10377, 10378 };
	private static int[] TwoReward = { 10398, 10399, 10397, 10400, 10405, 10404, 10402, 10401, 10403 };
	private static int[] ThreeReward = { 9898, 5595, 5594, 5593 };
	private static int[] MOBS = { 22790, 22791, 22793, 22794, 22795, 22796, 22797, 22798, 22799, 22800 };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _270_TheOneWhoEndsSilence()
	{
		super(true);
		addStartNpc(32757);
		addKillId(MOBS);
		addQuestItem(15526);
	}

	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("new_falsepriest_gremory_q0270_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if (event.equalsIgnoreCase("killmob"))
		{
			if (st.getQuestItemsCount(15526) == 0)
				return "new_falsepriest_gremory_q0270_06.htm";
			if (st.getQuestItemsCount(15526) < 100)
				return "new_falsepriest_gremory_q0270_07.htm";
			if (st.getQuestItemsCount(15526) >= 100)
				return "new_falsepriest_gremory_q0270_08.htm";
		}
		if (event.equalsIgnoreCase("100item"))
		{
			if (st.getQuestItemsCount(15526) >= 100)
			{
				st.takeItems(15526, 100);
				if (Rnd.chance(50))
					st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
				else
					st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
				return "new_falsepriest_gremory_q0270_09.htm";
			}
			else
				return "new_falsepriest_gremory_q0270_10.htm";
		}
		if (event.equalsIgnoreCase("200item"))
		{
			if (st.getQuestItemsCount(15526) >= 200)
			{
				st.takeItems(15526, 200);
				st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
				st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
				return "new_falsepriest_gremory_q0270_09.htm";
			}
			else
				return "new_falsepriest_gremory_q0270_10.htm";
		}
		if (event.equalsIgnoreCase("300item"))
		{
			if (st.getQuestItemsCount(15526) >= 300)
			{
				st.takeItems(15526, 300);
				st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
				st.giveItems(TwoReward[Rnd.get(TwoReward.length)], 1);
				st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
				return "new_falsepriest_gremory_q0270_09.htm";
			}
			else
				return "new_falsepriest_gremory_q0270_10.htm";
		}
		if (event.equalsIgnoreCase("400item"))
		{
			if (st.getQuestItemsCount(15526) >= 400)
			{
				st.takeItems(15526, 400);
				if (Rnd.chance(50))
				{
					st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
					st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
					st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
				}
				else
				{
					st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
					st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
					st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
					st.giveItems(TwoReward[Rnd.get(TwoReward.length)], 1);
				}
				return "new_falsepriest_gremory_q0270_09.htm";
			}
			else
				return "new_falsepriest_gremory_q0270_10.htm";
		}
		if (event.equalsIgnoreCase("500item"))
		{
			if (st.getQuestItemsCount(15526) >= 500)
			{
				st.takeItems(15526, 500);
				st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
				st.giveItems(FirstReward[Rnd.get(FirstReward.length)], 1);
				st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
				st.giveItems(ThreeReward[Rnd.get(ThreeReward.length)], 1);
				st.giveItems(TwoReward[Rnd.get(TwoReward.length)], 1);
				return "new_falsepriest_gremory_q0270_09.htm";
			}
			else
				return "new_falsepriest_gremory_q0270_10.htm";
		}
		if (event.equalsIgnoreCase("exit"))
		{
			st.takeItems(15526, -1);
			//st.setState(3);
			st.playSound(SOUND_FINISH);
			st.unset("cond");
			st.exitCurrentQuest(true);
			return "new_falsepriest_gremory_q0270_13.htm";
		}
		return event;
	}

	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		if (npcId == 32757)
		{
			if (state == 1)
			{
				QuestState qs = st.getPlayer().getQuestState(_10288_SecretMission.class);
				if (qs != null && qs.isCompleted() && st.getPlayer().getLevel() >= 82)
					return "new_falsepriest_gremory_q0270_01.htm";
				return "lvl.htm";
			}
			if (state == 2 && cond == 1)
				return "new_falsepriest_gremory_q0270_05.htm";
		}
		return "noquest";
	}

	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if (st.getState() != 2)
			return null;
		int npcId = npc.getNpcId();
		if (contains(MOBS, npcId) && Rnd.chance(50))
		{
			st.giveItems(15526, (int)ConfigValue.RateQuestsDrop);
			st.playSound("ItemSound.quest_itemget");
		}
		return null;
	}
}