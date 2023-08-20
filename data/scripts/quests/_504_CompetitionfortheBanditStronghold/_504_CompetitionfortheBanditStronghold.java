package quests._504_CompetitionfortheBanditStronghold;

import java.io.PrintStream;
import java.util.Calendar;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.clanhall.BanditStrongholdSiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _504_CompetitionfortheBanditStronghold extends Quest implements ScriptFile
{
	private static final int MESSENGER = 35437;
	private static int TARLK_BUGBEAR = 20570;
	private static int TARLK_BASILISK = 20573;
	private static int ELDER_TARLK_BASILISK = 20574;

	private static int AMULET = 4332;
	private static int QREWARD = 5009;

	private static int AMULET_SHANCE = 10;

	public _504_CompetitionfortheBanditStronghold()
	{
		super(PARTY_ALL);

		addStartNpc(MESSENGER);
		addKillId(TARLK_BUGBEAR, TARLK_BASILISK, ELDER_TARLK_BASILISK);
	}

	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("35437-02.htm"))
		{
			st.set("cond", "1");
			st.setState(2);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(npcId == MESSENGER)
		{
			if(cond == 0)
			{
				if(BanditStrongholdSiege.getInstance().getSiegeDate().getTimeInMillis() != 0)
					if(st.getPlayer().getClan() != null && st.getPlayer().getClan().getLevel() >= 4)
						if(BanditStrongholdSiege.getInstance().isRegistrationPeriod())
							htmltext = "35437-01.htm";
						else
							htmltext = "notime.htm";
					else
						htmltext = "noclan.htm";
			}
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(AMULET) < 30)
					htmltext = "noitem.htm";
			}
			else if(cond == 2)
			{
				if(st.getQuestItemsCount(AMULET) >= 30)
				{
					st.takeItems(AMULET, -1);
					st.giveItems(QREWARD, 1);
					st.set("cond", "3");
					//st.setState(2);
					htmltext = "35437-03.htm";
				}
				else
					htmltext = "noitem.htm";
			}
			else if(cond == 3 && st.getQuestItemsCount(QREWARD) >= 1)
				htmltext = "35437-03.htm";
		}
		return htmltext;
	}

	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		if(cond == 1)
		{
			st.rollAndGive(AMULET, 1, 1, 30, AMULET_SHANCE);
			if(st.getQuestItemsCount(AMULET) == 30)
			{
				st.set("cond", "2");
				st.playSound(SOUND_MIDDLE);
				//st.setState(2);
			}
		}
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}
