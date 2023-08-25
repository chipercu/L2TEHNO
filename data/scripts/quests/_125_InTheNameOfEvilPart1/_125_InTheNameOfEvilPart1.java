package quests._125_InTheNameOfEvilPart1;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Files;

public class _125_InTheNameOfEvilPart1 extends Quest implements ScriptFile
{
	//NPCs
	private static final int Mushika = 32114;
	private static final int Karakawei = 32117;
	private static final int UluKaimu = 32119;
	private static final int BaluKaimu = 32120;
	private static final int ChutaKaimu = 32121;
	private static final int mobs1[] = { 22200, 22201, 22202, 22219, 22224 }; // Ornithomimus
	private static final int mobs2[] = { 22203, 22204, 22205, 22220, 22225 }; // Deinonychus
	// ITEMs
	private static final short OrClaw = 8779;
	private static final short DienBone = 8780;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _125_InTheNameOfEvilPart1()
	{
		super(false);

		addStartNpc(Mushika);
		addTalkId(new int[] { Karakawei, UluKaimu, BaluKaimu, ChutaKaimu });
		addKillId(mobs1);
		addKillId(mobs2);
		addQuestItem(new int[] { OrClaw, DienBone });
	}

	private String showField(QuestState st, int cond)
	{
		String htmltext = "";
		if(cond == 5)
			htmltext = Files.read("data/scripts/quests/" + getName() + "/32119-02.htm", st.getPlayer().getVar("lang@"));
		else if(cond == 6)
			htmltext = Files.read("data/scripts/quests/" + getName() + "/32120-02.htm", st.getPlayer().getVar("lang@"));
		else if(cond == 7)
			htmltext = Files.read("data/scripts/quests/" + getName() + "/32121-02.htm", st.getPlayer().getVar("lang@"));
		htmltext = htmltext.replace("%word%", st.get("word").toString());
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32114-02.htm"))
		{
			st.set("word", "");
			st.set("id", "0");
			st.set("cond", "2");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32117-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32117-04.htm"))
		{
			if(st.getQuestItemsCount(OrClaw) >= 2 && st.getQuestItemsCount(DienBone) >= 2)
			{
				st.takeItems(OrClaw, -1);
				st.takeItems(DienBone, -1);
				st.set("id", "32117");
			}
			else
			{
				htmltext = "32117-02.htm";
				st.set("cond", "3");
			}
		}
		else if(event.equalsIgnoreCase("32117-06.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32119-10.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32120-06.htm"))
		{
			st.set("cond", "7");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32121-08.htm"))
		{
			st.set("cond", "8");
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("showField"))
			htmltext = showField(st, st.getInt("cond"));
		else if(event.length() == 1)
		{
			int cond = st.getInt("cond");
			st.set("word", st.get("word") + event);
			int len = st.get("word").toString().length();
			if(len == 4 && cond > 4 && cond < 8)
			{
				if(cond == 5 && st.get("word") != null && st.get("word").toString().equalsIgnoreCase("tepu"))
					htmltext = Files.read("data/scripts/quests/" + getName() + "/32119-03.htm", st.getPlayer().getVar("lang@"));
				else if(cond == 6 && st.get("word") != null && st.get("word").toString().equalsIgnoreCase("toon"))
					htmltext = Files.read("data/scripts/quests/" + getName() + "/32120-03.htm", st.getPlayer().getVar("lang@"));
				else if(cond == 7 && st.get("word") != null && st.get("word").toString().equalsIgnoreCase("wagu"))
					htmltext = Files.read("data/scripts/quests/" + getName() + "/32121-03.htm", st.getPlayer().getVar("lang@"));
				else
					htmltext = showField(st, cond);
				st.set("word", "");
			}
			else
				htmltext = showField(st, cond);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(st.getState() == CREATED && npcId == Mushika)
		{
			QuestState qs124 = st.getPlayer().getQuestState("_124_MeetingTheElroki");
			if(qs124 == null || qs124.getState() != COMPLETED || st.getPlayer().getLevel() < 76)
			{
				htmltext = "32114-00.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "32114-01.htm";
				st.setState(STARTED);
				st.set("cond", "1");
			}
		}
		else if(st.getState() == STARTED)
		{
			switch(npcId)
			{
				case Mushika:
				{
					if(cond == 1)
						htmltext = "32114-01.htm";
					if(cond == 2)
						htmltext = "32114-02.htm";
					else if(cond == 8)
					{
						htmltext = "32114-03.htm";
						st.addExpAndSp(859195, 86603);
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(false);
					}
					break;
				}
				case Karakawei:
				{
					if(cond == 2)
						htmltext = "32117-01.htm";
					else if(cond == 3)
						htmltext = "32117-02.htm";
					else if(cond == 4)
					{
						if(st.getQuestItemsCount(OrClaw) >= 2 && st.getQuestItemsCount(DienBone) >= 2)
							htmltext = "32117-03.htm";
						else if(st.getInt("id") == 32117)
							htmltext = "32117-04.htm";
					}
					else if(cond == 5)
						htmltext = "32117-06a.htm";
					break;
				}
				case UluKaimu:
				{
					if(cond == 5)
						htmltext = "32119-01.htm";
					else if(cond == 6)
						htmltext = "32119-10.htm";
					break;
				}
				case BaluKaimu:
				{
					if(cond == 6)
						htmltext = "32120-01.htm";
					else if(cond == 7)
						htmltext = "32120-06.htm";
					break;
				}
				case ChutaKaimu:
				{
					if(cond == 7)
						htmltext = "32121-01.htm";
					else if(cond == 8)
						htmltext = "32121-08a.htm";
					break;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(cond == 3)
		{
			for(int id : mobs1)
			{
				if(id == npcId)
					st.rollAndGive(OrClaw, 1, 1, 2, 10);
			}
			for(int id : mobs2)
			{
				if(id == npcId)
					st.rollAndGive(DienBone, 1, 1, 2, 10);
			}
			if(st.getQuestItemsCount(OrClaw) >= 2 && st.getQuestItemsCount(DienBone) >= 2)
			{
				st.set("cond", "4");
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}