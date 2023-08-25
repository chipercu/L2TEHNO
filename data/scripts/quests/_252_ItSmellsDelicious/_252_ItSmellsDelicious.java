package quests._252_ItSmellsDelicious;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * @author Drizzy
 * @date 24.02.11
 * @time 12:05:32
 */
public class _252_ItSmellsDelicious extends Quest implements ScriptFile
{
    //NPC
    private static final int STAN = 30200;
    //MOB
    private static final int COOK = 18908;
    private static final int[] MOB = { 22786, 22787, 22788 };
    //Item
    private static final int diary = 15500;
    private static final int page = 15501;
    public void onLoad()
    {}

    public void onReload()
    {}

    public void onShutdown()
    {}

    public _252_ItSmellsDelicious()
	{
		super(false);
        addStartNpc(STAN);
		for(int npcId : MOB)
			addKillId(npcId);
        addKillId(COOK);
        addQuestItem(diary, page);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();

		if (npcId != STAN)
			return event;

		if (event.equalsIgnoreCase("stan_q0252_05.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}

		else if (event.equalsIgnoreCase("stan_q0252_08.htm"))
		{
			st.unset("cond");
			st.giveItems(57, 147656);
			st.addExpAndSp(716238, 78324);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
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
			if (st.getPlayer().getLevel() >= 82)
				htmltext = "stan_q0252_01.htm";
			else
			{
				htmltext = "stan_q0252_02.htm";
				st.exitCurrentQuest(true);
			}
		if(id == STARTED)
			if(cond == 1)
				htmltext = "stan_q0252_06.htm";
            if(cond == 2)
                htmltext = "stan_q0252_07.htm";
        if(id == COMPLETED)
            htmltext = "stan_q0252_03.htm";

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
            if(cond == 1)
            {
                if((npcId == MOB[0] || npcId == MOB[1] || npcId == MOB[2]) && st.getQuestItemsCount(diary) < 10)
                {
					int i0 = Rnd.get(1000);
					if(i0 < 599)
					{
						st.giveItems(diary, 1, false);
						st.playSound(SOUND_ITEMGET);
					}
                }
                if(npcId == COOK && st.getQuestItemsCount(page) < 5)
                {
					int i0 = Rnd.get(1000);
					if(i0 < 360)
					{
						st.giveItems(page, 1, false);
						st.playSound(SOUND_ITEMGET);
					}
                }
                if(st.getQuestItemsCount(diary) == 10 && st.getQuestItemsCount(page) == 5)
                {
                    st.set("cond", "2");
                    st.playSound(SOUND_MIDDLE);
                }
            }
		}
		return null;
	}
}