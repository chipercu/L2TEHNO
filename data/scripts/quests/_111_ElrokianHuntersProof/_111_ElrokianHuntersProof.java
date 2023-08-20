package quests._111_ElrokianHuntersProof;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _111_ElrokianHuntersProof extends Quest implements ScriptFile
{
	// NPCs
	private static final int Marquez = 32113;
	private static final int Mushika = 32114;
	private static final int Asamah = 32115;
	private static final int Kirikachin = 32116;
	// MOBs
	private static final int mobs1[] = { 22196, 22197, 22198, 22218 };
	private static final int mobs2[] = { 22200, 22201, 22202, 22219 };
	private static final int mobs3[] = { 22208, 22209, 22210, 22221, 22226};
	private static final int mobs4[] = { 22203, 22204, 22205, 22220 };
	//ITEMs
	private static final short ElrokianTrap = 8763;
	private static final short TrapStone = 8764;
	private static final short DiaryFragment = 8768;
	private static final short ExpeditionMembersLetter = 8769;
	private static final short OrnithomimusClaw = 8770;
	private static final short DeinonychusBone = 8771;
	private static final short PachycephalosaurusSkin = 8772;
	private static final short PracticeElrokianTrap = 8773;

	//SKILLS
	private static final short KabokulaSkill = 3626;
	private static final short TapirawaSkill = 3627;
	private static final short ShabonobaSkill = 3628;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _111_ElrokianHuntersProof()
	{
		super(true);

		addStartNpc(Marquez);
		addTalkId(new int[] { Mushika, Asamah, Kirikachin });
		addKillId(mobs1);
		addKillId(mobs2);
		addKillId(mobs3);
		addKillId(mobs4);
		addQuestItem(new int[] { DiaryFragment, ExpeditionMembersLetter, OrnithomimusClaw, DeinonychusBone, PachycephalosaurusSkin, PracticeElrokianTrap });
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		if(st.getState() == CREATED && npcId == Marquez)
		{
			if(st.getPlayer().getLevel() < 75)
			{
				htmltext = "32113-0.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "32113-1.htm";
				st.playSound(SOUND_ACCEPT);
				st.setState(STARTED);
				st.set("cond", "1");
			}
		}
		else if(st.isStarted())
		{
			switch(npcId)
			{
				case Marquez:
				{
					if(cond == 1)
						htmltext = "32113-1.htm";
					else if(cond == 3)
					{
						htmltext = "32113-2.htm";
						st.playSound(SOUND_MIDDLE);
						st.set("cond", "4");
					}
					else if(cond == 4)
						htmltext = "32113-2.htm";
					else if(cond == 5)
					{
						if(st.getQuestItemsCount(DiaryFragment) >= 50)
						{
							htmltext = "32113-3.htm";
							st.giveItems(ExpeditionMembersLetter, 1);
							st.takeItems(DiaryFragment, -1);
							st.playSound(SOUND_MIDDLE);
							st.set("cond", "6");
						}
						else
						{
							st.getPlayer().sendPacket(Msg.INCORRECT_ITEM_COUNT);
							htmltext = "32113-2.htm";
							st.set("cond", "4");
						}
					}
					break;
				}
				case Mushika:
				{
					if(cond == 1)
					{
						htmltext = "32114-1.htm";
						st.playSound(SOUND_MIDDLE);
						st.set("cond", "2");
					}
					else if(cond == 2)
						htmltext = "32114-1.htm";
					break;
				}
				case Asamah:
				{
					if(cond == 2)
					{
						htmltext = "32115-1.htm";
						st.playSound(SOUND_MIDDLE);
						st.set("cond", "3");
					}
					else if(cond == 3)
						htmltext = "32115-1.htm";
					else if(cond == 8)
					{
						htmltext = "32115-2.htm";
						st.playSound(SOUND_MIDDLE);
						st.set("cond", "9");
					}
					else if(cond == 9)
					{
						htmltext = "32115-3.htm";
						st.playSound(SOUND_MIDDLE);
						st.set("cond", "10");
					}
					else if(cond == 11)
					{
						if(st.getQuestItemsCount(OrnithomimusClaw) >= 10 && st.getQuestItemsCount(PachycephalosaurusSkin) >= 10 && st.getQuestItemsCount(DeinonychusBone) >= 10)
						{
							htmltext = "32115-5.htm";
							st.giveItems(PracticeElrokianTrap, 1);
							st.takeItems(OrnithomimusClaw, -1);
							st.takeItems(PachycephalosaurusSkin, -1);
							st.takeItems(DeinonychusBone, -1);
							st.playSound(SOUND_MIDDLE);
							st.set("cond", "12");
						}
						else
						{
							st.getPlayer().sendPacket(Msg.INCORRECT_ITEM_COUNT);
							htmltext = "32115-3.htm";
							st.set("cond", "10");
						}
					}
					break;
				}
				case Kirikachin:
				{
					if(cond == 6)
					{
						htmltext = "32116-1.htm";
						st.takeItems(ExpeditionMembersLetter, -1);
						st.playSound("EtcSound.elcroki_song_full");
						st.set("cond", "8");
					}
					else if(cond == 12 && st.getQuestItemsCount(PracticeElrokianTrap) > 0)
					{
						htmltext = "32116-2.htm";
						st.giveItems(57, 1022636, false);
						st.takeItems(PracticeElrokianTrap, -1);
						st.giveItems(ElrokianTrap, 1);
						st.giveItems(TrapStone, 100);
						/** Добавление умений, вот только бы надо разобраться - работают ли эти умения :(
						st.getPlayer().addSkill(SkillTable.getInstance().getInfo(KabokulaSkill, 1), true);
						st.getPlayer().addSkill(SkillTable.getInstance().getInfo(ShabonobaSkill, 1), true);
						st.getPlayer().addSkill(SkillTable.getInstance().getInfo(TapirawaSkill, 1), true);
						*/
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(false);
					}
					break;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getPlayer().getLevel() < 75)
			return null;
		if(st.getInt("cond") == 4)
		{
			for(int i : mobs1)
				if(npc.getNpcId() == i && st.rollAndGive(DiaryFragment, 1, 1, 50, 25))
					st.set("cond", "5");
		}
		else if(st.getInt("cond") == 10)
		{
			for(int i : mobs2)
				if(npc.getNpcId() == i)
					st.rollAndGive(OrnithomimusClaw, 1, 1, 10, 66);
			for(int i : mobs3)
				if(npc.getNpcId() == i)
					st.rollAndGive(PachycephalosaurusSkin, 1, 1, 10, 50);
			for(int i : mobs4)
				if(npc.getNpcId() == i)
					st.rollAndGive(DeinonychusBone, 1, 1, 10, 33);
			if(st.getQuestItemsCount(OrnithomimusClaw) == 10 && st.getQuestItemsCount(PachycephalosaurusSkin) == 10 && st.getQuestItemsCount(DeinonychusBone) == 10)
				st.set("cond", "11");
		}
		return null;
	}
}