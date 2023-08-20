package quests._240_ImTheOnlyOneYouCanTrust;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _240_ImTheOnlyOneYouCanTrust extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int KINTAIJIN = 32640;

	private static final int SpikedStakato = 22617;
	private static final int CannibalisticStakatoFollower = 22624;
	private static final int CannibalisticStakatoLeader1 = 22625;
	private static final int CannibalisticStakatoLeader2 = 22626;

	private static final int STAKATOFANGS = 14879;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public _240_ImTheOnlyOneYouCanTrust()
	{
		super(false);
		addStartNpc(KINTAIJIN);
		addKillId(SpikedStakato, CannibalisticStakatoFollower, CannibalisticStakatoLeader1, CannibalisticStakatoLeader2);
		addQuestItem(STAKATOFANGS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32640-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		if(id == COMPLETED)
			htmltext = "32640-10.htm";
		else if(id == CREATED)
		{
			if(st.getPlayer().getLevel() >= 81)
				htmltext = "32640-1.htm";
			else
			{
				htmltext = "32640-0.htm";
				st.exitCurrentQuest(true);
			}
		}
		else
		{
			if(cond == 1)
				htmltext = "32640-8.htm";
			else if(cond == 2)
			{
				st.addExpAndSp(589542, 36800);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				htmltext = "32640-9.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			st.giveItems(STAKATOFANGS, 1);
			if(st.getQuestItemsCount(STAKATOFANGS) >= 25)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}