package quests._191_VainConclusion;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._188_SealRemoval._188_SealRemoval;

public class _191_VainConclusion extends Quest implements ScriptFile
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Dorothy = 30970;
	private static final int Shegfield = 30068;

	private static final int Metal = 10371;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _191_VainConclusion()
	{
		super(false);

		addTalkId(Kusto, Dorothy, Lorain, Shegfield);
		addFirstTalkId(Dorothy);
		addQuestItem(Metal);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30970-03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
			st.giveItems(Metal, 1);
		}
		else if(event.equalsIgnoreCase("30673-02.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "2");
			st.takeItems(Metal, -1);
		}
		else if(event.equalsIgnoreCase("30068-03.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("30512-02.htm"))
		{
			st.giveItems(ADENA_ID, 117327, true);
			st.addExpAndSp(309467, 20614, true);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(st.getState() == STARTED)
		{
			if(npcId == Dorothy)
			{
				if(cond == 0)
					if(st.getPlayer().getLevel() < 42)
						htmltext = "30970-00.htm";
					else
						htmltext = "30970-01.htm";
				else if(cond == 1)
					htmltext = "30970-04.htm";
			}
			else if(npcId == Lorain)
			{
				if(cond == 1)
					htmltext = "30673-01.htm";
				else if(cond == 2)
					htmltext = "30673-03.htm";
				else if(cond == 3)
				{
					htmltext = "30673-04.htm";
					st.set("cond", "4");
					st.playSound(SOUND_MIDDLE);
				}
				else if(cond == 4)
					htmltext = "30673-05.htm";
			}
			else if(npcId == Shegfield)
			{
				if(cond == 2)
					htmltext = "30068-01.htm";
				else if(cond == 3)
					htmltext = "30068-04.htm";
			}
			else if(npcId == Kusto)
			{
				if(cond == 4)
					htmltext = "30512-01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(_188_SealRemoval.class);
		if(qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
			newQuestState(player, STARTED);
		return "";
	}
}