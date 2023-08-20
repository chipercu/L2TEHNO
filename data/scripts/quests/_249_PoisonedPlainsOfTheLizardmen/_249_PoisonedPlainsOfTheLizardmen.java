package quests._249_PoisonedPlainsOfTheLizardmen;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _249_PoisonedPlainsOfTheLizardmen extends Quest implements ScriptFile
{
	private static final int MOUEN = 30196;
	private static final int JHONNY = 32744;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
	
	public _249_PoisonedPlainsOfTheLizardmen()
	{
		super(false);		
		addStartNpc(MOUEN);
		addTalkId(JHONNY);
	}
	
	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;	
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		
		if(npcId == MOUEN)
		{
			if (event.equalsIgnoreCase("30196-03.htm"))
			{
				st.setState(STARTED);
				st.set("cond", "1");
				st.playSound(SOUND_ACCEPT);
			}
		}
		if(npcId == JHONNY)
		{
			if (event.equalsIgnoreCase("32744-03.htm"))
			{
				st.unset("cond");
				st.giveItems(57, 83056);
				st.addExpAndSp(477496, 58743);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		}	
		return htmltext;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");
		
		if(id == COMPLETED)
			if(npcId == MOUEN)
				htmltext = "30196-05.htm";
			if(npcId == JHONNY)
				htmltext = "32744-04.htm";
		if(id == CREATED)
			if (st.getPlayer().getLevel() >= 82)
				htmltext = "30196-01.htm";
			else
			{
				htmltext = "30196-00.htm";
				st.exitCurrentQuest(true);
			}
		if(id == STARTED)
			if(npcId == MOUEN)
				if (cond == 1)
					htmltext = "30196-04.htm";
			if(npcId == JHONNY)
				if (cond == 1)
					htmltext = "32744-01.htm";
		return htmltext;
	}
}
