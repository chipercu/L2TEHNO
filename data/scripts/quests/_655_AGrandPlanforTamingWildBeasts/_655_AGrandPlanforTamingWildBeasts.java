package quests._655_AGrandPlanforTamingWildBeasts;

import java.io.PrintStream;
import java.util.Calendar;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.clanhall.WildBeastFarmSiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _655_AGrandPlanforTamingWildBeasts extends Quest implements ScriptFile
{
	private static final int MESSENGER = 35627;
	private static int BUFFALO = 18871;
	private static int COUGAR = 18870;
	private static int KUKABURA = 18869;

	private static int STONE = 8084;
	private static int TSTONE = 8293;

	private static int STONE_SHANCE = 30;

	public _655_AGrandPlanforTamingWildBeasts()
	{
		super(true);
		addStartNpc(MESSENGER);
		addKillId(BUFFALO,COUGAR,KUKABURA,STONE);
	}

	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("35627-02.htm"))
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
				if(WildBeastFarmSiege.getInstance().getSiegeDate().getTimeInMillis() != 0)
					if(st.getPlayer().getClan() != null && st.getPlayer().getClan().getLevel() >= 4)
						if(WildBeastFarmSiege.getInstance().isRegistrationPeriod())
							htmltext = "35627-01.htm";
						else
							htmltext = "notime.htm";
					else
						htmltext = "noclan.htm";
			}
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(STONE) < 10)
					htmltext = "noitem.htm";
			}
			else if(cond == 2)
			{
				if(st.getQuestItemsCount(STONE) >= 10)
				{
					st.takeItems(STONE, -1);
					st.giveItems(TSTONE, 1);
					st.set("cond", "3");
					st.setState(2);
					htmltext = "35627-03.htm";
				}
				else 
					htmltext = "noitem.htm";
			}
			else if(cond == 3 && st.getQuestItemsCount(TSTONE) >= 1)
				htmltext = "35627-03.htm";
		}
		return htmltext;
	}

	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		if(cond == 1)
		{
			st.rollAndGive(STONE, 1, 1, 10, STONE_SHANCE);
			if(st.getQuestItemsCount(STONE) == 10)
			{
				st.set("cond", "2");
				st.playSound("SOUND_MIDDLE");
				st.setState(2);
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
