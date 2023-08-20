package quests._641_AttackSailren;

import quests._126_IntheNameofEvilPart2._126_IntheNameofEvilPart2;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _641_AttackSailren extends Quest implements ScriptFile
{
	//NPC
	private static int STATUE = 32109;

	//MOBS
	private static int VEL1 = 22196;
	private static int VEL2 = 22197;
	private static int VEL3 = 22198;
	private static int VEL4 = 22218;
	private static int VEL5 = 22223;
	private static int PTE = 22199;
	//items
	private static int FRAGMENTS = 8782;
	private static int GAZKH = 8784;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _641_AttackSailren()
	{
		super(true);

		addStartNpc(STATUE);

		addKillId(VEL1);
		addKillId(VEL2);
		addKillId(VEL3);
		addKillId(VEL4);
		addKillId(VEL5);
		addKillId(PTE);

		addQuestItem(FRAGMENTS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("statue_of_shilen_q0641_05.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("statue_of_shilen_q0641_08.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(FRAGMENTS, -1);
			st.giveItems(GAZKH, 1);
			st.exitCurrentQuest(true);
			st.unset("cond");
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
			QuestState qs = st.getPlayer().getQuestState(_126_IntheNameofEvilPart2.class);
			if(qs == null || !qs.isCompleted())
				htmltext = "statue_of_shilen_q0641_02.htm";
			else if(st.getPlayer().getLevel() >= 77)
				htmltext = "statue_of_shilen_q0641_01.htm";
			else
				st.exitCurrentQuest(true);
		}
		else if(cond == 1)
			htmltext = "statue_of_shilen_q0641_05.htm";
		else if(cond == 2)
			htmltext = "statue_of_shilen_q0641_07.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(FRAGMENTS) < 30)
		{
			st.giveItems(FRAGMENTS, 1);
			if(st.getQuestItemsCount(FRAGMENTS) == 30)
			{
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "2");
				st.setState(STARTED);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}