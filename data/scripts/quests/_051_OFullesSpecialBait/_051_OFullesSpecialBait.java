package quests._051_OFullesSpecialBait;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _051_OFullesSpecialBait extends Quest implements ScriptFile
{
	int OFulle = 31572;
	int FetteredSoul = 20552;

	int LostBaitIngredient = 7622;
	int IcyAirFishingLure = 7611;

	Integer FishSkill = 1315;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _051_OFullesSpecialBait()
	{
		super(false);

		addStartNpc(OFulle);

		addTalkId(OFulle);

		addKillId(FetteredSoul);

		addQuestItem(LostBaitIngredient);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("fisher_ofulle_q0051_0104.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("fisher_ofulle_q0051_0201.htm"))
			if(st.getQuestItemsCount(LostBaitIngredient) < 100)
				htmltext = "fisher_ofulle_q0051_0202.htm";
			else
			{
				st.unset("cond");
				st.takeItems(LostBaitIngredient, -1);
				st.giveItems(IcyAirFishingLure, 4);
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
		if(npcId == OFulle)
			if(id == CREATED)
			{
				if(st.getPlayer().getLevel() < 36)
				{
					htmltext = "fisher_ofulle_q0051_0103.htm";
					st.exitCurrentQuest(true);
				}
				else if(st.getPlayer().getSkillLevel(FishSkill) >= 11)
					htmltext = "fisher_ofulle_q0051_0101.htm";
				else
				{
					htmltext = "fisher_ofulle_q0051_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1 || cond == 2)
				if(st.getQuestItemsCount(LostBaitIngredient) < 100)
				{
					htmltext = "fisher_ofulle_q0051_0106.htm";
					st.set("cond", "1");
				}
				else
					htmltext = "fisher_ofulle_q0051_0105.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == FetteredSoul && st.getInt("cond") == 1)
			if(st.getQuestItemsCount(LostBaitIngredient) < 100 && Rnd.chance(30))
			{
				st.giveItems(LostBaitIngredient, 1);
				if(st.getQuestItemsCount(LostBaitIngredient) == 100)
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
