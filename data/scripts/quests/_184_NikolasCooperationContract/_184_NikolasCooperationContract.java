package quests._184_NikolasCooperationContract;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _184_NikolasCooperationContract extends Quest implements ScriptFile
{
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;
	private static final int Device = 32366;
	private static final int Alarm = 32367;

	private static final int Certificate = 10362;
	private static final int Metal = 10359;
	private static final int BrokenMetal = 10360;
	private static final int NicolasMap = 10361;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _184_NikolasCooperationContract()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addTalkId(Lorain, Nikola, Device, Alarm);
		addQuestItem(NicolasMap, BrokenMetal, Metal);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		L2Player player = st.getPlayer();

		if(event.equalsIgnoreCase("30621-01.htm"))
		{
			if(player.getLevel() < 40)
				htmltext = "30621-00.htm";
		}
		else if(event.equalsIgnoreCase("30621-04.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.set("cond", "1");
			st.giveItems(NicolasMap, 1);
		}
		else if(event.equalsIgnoreCase("30673-03.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "2");
			st.takeItems(NicolasMap, -1);
		}
		else if(event.equalsIgnoreCase("30673-05.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "3");
		}
		else if(event.equalsIgnoreCase("30673-09.htm"))
		{
			if(st.getQuestItemsCount(BrokenMetal) > 0)
				htmltext = "30673-10.htm";
			else if(st.getQuestItemsCount(Metal) > 0)
				st.giveItems(Certificate, 1);
			st.giveItems(ADENA_ID, 72527, true);
			st.addExpAndSp(203717, 14032, true);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		else if(event.equalsIgnoreCase("32366-02.htm"))
		{
			L2NpcInstance alarm = st.addSpawn(Alarm, 16491, 113563, -9064);
			st.set("step", "1");
			st.playSound("ItemSound3.sys_siren");
			startQuestTimer("1", 60000, alarm, player);
			Functions.npcSayInRange(alarm, 1500, 18451);
		}
		else if(event.equalsIgnoreCase("32366-05.htm"))
		{
			st.unset("step");
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "5");
			st.giveItems(BrokenMetal, 1);
		}
		else if(event.equalsIgnoreCase("32366-06.htm"))
		{
			st.unset("step");
			st.playSound(SOUND_MIDDLE);
			st.set("cond", "4");
			st.giveItems(Metal, 1);
		}
		else if(event.equalsIgnoreCase("32367-02.htm"))
			st.set("pass", "0");
		else if(event.startsWith("correct"))
		{
			st.set("pass", str(st.getInt("pass") + 1));
			htmltext = event.substring(8);
			if(htmltext.equalsIgnoreCase("32367-07.htm"))
				if(st.getInt("pass") == 4)
				{
					st.set("step", "3");
					cancelQuestTimer("1", player);
					cancelQuestTimer("2", player);
					cancelQuestTimer("3", player);
					cancelQuestTimer("4", player);
					st.unset("pass");
					npc.deleteMe();
				}
				else
					htmltext = "32367-06.htm";
		}
		else if(event.equalsIgnoreCase("1"))
		{
			Functions.npcSayInRange(npc, 1500, 18452);
			startQuestTimer("2", 30000, npc, player);
			return null;
		}
		else if(event.equalsIgnoreCase("2"))
		{
			Functions.npcSayInRange(npc, 1500, 18453);
			startQuestTimer("3", 20000, npc, player);
			return null;
		}
		else if(event.equalsIgnoreCase("3"))
		{
			Functions.npcSayInRange(npc, 1500, 18454);
			startQuestTimer("4", 10000, npc, player);
			return null;
		}
		else if(event.equalsIgnoreCase("4"))
		{
			Functions.npcSayInRange(npc, 1500, 18455);
			npc.deleteMe();
			st.set("step", "2");
			return null;
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");

		if(st.isStarted())
			if(npcId == Nikola)
			{
				if(cond == 0)
					if(st.getPlayer().getLevel() < 40)
						htmltext = "30621-00.htm";
					else
						htmltext = "30621-01.htm";
				else if(cond == 1)
					htmltext = "30621-05.htm";
			}
			else if(npcId == Lorain)
			{
				if(cond == 1)
					htmltext = "30673-01.htm";
				else if(cond == 2)
					htmltext = "30673-04.htm";
				else if(cond == 3)
					htmltext = "30673-06.htm";
				else if(cond == 4 || cond == 5)
					htmltext = "30673-07.htm";
			}
			else if(npcId == Device)
			{
				int step = st.getInt("step");
				if(cond == 3)
					if(step == 0)
						htmltext = "32366-01.htm";
					else if(step == 1)
						htmltext = "32366-02.htm";
					else if(step == 2)
						htmltext = "32366-04.htm";
					else if(step == 3)
						htmltext = "32366-03.htm";
			}
			else if(npcId == Alarm)
				htmltext = "32367-01.htm";

		return htmltext;
	}
}