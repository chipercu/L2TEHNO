package quests._268_TracesOfEvil;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _268_TracesOfEvil extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	//NPC
	public final int KUNAI = 30559;
	//MOBS
	public final int SPIDER = 20474;
	public final int FANG_SPIDER = 20476;
	public final int BLADE_SPIDER = 20478;
	//ITEMS
	public final int CONTAMINATED = 10869;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _268_TracesOfEvil()
	{
		super(false);
		addStartNpc(KUNAI);
		addKillId(SPIDER, FANG_SPIDER, BLADE_SPIDER);
		addQuestItem(CONTAMINATED);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("trader_kunai_q0268_03.htm"))
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
		if(st.getInt("cond") == 0)
			if(st.getPlayer().getLevel() < 15)
			{
				htmltext = "trader_kunai_q0268_02.htm";
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "trader_kunai_q0268_01.htm";
		else if(st.getQuestItemsCount(CONTAMINATED) >= 30)
		{
			htmltext = "trader_kunai_q0268_06.htm";
			st.giveItems(ADENA_ID, 2474, true);
			st.addExpAndSp(8738, 409, true);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else
			htmltext = "trader_kunai_q0268_04.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		st.giveItems(CONTAMINATED, 1);
		if(st.getQuestItemsCount(CONTAMINATED) <= 29)
			st.playSound(SOUND_ITEMGET);
		else if(st.getQuestItemsCount(CONTAMINATED) >= 30)
		{
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "2");
			st.setState(STARTED);
		}
		return null;
	}
}