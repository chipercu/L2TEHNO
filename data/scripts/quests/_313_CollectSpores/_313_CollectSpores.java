package quests._313_CollectSpores;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _313_CollectSpores extends Quest implements ScriptFile
{
	//NPC
	public final int Herbiel = 30150;
	//Mobs
	public final int SporeFungus = 20509;
	//Quest Items
	public final int SporeSac = 1118;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _313_CollectSpores()
	{
		super(false);

		addStartNpc(Herbiel);
		addTalkId(Herbiel);
		addKillId(SporeFungus);
		addQuestItem(SporeSac);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("green_q0313_05.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 8)
				htmltext = "green_q0313_03.htm";
			else
			{
				htmltext = "green_q0313_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1)
			htmltext = "green_q0313_06.htm";
		else if(cond == 2)
			if(st.getQuestItemsCount(SporeSac) < 10)
			{
				st.set("cond", "1");
				htmltext = "green_q0313_06.htm";
			}
			else
			{
				st.takeItems(SporeSac, -1);
				st.giveItems(ADENA_ID, 3500, true);
				st.playSound(SOUND_FINISH);
				htmltext = "green_q0313_07.htm";
				st.exitCurrentQuest(true);
			}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(cond == 1 && npcId == SporeFungus && Rnd.chance(70))
		{
			st.giveItems(SporeSac, 1);
			if(st.getQuestItemsCount(SporeSac) < 10)
				st.playSound(SOUND_ITEMGET);
			else
			{
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "2");
				st.setState(STARTED);
			}
		}
		return null;
	}
}