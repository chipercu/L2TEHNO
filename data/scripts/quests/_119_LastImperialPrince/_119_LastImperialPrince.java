package quests._119_LastImperialPrince;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * Квест 119 (Last Imperial Prince (Последний Принц Империи))
 *
 * @author Ozzy
 */
public class _119_LastImperialPrince extends Quest implements ScriptFile
{
	//NPC's
	private static final int SPIRIT = 31453;
	private static final int DEVORIN = 32009;

	//Items
	private static final int BROOCH = 7262;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _119_LastImperialPrince()
	{
		super(false);
		addStartNpc(SPIRIT);
		addTalkId(DEVORIN);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31453-06.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32009-02.htm"))
		{
			if(st.getQuestItemsCount(BROOCH) < 1)
			{
				htmltext = "32009-02a.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(event.equalsIgnoreCase("32009-03.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("31453-10.htm"))
		{
			st.giveItems(ADENA_ID, 150292, true);
			st.addExpAndSp(902439, 90067);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");

		if(npcId == SPIRIT)
		{
			if(st.getState() == CREATED)
			{
				if(st.getPlayer().getLevel() >= 74 && st.getQuestItemsCount(BROOCH) >= 1)
					htmltext = "31453-01.htm";
				else
				{
					htmltext = "31453-02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(BROOCH) >= 1)
					htmltext = "31453-07.htm";
				else
				{
					htmltext = "31453-07a.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 2)
				htmltext = "31453-08.htm";
			/*else if(st.getState() == COMPLETED)
				htmltext = "31453-03.htm"; //TODO: Придумать что-то с выводом HTML при стейте COMPLETED. Сейчас стоит вывод сообщения по умолчанию. */
		}
		else if(npcId == DEVORIN)
		{
			if(cond == 1)
				htmltext = "32009-01.htm";
			else if (cond == 2)
				htmltext = "32009-04.htm";
		}
		return htmltext;
	}
}