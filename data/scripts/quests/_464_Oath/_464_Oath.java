package quests._464_Oath;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

/**
 * Quest for Monastery of Silence
 * @author Drizzy
 * @date 06.07.2011
 * @time 22:31:49
 */
public class _464_Oath extends Quest implements ScriptFile
{
	//Npc
	private static final int Sophia = 32596;
	private static final int Seresin = 30657;
	private static final int Holly = 30839;
	private static final int Floen = 30899;
	private static final int Dominik = 31350;
	private static final int Chicherin = 30539;
	private static final int Tobias = 30297;
	private static final int Byron = 31960;
	private static final int Agnes = 31588;

	//Massive
	private static final int[] NPC = { 30657, 30839, 30899, 31350, 30539, 30297, 31960, 31588 };
	private static final int[] QuestCond = { 2, 3, 4, 5, 6, 7, 8, 9 };

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _464_Oath()
	{
		super(false);
		addTalkId(Sophia, Seresin, Holly, Floen, Dominik, Chicherin, Tobias, Byron, Agnes);
		addQuestItem(15538, 15539);
	}

	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("item-1.htm"))
		{
			if(!st.isNowAvailable())
				return "nextday.htm";
			else if(st.getPlayer().getLevel() < 82)
				return "lvl.htm";
			else
			{
				st.playSound(SOUND_ACCEPT);
				st.setState(STARTED);
				st.set("cond", "1");
				st.takeItems(15537, 1);
				st.giveItems(15538, 1);
				return "item-1.htm";
			}
		}
		else if(event.equalsIgnoreCase("whoGiveItem"))
		{
			int select = Rnd.get(0, 7);
			st.setCond(QuestCond[select]);
			st.takeItems(15538, -1);
			st.giveItems(15539, 1);
			addTalkId(NPC[st.getInt("select")]);
			st.set("select", select);
			return "director_sophia_q0464_04-" + st.getInt("select") + ".htm";
		}
		else if(event.equalsIgnoreCase("reward"))
		{
			int npcId = NPC[st.getInt("select")];
			if(npcId == Seresin)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(15449, 17696);
				st.giveItems(57, 42910);
				SetQuestReuse(st);
				return "npc_talk_q0464_00-1.htm";
			}
			if(npcId == Holly)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(189377, 21692);
				st.giveItems(57, 52599);
				SetQuestReuse(st);
				return "npc_talk_q0464_01-1.htm";
			}
			if(npcId == Floen)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(249180, 28542);
				st.giveItems(57, 69210);
				SetQuestReuse(st);
				return "npc_talk_q0464_02-1.htm";
			}
			if(npcId == Dominik)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(249180, 28542);
				st.giveItems(57, 69210);
				SetQuestReuse(st);
				return "npc_talk_q0464_03-1.htm";
			}
			if(npcId == Chicherin)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(19408, 47062);
				st.giveItems(57, 169442);
				SetQuestReuse(st);
				return "npc_talk_q0464_04-1.htm";
			}
			if(npcId == Tobias)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(24146, 58551);
				st.giveItems(57, 210806);
				SetQuestReuse(st);
				return "npc_talk_q0464_05-1.htm";
			}
			if(npcId == Byron)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(15449, 17696);
				st.giveItems(57, 42910);
				SetQuestReuse(st);
				return "npc_talk_q0464_06-1.htm";
			}
			if(npcId == Agnes)
			{
				st.takeItems(15539, -1);
				st.addExpAndSp(15449, 17696);
				st.giveItems(57, 42910);
				SetQuestReuse(st);
				return "npc_talk_q0464_07-1.htm";
			}
		}
		return event;
	}

	private void SetQuestReuse(QuestState st)
	{
		st.playSound(SOUND_FINISH);
		st.exitCurrentQuest(this);
	}

	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(npcId == Sophia)
		{
			if(cond == 1)
			{
				return "director_sophia_q0464_01.htm";
			}
			if(cond == 2 || cond == 3 || cond == 4 || cond == 5 || cond == 6 || cond == 7 || cond == 8 || cond == 9)
			{
				return "director_sophia_q0464_05-" + st.getInt("select") + ".htm";
			}
		}
		if(npcId == NPC[st.getInt("select")])
		{
			if(cond == QuestCond[st.getInt("select")])
			{
				return "npc_talk_q0464_0" + st.getInt("select") + "-0.htm";
			}
		}
		return "noquest";
	}
}