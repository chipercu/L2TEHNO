package quests._690_JudesRequest;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _690_JudesRequest extends Quest implements ScriptFile
{
	private static final int DROP_RATE = 50;
	private static final int EVIL = 10327;
	private static final int[] REWARDS_60 = { 9975, 9968, 9970, 10545, 9972, 9971, 9974, 9969, 10544, 9967, 10374, 10380,
			10378, 10379, 10376, 10373, 10375, 10381, 10377 };

	private static final int[] REWARDS_100 = { 9984, 9977, 9979, 9981, 9980, 9983, 9978, 9976 };

	private static final int[] REWARDS_MAT = { 9624, 9617, 9619, 9621, 9620, 9623, 9618, 9616, 10547, 10546, 10398,
			10404, 10402, 10403, 10400, 10397, 10399, 10405, 10401 };

	private static final int JUDE = 32356;
	private static int[] MOBS = { 22398, 22399 };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _690_JudesRequest()
	{
		super(false);

		addStartNpc(JUDE);
		addTalkId(JUDE);
		addKillId(MOBS);
		addQuestItem(EVIL);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		long evil_count = st.getQuestItemsCount(EVIL);
		if(event.equals("jude_q0690_03.htm"))
		{
			if(st.getPlayer().getLevel() >= 78)
			{
				st.set("cond", "1");
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}
			else
			{
				htmltext = "jude_q0690_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(event.equals("jude_q0690_09.htm"))
		{
			if(evil_count >= 5)
			{
				htmltext = "jude_q0690_09.htm";
				st.takeItems(EVIL, 5);
				st.giveItems(REWARDS_MAT[Rnd.get(REWARDS_MAT.length)], 1);
				st.giveItems(REWARDS_MAT[Rnd.get(REWARDS_MAT.length)], 1);
				st.giveItems(REWARDS_MAT[Rnd.get(REWARDS_MAT.length)], 1);
			}
			else
				htmltext = "jude_q0690_09a.htm";
		}
		else if(event.equals("jude_q0690_07.htm"))
		{
			if(evil_count >= 200)
			{
				htmltext = "jude_q0690_07.htm";
				st.takeItems(EVIL, 200);
				if(ConfigValue.Alt100PercentRecipesS)
					st.giveItems(REWARDS_100[Rnd.get(REWARDS_100.length)], 1);
				else
					st.giveItems(REWARDS_60[Rnd.get(REWARDS_60.length)], 1);
			}
			else
				htmltext = "jude_q0690_07a.htm";
		}
		else if(event.equals("jude_q0690_08.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_GIVEUP);
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");
		long evil_count = st.getQuestItemsCount(EVIL);

		if(npcId == JUDE)
		{
			if(id == CREATED)
			{
				htmltext = "jude_q0690_01.htm";
				if(st.getPlayer().getLevel() < 78)
				{
					st.exitCurrentQuest(true);
					htmltext = "jude_q0690_02.htm";
				}
			}
			else if(id == STARTED)
			{
				if(cond == 1 && evil_count >= 200)
					htmltext = "jude_q0690_04.htm";
				else if(cond == 1 && evil_count >= 5 && evil_count <= 200)
					htmltext = "jude_q0690_05.htm";
				else
					htmltext = "jude_q0690_05a.htm";
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getRandomPartyMember(STARTED, ConfigValue.AltPartyDistributionRange);

		if(st.getState() != STARTED)
			return null;

		if(player != null)
		{
			QuestState sts = player.getQuestState(st.getQuest().getName());
			if(sts != null && Rnd.chance(DROP_RATE))
			{
				st.giveItems(EVIL, 1);
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}