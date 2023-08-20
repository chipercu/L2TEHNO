package quests._251_NoSecrets;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * @author Drizzy
 * @date 24.02.11
 * @time 22:04:12
 */

public class _251_NoSecrets extends Quest implements ScriptFile
{
    //Npc
    private static final int PINAPS = 30201;
    //Mobs
    private static final int[] MOB = { 22775, 22777, 22778 };
    private static final int[] MOB1 = { 22781, 22783, 22780, 22782, 22784 };
    //Items
    private static final int diary = 15508;
    private static final int timetable = 15509;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

    public _251_NoSecrets()
	{
		super(false);
        addStartNpc(PINAPS);
		for(int npcId : MOB)
			addKillId(npcId);
		for(int npcId : MOB1)
			addKillId(npcId);
        addQuestItem(diary, timetable);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();

		if (npcId != PINAPS)
			return event;

		if (event.equalsIgnoreCase("pinaps_q0251_05.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
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
 			if (st.getPlayer().getLevel() >= 82)
				htmltext = "pinaps_q0251_01.htm";
			else
			{
				htmltext = "pinaps_q0251_02.htm";
				st.exitCurrentQuest(true);
			}
        }
		if(id == STARTED)
			if(cond == 1)
				htmltext = "pinaps_q0251_06.htm";
            if(cond == 2)
            {
                htmltext = "pinaps_q0251_07.htm";
                st.unset("cond");
                st.giveItems(57, 313355);
                st.addExpAndSp(56787, 160578);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        if(id == COMPLETED)
            htmltext = "pinaps_q0252_03.htm";

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
                if((npcId == MOB[0] || npcId == MOB[1] || npcId == MOB[2]) && st.getQuestItemsCount(timetable) < 5 && Rnd.get(1000) < 870)
                {
                    st.giveItems(timetable, 1, false);
                    st.playSound(SOUND_ITEMGET);
                }
                if((npcId == MOB1[0] || npcId == MOB1[1] || npcId == MOB1[2] || npcId == MOB1[3] || npcId == MOB1[4]) && st.getQuestItemsCount(diary) < 10 && Rnd.chance(15))
                {
                    st.giveItems(diary, 1, false);
                    st.playSound(SOUND_ITEMGET);
                }
                if(st.getQuestItemsCount(timetable) >= 5 && st.getQuestItemsCount(diary) >= 10)
                {
                    st.set("cond", "2");
                    st.playSound(SOUND_MIDDLE);
                }
            }
		}
		return null;
	}
}