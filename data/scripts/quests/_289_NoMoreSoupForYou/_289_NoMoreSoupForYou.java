package quests._289_NoMoreSoupForYou;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._252_ItSmellsDelicious._252_ItSmellsDelicious;

/**
 * @Author Drizzy
 * @date 25.02.11
 * @time 00:46:32
 */
public class _289_NoMoreSoupForYou extends Quest implements ScriptFile
{
    //Npc
    private static final int STAN = 30200;
    //Mobs
    private static final int[] MOB = { 18908, 22779, 22786, 22787, 22788 };
    //Items
    private static final int full = 15712;
    private static final int empty = 15713;
    private static final int fruit = 15507;
    //Reward
    private static final int weapon[] = { 10377, 10401 };
    private static final int ARMOR[] = {15778, 15781, 15775, 15784, 15787, 15791, 15814, 15813, 15812, 15645, 15648, 15651, 15654, 15657, 15693, 15772, 15773, 15774};


	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

    public _289_NoMoreSoupForYou()
	{
		super(false);
        addStartNpc(STAN);
		for(int npcId : MOB)
			addKillId(npcId);
        addQuestItem(full,empty,fruit);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int npcId = npc.getNpcId();

		if (npcId != STAN)
			return event;

		if (event.equalsIgnoreCase("stan_q0289_04.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
            st.giveItems(fruit, 500);
		}

		if (event.equalsIgnoreCase("stan_q0289_14.htm"))
		{
			st.unset("cond");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}

		if (event.equalsIgnoreCase("stan_q0289_06.htm"))
		{
            st.giveItems(fruit, 500);
		}

		if (event.equalsIgnoreCase("rewardweap"))
		{
            if(st.getQuestItemsCount(full) < 500)
                return "stan_q0289_08.htm";
            else
            {
                if(st.getQuestItemsCount(full) > 500)
                {
                    int j = Rnd.get(weapon.length);
                    if(j == 0)
                    {
                        st.takeItems(full, 500);
                        st.giveItems(weapon[j], 1);
                    }
                    if(j == 1)
                    {
                        st.takeItems(full, 500);
                        st.giveItems(weapon[j], Rnd.get(3,6));
                    }
                    return "stan_q0289_09.htm";
                }
            }
		}

		if (event.equalsIgnoreCase("rewardarm"))
		{
            if(st.getQuestItemsCount(full) < 100)
                return "stan_q0289_10.htm";
            else
            {
                if(st.getQuestItemsCount(full) > 100)
                {
                    int k = Rnd.get(ARMOR.length);
                    st.takeItems(full, 100);
                    st.giveItems(ARMOR[k], 1);
                    return "stan_q0289_11.htm";
                }
            }
		}

		if (event.equalsIgnoreCase("exchange"))
		{
            if(st.getQuestItemsCount(empty) > 2)
            {
                int count = (int) st.getQuestItemsCount(empty);
                st.takeItems(empty,count);
                st.giveItems(full,count/2);
                return "stan_q0289_07.htm";
            }
		}

		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");

		if (npcId != STAN)
			return htmltext;

		if(id == CREATED)
        {
            QuestState qs = st.getPlayer().getQuestState(_252_ItSmellsDelicious.class);
            if(qs != null && qs.isCompleted() && st.getPlayer().getLevel() >= 82)
                htmltext = "stan_q0289_01.htm";
            else
            {
                htmltext = "stan_q0289_02.htm";
                st.exitCurrentQuest(true);
            }
        }
		if(id == STARTED)
			if(cond == 1)
            {
                int count = (int) st.getQuestItemsCount(empty);
                int count1 = (int) st.getQuestItemsCount(full);
                if(count + count1 < 100)
                    htmltext = "stan_q0289_05.htm";
                else
                {
                    htmltext = "stan_q0289_07.htm";
                }

            }
        if(id == COMPLETED)
        {}
        return htmltext;
    }

    @Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
        int cond = st.getInt("cond");
		if(id == STARTED)
		{
            if(Rnd.chance(40))
            {
                st.giveItems(full, 1);
                st.playSound(SOUND_ITEMGET);
            }
            else
            {
                st.giveItems(empty, 1);
                st.playSound(SOUND_ITEMGET);
            }
		}
		return null;
	}
}