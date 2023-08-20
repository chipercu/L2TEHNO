package quests._650_ABrokenDream;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import quests._117_OceanOfDistantStar._117_OceanOfDistantStar;

public class _650_ABrokenDream extends Quest implements ScriptFile
{
	// NPC
	private static final int RailroadEngineer = 32054;
	// mobs
	private static final int ForgottenCrewman = 22027;
	private static final int VagabondOfTheRuins = 22028;
	// QuestItem
	private static final int RemnantsOfOldDwarvesDreams = 8514;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _650_ABrokenDream()
	{
		super(false);
		addStartNpc(RailroadEngineer);

		addKillId(ForgottenCrewman);
		addKillId(VagabondOfTheRuins);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "ghost_of_railroadman_q0650_0103.htm";
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
		}
		else if(event.equalsIgnoreCase("650_4"))
		{
			htmltext = "ghost_of_railroadman_q0650_0205.htm";
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			st.unset("cond");
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		String htmltext = "noquest";
		if(cond == 0)
		{
			QuestState OceanOfDistantStar = st.getPlayer().getQuestState(_117_OceanOfDistantStar.class);
			if(OceanOfDistantStar != null)
			{
				if(OceanOfDistantStar.isCompleted())
				{
					if(st.getPlayer().getLevel() < 39)
					{
						st.exitCurrentQuest(true);
						htmltext = "ghost_of_railroadman_q0650_0102.htm";
					}
					else
						htmltext = "ghost_of_railroadman_q0650_0101.htm";
				}
				else
				{
					htmltext = "ghost_of_railroadman_q0650_0104.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "ghost_of_railroadman_q0650_0104.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1)
			htmltext = "ghost_of_railroadman_q0650_0202.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		st.rollAndGive(RemnantsOfOldDwarvesDreams, 1, 1, 68);
		return null;
	}

}