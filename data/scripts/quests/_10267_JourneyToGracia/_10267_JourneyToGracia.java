package quests._10267_JourneyToGracia;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _10267_JourneyToGracia extends Quest implements ScriptFile
{
	private final static int Orven = 30857;
	private final static int Keucereus = 32548;
	private final static int Papiku = 32564;

	private final static int Letter = 13810;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _10267_JourneyToGracia()
	{
		super(false);

		addStartNpc(Orven);

		addTalkId(Keucereus);
		addTalkId(Papiku);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("30857-06.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(Letter, 1);
		}
		else if(event.equalsIgnoreCase("32564-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32548-02.htm"))
		{
			st.giveItems(ADENA_ID, 92500);
			st.addExpAndSp(75480, 7570);
			st.unset("cond");
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");

		if(id == COMPLETED)
		{
			if(npcId == Keucereus)
				htmltext = "32548-03.htm";
			else if(npcId == Orven)
				htmltext = "30857-0a.htm";
		}
		else if(id == CREATED)
		{
			if(npcId == Orven)
				if(st.getPlayer().getLevel() < 75)
					htmltext = "30857-00.htm";
				else
					htmltext = "30857-01.htm";
		}
		else if(id == STARTED)
		{
			if(npcId == Orven)
				htmltext = "30857-07.htm";
			else if(npcId == Papiku)
			{
				if(cond == 1)
					htmltext = "32564-01.htm";
				else
					htmltext = "32564-03.htm";
			}
			else if(npcId == Keucereus && cond == 2)
				htmltext = "32548-01.htm";
		}
		return htmltext;
	}
}