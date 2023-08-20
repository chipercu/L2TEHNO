package quests._151_CureforFeverDisease;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.util.Rnd;

public class _151_CureforFeverDisease extends Quest implements ScriptFile
{
	int POISON_SAC = 703;
	int FEVER_MEDICINE = 704;
	int ROUND_SHIELD = 102;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _151_CureforFeverDisease()
	{
		super(false);

		addStartNpc(30050);

		addTalkId(30032);

		addKillId(20103, 20106, 20108);

		addQuestItem(FEVER_MEDICINE, POISON_SAC);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			htmltext = "elias_q0151_03.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if(id != CREATED)
			cond = st.getInt("cond");
		if(npcId == 30050)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 15)
					htmltext = "elias_q0151_02.htm";
				else
				{
					htmltext = "elias_q0151_01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1 && st.getQuestItemsCount(POISON_SAC) == 0 && st.getQuestItemsCount(FEVER_MEDICINE) == 0)
				htmltext = "elias_q0151_04.htm";
			else if(cond == 1 && st.getQuestItemsCount(POISON_SAC) == 1)
				htmltext = "elias_q0151_05.htm";
			else if(cond == 3 && st.getQuestItemsCount(FEVER_MEDICINE) == 1)
			{
				st.takeItems(FEVER_MEDICINE, -1);

				st.giveItems(ROUND_SHIELD, 1);
				st.getPlayer().addExpAndSp(13106, 613, false, false);

				if(st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q4"))
				{
					st.getPlayer().setVar("p1q4", "1");
					st.getPlayer().sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				}

				htmltext = "elias_q0151_06.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		}
		else if(npcId == 30032)
			if(cond == 2 && st.getQuestItemsCount(POISON_SAC) > 0)
			{
				st.giveItems(FEVER_MEDICINE, 1);
				st.takeItems(POISON_SAC, -1);
				st.set("cond", "3");
				htmltext = "yohan_q0151_01.htm";
			}
			else if(cond == 3 && st.getQuestItemsCount(FEVER_MEDICINE) > 0)
				htmltext = "yohan_q0151_02.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if((npcId == 20103 || npcId == 20106 || npcId == 20108) && st.getQuestItemsCount(POISON_SAC) == 0 && st.getInt("cond") == 1 && Rnd.chance(50))
		{
			st.set("cond", "2");
			st.giveItems(POISON_SAC, 1);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}