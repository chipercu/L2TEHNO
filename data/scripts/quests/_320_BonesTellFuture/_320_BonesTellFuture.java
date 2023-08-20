package quests._320_BonesTellFuture;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _320_BonesTellFuture extends Quest implements ScriptFile
{
	//item
	public final int BONE_FRAGMENT = 809;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _320_BonesTellFuture()
	{
		super(false);

		addStartNpc(30359);
		addTalkId(30359);

		addKillId(20517);
		addKillId(20518);

		addQuestItem(BONE_FRAGMENT);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("tetrarch_kaitar_q0320_04.htm"))
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
			if(st.getPlayer().getRace() != Race.darkelf)
			{
				htmltext = "tetrarch_kaitar_q0320_00.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getPlayer().getLevel() >= 10)
				htmltext = "tetrarch_kaitar_q0320_03.htm";
			else
			{
				htmltext = "tetrarch_kaitar_q0320_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(st.getQuestItemsCount(BONE_FRAGMENT) < 10)
			htmltext = "tetrarch_kaitar_q0320_05.htm";
		else
		{
			htmltext = "tetrarch_kaitar_q0320_06.htm";
			st.giveItems(ADENA_ID, 8470, true);
			st.takeItems(BONE_FRAGMENT, -1);
			st.exitCurrentQuest(true);
			st.unset("cond");
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		st.rollAndGive(BONE_FRAGMENT, 1, 1, 10, 10);
		if(st.getQuestItemsCount(BONE_FRAGMENT) >= 10)
			st.set("cond", "2");
		st.setState(STARTED);
		return null;
	}
}
