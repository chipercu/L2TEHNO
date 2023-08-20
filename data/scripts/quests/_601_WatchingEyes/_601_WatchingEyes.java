package quests._601_WatchingEyes;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _601_WatchingEyes extends Quest implements ScriptFile
{
	//NPC
	private static int EYE_OF_ARGOS = 31683;
	//ITEMS
	private static int PROOF_OF_AVENGER = 7188;
	//MOBS
	private static int[] MOBS = { 21306, 21308, 21309, 21310, 21311 };
	private static int[][] REWARDS = { { 6699, 90000, 0, 19 }, { 6698, 80000, 20, 39 }, { 6700, 40000, 40, 49 },
			{ 0, 230000, 50, 100 } };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _601_WatchingEyes()
	{
		super(true);

		addStartNpc(EYE_OF_ARGOS);

		addKillId(MOBS);

		addQuestItem(PROOF_OF_AVENGER);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("eye_of_argos_q0601_0104.htm"))
			if(st.getPlayer().getLevel() < 71)
			{
				htmltext = "eye_of_argos_q0601_0103.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				st.setState(STARTED);
				st.set("cond", "1");
				st.playSound(SOUND_ACCEPT);
			}
		else if(event.equalsIgnoreCase("eye_of_argos_q0601_0201.htm"))
		{
			int random = Rnd.get(101);
			int i = 0;
			int item = 0;
			int adena = 0;
			while(i < REWARDS.length)
			{
				item = REWARDS[i][0];
				adena = REWARDS[i][1];
				if(REWARDS[i][2] <= random && random <= REWARDS[i][3])
					break;
				i++;
			}
			st.giveItems(ADENA_ID, adena , true);
			if(item != 0)
			{
				st.giveItems(item, 5, true);
				st.addExpAndSp(120000, 10000, true);
			}
			st.takeItems(PROOF_OF_AVENGER, -1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(cond == 0)
			htmltext = "eye_of_argos_q0601_0101.htm";
		else if(cond == 1)
			htmltext = "eye_of_argos_q0601_0106.htm";
		else if(cond == 2 && st.getQuestItemsCount(PROOF_OF_AVENGER) >= 100)
			htmltext = "eye_of_argos_q0601_0105.htm";
		else
		{
			htmltext = "eye_of_argos_q0601_0202.htm";
			st.set("cond", "1");
		}
		return htmltext;
	}

	@Override
    public String onKill(L2NpcInstance npc, QuestState st)
    {
        if(st.getInt("cond") == 1)
        {
            st.rollAndGive(PROOF_OF_AVENGER, (int)ConfigValue.RateQuestsDrop, (int)ConfigValue.RateQuestsDrop, 100, 50);
            st.playSound(SOUND_ITEMGET);
            if (st.getQuestItemsCount(PROOF_OF_AVENGER) >= 100)
            {
                st.set("cond", "2");
                st.playSound(SOUND_MIDDLE);
            }
        }
        return null;
    }	
}