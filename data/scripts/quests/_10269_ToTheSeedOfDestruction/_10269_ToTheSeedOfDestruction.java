package quests._10269_ToTheSeedOfDestruction;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10269_ToTheSeedOfDestruction extends Quest implements ScriptFile
{
	private final static int Keucereus = 32548;
	private final static int Allenos = 32526;

	private final static int Introduction = 13812;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _10269_ToTheSeedOfDestruction()
	{
		super(false);

		addStartNpc(Keucereus);

		addTalkId(Allenos);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32548-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(Introduction, 1);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int npcId = npc.getNpcId();
		if(id == COMPLETED)
			if(npcId == Allenos)
				htmltext = "32526-02.htm";
			else
				htmltext = "32548-0a.htm";
		else if(id == CREATED && npcId == Keucereus)
			if(st.getPlayer().getLevel() < 75)
				htmltext = "32548-00.htm";
			else
				htmltext = "32548-01.htm";
		else if(id == STARTED && npcId == Keucereus)
			htmltext = "32548-06.htm";
		else if(id == STARTED && npcId == Allenos)
		{
			htmltext = "32526-01.htm";
			st.giveItems(ADENA_ID, 29174);
			st.addExpAndSp(176121, 17671, true);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}
}