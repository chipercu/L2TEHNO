package quests._654_JourneytoaSettlement;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;
import quests._119_LastImperialPrince._119_LastImperialPrince;

public class _654_JourneytoaSettlement extends Quest implements ScriptFile
{
	// NPC
	private static final int NamelessSpirit = 31453;

	// Mobs
	private static final int CanyonAntelope = 21294;
	private static final int CanyonAntelopeSlave = 21295;

	// Items
	private static final int AntelopeSkin = 8072;

	// Rewards
	private static final int FrintezzasMagicForceFieldRemovalScroll = 8073;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _654_JourneytoaSettlement()
	{
		super(true);

		addStartNpc(NamelessSpirit);
		addKillId(CanyonAntelope, CanyonAntelopeSlave);
		addQuestItem(AntelopeSkin);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("printessa_spirit_q0654_03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("printessa_spirit_q0654_04.htm"))
			st.set("cond", "2");
		if(event.equalsIgnoreCase("printessa_spirit_q0654_07.htm"))
		{
			st.giveItems(FrintezzasMagicForceFieldRemovalScroll, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		QuestState q = st.getPlayer().getQuestState(_119_LastImperialPrince.class);
		if(q == null)
			return htmltext;
		if(st.getPlayer().getLevel() < 74)
		{
			htmltext = "printessa_spirit_q0654_02.htm";
			st.exitCurrentQuest(true);
			return htmltext;
		}
		else if(!q.isCompleted())
		{
			htmltext = "noquest";
			st.exitCurrentQuest(true);
			return htmltext;
		}

		int cond = st.getCond();
		if(npc.getNpcId() == NamelessSpirit)
		{
			if(cond == 0)
				return "printessa_spirit_q0654_01.htm";
			if(cond == 1)
				return "printessa_spirit_q0654_03.htm";
			if(cond == 3)
				return "printessa_spirit_q0654_06.htm";
		}
		else
			htmltext = "noquest";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 2 && Rnd.chance(5))
		{
			st.setCond(3);
			st.giveItems(AntelopeSkin, 1);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}