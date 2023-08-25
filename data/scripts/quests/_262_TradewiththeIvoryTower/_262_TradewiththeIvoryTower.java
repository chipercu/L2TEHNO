package quests._262_TradewiththeIvoryTower;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _262_TradewiththeIvoryTower extends Quest implements ScriptFile
{
	//NPC
	public final int VOLODOS = 30137;

	//MOB
	public final int GREEN_FUNGUS = 20007;
	public final int BLOOD_FUNGUS = 20400;

	public final int FUNGUS_SAC = 707;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _262_TradewiththeIvoryTower()
	{
		super(false);

		addStartNpc(VOLODOS);
		addKillId(new int[] { BLOOD_FUNGUS, GREEN_FUNGUS });
		addQuestItem(new int[] { FUNGUS_SAC });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("vollodos_q0262_03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 8)
			{
				htmltext = "vollodos_q0262_02.htm";
				return htmltext;
			}
			htmltext = "vollodos_q0262_01.htm";
			st.exitCurrentQuest(true);
		}
		else if(cond == 1 && st.getQuestItemsCount(FUNGUS_SAC) < 10)
			htmltext = "vollodos_q0262_04.htm";
		else if(cond == 2 && st.getQuestItemsCount(FUNGUS_SAC) >= 10)
		{
			st.giveItems(ADENA_ID, 3000);
			st.takeItems(FUNGUS_SAC, -1);
			st.set("cond", "0");
			st.playSound(SOUND_FINISH);
			htmltext = "vollodos_q0262_05.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int random = Rnd.get(10);
		if(st.getInt("cond") == 1 && st.getQuestItemsCount(FUNGUS_SAC) < 10)
			if(npcId == GREEN_FUNGUS && random < 3 || npcId == BLOOD_FUNGUS && random < 4)
			{
				st.giveItems(FUNGUS_SAC, 1);
				if(st.getQuestItemsCount(FUNGUS_SAC) == 10)
				{
					st.set("cond", "2");
					st.playSound(SOUND_MIDDLE);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}