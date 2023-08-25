package quests._052_WilliesSpecialBait;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _052_WilliesSpecialBait extends Quest implements ScriptFile
{
	private final static int Willie = 31574;
	private final static int[] TarlkBasilisks = { 20573, 20574 };
	private final static int EyeOfTarlkBasilisk = 7623;
	private final static int EarthFishingLure = 7612;
	private final static Integer FishSkill = 1315;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _052_WilliesSpecialBait()
	{
		super(false);

		addStartNpc(Willie);

		addKillId(TarlkBasilisks);

		addQuestItem(EyeOfTarlkBasilisk);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("fisher_willeri_q0052_0104.htm"))
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("fisher_willeri_q0052_0201.htm"))
			if(st.getQuestItemsCount(EyeOfTarlkBasilisk) < 100)
				htmltext = "fisher_willeri_q0052_0202.htm";
			else
			{
				st.unset("cond");
				st.takeItems(EyeOfTarlkBasilisk, -1);
				st.giveItems(EarthFishingLure, 4);
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
		if(npcId == Willie)
			if(id == CREATED)
			{
				if(st.getPlayer().getLevel() < 48)
				{
					htmltext = "fisher_willeri_q0052_0103.htm";
					st.exitCurrentQuest(true);
				}
				else if(st.getPlayer().getSkillLevel(FishSkill) >= 16)
					htmltext = "fisher_willeri_q0052_0101.htm";
				else
				{
					htmltext = "fisher_willeri_q0052_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1 || cond == 2)
				if(st.getQuestItemsCount(EyeOfTarlkBasilisk) < 100)
				{
					htmltext = "fisher_willeri_q0052_0106.htm";
					st.set("cond", "1");
				}
				else
					htmltext = "fisher_willeri_q0052_0105.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == TarlkBasilisks[0] || npcId == TarlkBasilisks[1] && st.getInt("cond") == 1)
			if(st.getQuestItemsCount(EyeOfTarlkBasilisk) < 100 && Rnd.chance(30))
			{
				st.giveItems(EyeOfTarlkBasilisk, 1);
				if(st.getQuestItemsCount(EyeOfTarlkBasilisk) == 100)
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