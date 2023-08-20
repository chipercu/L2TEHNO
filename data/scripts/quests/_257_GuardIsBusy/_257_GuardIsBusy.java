package quests._257_GuardIsBusy;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import quests._255_Tutorial._255_Tutorial;

/**
 * Квест проверен и работает.
 * Рейты прописаны путем повышения шанса получения квестовых вещей.
 */
public class _257_GuardIsBusy extends Quest implements ScriptFile
{
	int GLUDIO_LORDS_MARK = 1084;
	int ORC_AMULET = 752;
	int ORC_NECKLACE = 1085;
	int WEREWOLF_FANG = 1086;
	int ADENA = 57;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _257_GuardIsBusy()
	{
		super(false);

		addStartNpc(30039);
		addKillId(20130, 20131, 20132, 20342, 20343, 20006, 20093, 20096, 20098);
		addQuestItem(ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG, GLUDIO_LORDS_MARK);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gilbert_q0257_03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.takeItems(GLUDIO_LORDS_MARK, -1);
			st.giveItems(GLUDIO_LORDS_MARK, 1);
		}
		else if(event.equalsIgnoreCase("257_2"))
		{
			htmltext = "gilbert_q0257_05.htm";
			st.takeItems(GLUDIO_LORDS_MARK, -1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if(event.equalsIgnoreCase("257_3"))
			htmltext = "gilbert_q0257_06.htm";
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");

		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 6)
			{
				htmltext = "gilbert_q0257_02.htm";
				return htmltext;
			}
			htmltext = "gilbert_q0257_01.htm";
			st.exitCurrentQuest(true);
		}
		else if(cond == 1 && st.getQuestItemsCount(ORC_AMULET) < 1 && st.getQuestItemsCount(ORC_NECKLACE) < 1 && st.getQuestItemsCount(WEREWOLF_FANG) < 1)
			htmltext = "gilbert_q0257_04.htm";
		else if(cond == 1 && (st.getQuestItemsCount(ORC_AMULET) > 0 || st.getQuestItemsCount(ORC_NECKLACE) > 0 || st.getQuestItemsCount(WEREWOLF_FANG) > 0))
		{
			st.giveItems(ADENA, 12 * st.getQuestItemsCount(ORC_AMULET) + 20 * st.getQuestItemsCount(ORC_NECKLACE) + 25 * st.getQuestItemsCount(WEREWOLF_FANG), false);

			if(st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q2"))
			{
				st.getPlayer().setVar("p1q2", "1");
				st.getPlayer().sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				QuestState qs = st.getPlayer().getQuestState(_255_Tutorial.class);
				if(qs != null && qs.getInt("Ex") != 10)
				{
					st.showQuestionMark(26);
					qs.set("Ex", "10");
					if(st.getPlayer().getClassId().isMage())
					{
						st.playTutorialVoice("tutorial_voice_027");
						st.giveItems(5790, 3000);
					}
					else
					{
						st.playTutorialVoice("tutorial_voice_026");
						st.giveItems(5789, 6000);
					}
				}
			}

			st.takeItems(ORC_AMULET, -1);
			st.takeItems(ORC_NECKLACE, -1);
			st.takeItems(WEREWOLF_FANG, -1);
			htmltext = "gilbert_q0257_07.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getQuestItemsCount(GLUDIO_LORDS_MARK) > 0 && st.getInt("cond") > 0)
			if(npcId == 20130 || npcId == 20131 || npcId == 20006)
				st.rollAndGive(ORC_AMULET, 1, 50);
			else if(npcId == 20093 || npcId == 20096 || npcId == 20098)
				st.rollAndGive(ORC_NECKLACE, 1, 50);
			else if(npcId == 20132)
				st.rollAndGive(WEREWOLF_FANG, 1, 33);
			else if(npcId == 20343)
				st.rollAndGive(WEREWOLF_FANG, 1, 50);
			else if(npcId == 20342)
				st.rollAndGive(WEREWOLF_FANG, 1, 75);
		return null;
	}
}