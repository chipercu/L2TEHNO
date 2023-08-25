package quests._122_OminousNews;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _122_OminousNews extends Quest implements ScriptFile
{
	int MOIRA = 31979;
	int KARUDA = 32017;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _122_OminousNews()
	{
		super(false);
		addStartNpc(MOIRA);
		addTalkId(KARUDA);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getInt("cond");
		htmltext = event;
		if(htmltext.equalsIgnoreCase("seer_moirase_q0122_0104.htm") && cond == 0)
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(htmltext.equalsIgnoreCase("karuda_q0122_0201.htm"))
			if(cond == 1)
			{
				st.giveItems(ADENA_ID, 8923);
				st.addExpAndSp(45151, 2310); // награда соответствует Т2
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
				htmltext = "noquest";
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(npcId == MOIRA)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 20)
					htmltext = "seer_moirase_q0122_0101.htm";
				else
				{
					htmltext = "seer_moirase_q0122_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
				htmltext = "seer_moirase_q0122_0104.htm";
		}
		else if(npcId == KARUDA && cond == 1)
			htmltext = "karuda_q0122_0101.htm";
		return htmltext;
	}
}