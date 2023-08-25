package quests._631_DeliciousTopChoiceMeat;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * @Quest for location Beast Farm on Freya.
 * @based on a quest from Epilogue
 * @reauthor: Drizzy
 * @date: 12.03.11
 * @time: 20:36
 */
public class _631_DeliciousTopChoiceMeat extends Quest implements ScriptFile
{
	//NPC
	public final int TUNATUN = 31537;
	//MOBS
	public final int MOB_LIST[] = { 18878, 18879, 18892, 18893, 18899, 18900, 18885, 18886 }; // Mob 3 stage.
	//ITEMS
	public final int TOP_QUALITY_MEAT = 15534;
	//REWARDS
	public final static int Reward_rec[] = { 10373, 10374, 10375, 10376, 10377, 10378, 10379, 10380, 10381 };
    public final static int Reward_piece[] = {10397, 10398, 10399, 10400, 10401, 10402, 10403, 10404, 10405 };
    public final static int Reward_box[] = { 15482, 15483 };  //TODO this item and handler.
    public final static int [][] REWARD = { Reward_rec, Reward_piece, Reward_box };

    public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _631_DeliciousTopChoiceMeat()
	{
		super(false);

		addStartNpc(TUNATUN);

		addTalkId(TUNATUN);

		for(int i : MOB_LIST)
			addKillId(i);

		addQuestItem(TOP_QUALITY_MEAT);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("beast_herder_tunatun_q0631_0104.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}

		if(event.equalsIgnoreCase("beast_herder_tunatun_q0631_0201.htm") && st.getQuestItemsCount(TOP_QUALITY_MEAT) >= 120)
        {
            int i = Rnd.get(REWARD.length);
            if(i == 0)
            {
                int j = Rnd.get(Reward_rec.length);
                st.giveItems(Reward_rec[j], 1);
            }
            if(i == 1)
            {
                int j = Rnd.get(Reward_piece.length);
                st.giveItems(Reward_piece[j], Rnd.get(1,9));
            }
            if(i == 2)
            {
                int j = Rnd.get(Reward_box.length);
                st.giveItems(Reward_box[j], Rnd.get(1,2));
            }
            htmltext = "beast_herder_tunatun_q0631_0202.htm";
            st.takeItems(TOP_QUALITY_MEAT, -1);
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
		if(cond < 1)
		{
			if(st.getPlayer().getLevel() < 82)
			{
				htmltext = "beast_herder_tunatun_q0631_0103.htm";
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "beast_herder_tunatun_q0631_0101.htm";
		}
		else if(cond == 1)
			htmltext = "beast_herder_tunatun_q0631_0106.htm";
		else if(cond == 2)
		{
			if(st.getQuestItemsCount(TOP_QUALITY_MEAT) < 120)
			{
				htmltext = "beast_herder_tunatun_q0631_0106.htm";
				st.set("cond", "1");
			}
			else
				htmltext = "beast_herder_tunatun_q0631_0105.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 1 && Rnd.chance(80))
		{
			st.giveItems(TOP_QUALITY_MEAT, (int)ConfigValue.RateQuestsDrop);
			if(st.getQuestItemsCount(TOP_QUALITY_MEAT) < 120)
				st.playSound(SOUND_ITEMGET);
			else
			{
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "2");
			}
		}
		return null;
	}
}