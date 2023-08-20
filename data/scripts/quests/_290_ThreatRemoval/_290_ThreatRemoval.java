package quests._290_ThreatRemoval;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._251_NoSecrets._251_NoSecrets;

/**
 * @author Drizzy
 * @date 24.02.11
 * @time 23:23:03
 */
public class _290_ThreatRemoval extends Quest implements ScriptFile
{
    //Npc
    private static final int PINAPS = 30201;
    //Mobs
    private static final int[] MOB = { 22775, 22776, 22777, 22780, 22781, 22782, 22783, 22784, 22785, 22778 };
    //Items
    private static final int tag = 15714;
    //Reward
    private static final int[] REWARD = { 959, 960, 9552 };


	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

    public _290_ThreatRemoval()
	{
		super(false);
        addStartNpc(PINAPS);
		for(int npcId : MOB)
			addKillId(npcId);
        addQuestItem(tag);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int npcId = npc.getNpcId();

		if (npcId != PINAPS)
			return event;

		if (event.equalsIgnoreCase("pinaps_q0290_03.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}

        if (event.equalsIgnoreCase("pinaps_q0290_06.htm"))
		{
			int j = Rnd.get(REWARD.length);
            if(j == 0)
            {
				if(st.getQuestItemsCount(tag) >= 400)
				{
					st.giveItems(REWARD[j], 1);
					st.takeItems(tag, 400);
				}
				else
					htmltext = "pinaps_q0290_04.htm";
            }
            if(j == 1)
            {
				if(st.getQuestItemsCount(tag) >= 400)
				{
					st.giveItems(REWARD[j], Rnd.get(1,3));
					st.takeItems(tag, 400);
				}
				else
					htmltext = "pinaps_q0290_04.htm";
            }
            if(j == 2)
            {
				if(st.getQuestItemsCount(tag) >= 400)
				{
					st.giveItems(REWARD[j], Rnd.get(1,2));
					st.takeItems(tag, 400);
				}
				else
					htmltext = "pinaps_q0290_04.htm";
            }
		}

        if (event.equalsIgnoreCase("pinaps_q0290_07.htm"))
		{
			if(st.getQuestItemsCount(tag) < 400)
				st.set("cond", "1");
			st.playSound(SOUND_MIDDLE);
		}

        if (event.equalsIgnoreCase("pinaps_q0290_09.htm"))
		{
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

		if (npcId != PINAPS)
			return htmltext;

		if(id == CREATED)
        {
            QuestState qs = st.getPlayer().getQuestState(_251_NoSecrets.class);
            if(qs != null && qs.isCompleted() && st.getPlayer().getLevel() >= 82)
                htmltext = "pinaps_q0290_02.htm";
            else
            {
                htmltext = "pinaps_q0290_01.htm";
                st.exitCurrentQuest(true);
            }
        }
		if(id == STARTED)
			if(cond == 1)
                htmltext = "pinaps_q0290_04.htm";
            if(cond == 2)
                htmltext = "pinaps_q0290_05.htm";

        return htmltext;
    }

    @Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int id = st.getState();
        int cond = st.getInt("cond");
		if(id == STARTED)
		{
            if(Rnd.chance(80))
            {
                st.giveItems(tag, 1, false);
                st.playSound(SOUND_ITEMGET);
            }
            if(cond == 1)
			{
                if(st.getQuestItemsCount(tag) >= 400)
                {
                    st.set("cond", "2");
                    st.playSound(SOUND_MIDDLE);
                }
			}
		}
		return null;
	}
}