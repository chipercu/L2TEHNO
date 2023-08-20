package quests._053_LinnaeusSpecialBait;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _053_LinnaeusSpecialBait extends Quest implements ScriptFile
{
	int Linnaeu = 31577;
	int CrimsonDrake = 20670;
	int HeartOfCrimsonDrake = 7624;
	int FlameFishingLure = 7613;
	Integer FishSkill = 1315;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _053_LinnaeusSpecialBait()
	{
		super(false);

		addStartNpc(Linnaeu);

		addTalkId(Linnaeu);

		addKillId(CrimsonDrake);

		addQuestItem(HeartOfCrimsonDrake);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("fisher_linneaus_q0053_0104.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("fisher_linneaus_q0053_0201.htm"))
			if(st.getQuestItemsCount(HeartOfCrimsonDrake) < 100)
				htmltext = "fisher_linneaus_q0053_0202.htm";
			else
			{
				st.unset("cond");
				st.takeItems(HeartOfCrimsonDrake, -1);
				st.giveItems(FlameFishingLure, 4);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		int id = st.getState();
		if(npcId == Linnaeu)
			if(id == CREATED)
			{
				if(st.getPlayer().getLevel() < 60)
				{
					htmltext = "fisher_linneaus_q0053_0103.htm";
					st.exitCurrentQuest(true);
				}
				else if(st.getPlayer().getSkillLevel(FishSkill) >= 21)
					htmltext = "fisher_linneaus_q0053_0101.htm";
				else
				{
					htmltext = "fisher_linneaus_q0053_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1 || cond == 2)
				if(st.getQuestItemsCount(HeartOfCrimsonDrake) < 100)
				{
					htmltext = "fisher_linneaus_q0053_0106.htm";
					st.set("cond", "1");
				}
				else
					htmltext = "fisher_linneaus_q0053_0105.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == CrimsonDrake && st.getInt("cond") == 1)
			if(st.getQuestItemsCount(HeartOfCrimsonDrake) < 100 && Rnd.chance(30))
			{
				st.giveItems(HeartOfCrimsonDrake, 1);
				if(st.getQuestItemsCount(HeartOfCrimsonDrake) == 100)
				{
					st.playSound(SOUND_MIDDLE);
					st.set("cond", "2");
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}