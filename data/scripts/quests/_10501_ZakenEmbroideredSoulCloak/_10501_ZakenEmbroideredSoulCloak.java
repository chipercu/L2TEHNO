package quests._10501_ZakenEmbroideredSoulCloak;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;


public class _10501_ZakenEmbroideredSoulCloak extends Quest implements ScriptFile 
{
	// NPC's
	private static final int OLF_ADAMS = 32612;
	// Mob's
	private static final int ZAKEN_HIGH = 29181;
	// Quest Item's
	private static final int SOUL_ZAKEN = 21722;
	// Item's
	private static final int CLOAK_OF_ZAKEN = 21719;

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

    public _10501_ZakenEmbroideredSoulCloak() 
	{
		super(PARTY_ALL);
		addStartNpc(OLF_ADAMS);
		addTalkId(OLF_ADAMS);
		addKillId(ZAKEN_HIGH);
		addQuestItem(SOUL_ZAKEN);
    }

    @Override
    public String onEvent(String event, QuestState st, L2NpcInstance npc) 	
	{
		if(event.equalsIgnoreCase("olf_adams_q10501_02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

    @Override
    public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 78)
				htmltext = "olf_adams_q10501_01.htm";
			else
			{
				htmltext = "olf_adams_q10501_00.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1)
			htmltext = "olf_adams_q10501_03.htm";
		else if(cond == 2)
			if(st.getQuestItemsCount(SOUL_ZAKEN) < 20)
			{
				st.setCond(1);
				htmltext = "olf_adams_q10501_03.htm";
			}
			else
			{
				st.takeItems(SOUL_ZAKEN, -1);
				st.giveItems(CLOAK_OF_ZAKEN, 1);
				st.playSound(SOUND_FINISH);
				htmltext = "olf_adams_q10501_04.htm";
				st.exitCurrentQuest(false);
			}
		return htmltext;
    }

    @Override
    public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 1 && npcId == ZAKEN_HIGH)
		{
			if(st.getQuestItemsCount(SOUL_ZAKEN) < 20)
			{
				st.giveItems(SOUL_ZAKEN, Rnd.get(1,3), false);
                st.playSound(SOUND_ITEMGET);
			}
			if(st.getQuestItemsCount(SOUL_ZAKEN) >= 20)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
    }
}