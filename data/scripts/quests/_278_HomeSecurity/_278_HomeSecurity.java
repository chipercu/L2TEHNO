package quests._278_HomeSecurity;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 *@author Drizzy
 *@date 05.03.11
 *@time 01:07:43
 */
public class _278_HomeSecurity extends Quest implements ScriptFile
{
    //Npc
    private static final int TUNATUN = 31537;
    //Mobs
    private static final int[] MOB = { 18906, 18907 };
    //Items
    private static final int mane = 15531;
    //Reward
    private static final int[] REWARD = { 959, 960, 9553 };


	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

    public _278_HomeSecurity()
	{
		super(false);
        addStartNpc(TUNATUN);
		for(int npcId : MOB)
			addKillId(npcId);
        addQuestItem(mane);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int npcId = npc.getNpcId();

		if (npcId != TUNATUN)
			return event;

		if (event.equalsIgnoreCase("beast_herder_tunatun_q0278_04.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}

        if (event.equalsIgnoreCase("beast_herder_tunatun_q0278_07.htm")) //
		{
			int j = Rnd.get(REWARD.length);
            if(j == 0)
            {
                st.giveItems(REWARD[j], 1);
                st.takeItems(mane, 300);
            }
            if(j == 1)
            {
                st.giveItems(REWARD[j], Rnd.get(1,10));
                st.takeItems(mane, 300);
            }
            if(j == 2)
            {
                st.giveItems(REWARD[j], Rnd.get(1,2));
                st.takeItems(mane, 300);
            }
            st.unset("cond");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
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

		if (npcId != TUNATUN)
			return htmltext;

		if(id == CREATED)
        {
            if(st.getPlayer().getLevel() >= 82)
                htmltext = "beast_herder_tunatun_q0278_01.htm";
            else
            {
                htmltext = "beast_herder_tunatun_q0278_03.htm";
                st.exitCurrentQuest(true);
            }
        }
		if(id == STARTED)
			if(cond == 1)
                htmltext = "beast_herder_tunatun_q0278_06.htm";
            if(cond == 2)
                htmltext = "beast_herder_tunatun_q0278_05.htm";    //

        return htmltext;
    }

    @Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int id = st.getState();
        int cond = st.getInt("cond");
		if(id == STARTED)
		{
            if(Rnd.chance(60))
            {
                st.giveItems(mane, 1, false);
                st.playSound(SOUND_ITEMGET);
            }
            if(cond == 1)
                if(st.getQuestItemsCount(mane) >= 300)
                {
                    st.set("cond", "2");
                    st.playSound(SOUND_MIDDLE);
                }
		}
		return null;
	}
}
