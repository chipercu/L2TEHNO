package quests._194_SevenSignContractOfMammon;

import quests._193_SevenSignDyingMessage._193_SevenSignDyingMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.tables.SkillTable;

public class _194_SevenSignContractOfMammon extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	// NPCs
	private static int SirGustavAthebaldt = 30760;
	private static int SecretAgentColin = 32571;
	private static int FrogKing = 32572;
	private static int GrandmaTess = 32573;
	private static int VillagerKuta = 32574;
	private static int ClaudiaAthebaldt = 31001;

	// ITEMS
	private static int AthebaltsIntroduction = 13818;
	private static int NativesGlove = 13819;
	private static int FrogKingsBead = 13820;
	private static int GrandmaTessCandyPouch = 13821;

	// Transform skills
	private static int SkillFrog = 6201;
	private static int SkillChild = 6202;
	private static int SkillNative = 6203;

	// Transform ids
	private static int TransformFrog = 111;
	private static int TransformChild = 112;
	private static int TransformNative = 124;

	public _194_SevenSignContractOfMammon()
	{
		super(false);

		addStartNpc(SirGustavAthebaldt);
		addTalkId(VillagerKuta, GrandmaTess, FrogKing, SecretAgentColin, ClaudiaAthebaldt);
		addQuestItem(AthebaltsIntroduction, NativesGlove, FrogKingsBead, GrandmaTessCandyPouch);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;

		if(event.equalsIgnoreCase("30760-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30760-05.htm"))
		{
			st.set("cond", "2");
			st.playSound(SOUND_MIDDLE);
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_CONTRACT_OF_MAMMON);
			return "";
		}
		else if(event.equalsIgnoreCase("30760-08.htm"))
		{
			st.set("cond", "3");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(AthebaltsIntroduction, 1);
		}
		else if(event.equalsIgnoreCase("32571-04.htm"))
		{
			st.set("cond", "4");
			st.playSound(SOUND_MIDDLE);
			st.takeItems(AthebaltsIntroduction, 1);
			if(player.getTransformation() != 0)
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			if(player.isMounted()) // Если чар на страйдере и тд, снимаем чара с него.
				player.setMount(0, 0, 0);
			SkillTable.getInstance().getInfo(SkillFrog, 1).getEffects(player, player, false, false);
		}
		else if(event.equalsIgnoreCase("32572-04.htm"))
		{
			st.set("cond", "5");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(FrogKingsBead, 1);
		}
		else if(event.equalsIgnoreCase("32571-06.htm"))
		{
			st.set("cond", "6");
			st.playSound(SOUND_MIDDLE);
			st.takeItems(FrogKingsBead, 1);
		}
		else if(event.equalsIgnoreCase("32571-08.htm"))
		{
			st.set("cond", "7");
			st.playSound(SOUND_MIDDLE);
			if(player.getTransformation() != 0)
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			if(player.isMounted()) // Если чар на страйдере и тд, снимаем чара с него.
				player.setMount(0, 0, 0);
			SkillTable.getInstance().getInfo(SkillChild, 1).getEffects(player, player, false, false);
		}
		else if(event.equalsIgnoreCase("32573-03.htm"))
		{
			st.set("cond", "8");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(GrandmaTessCandyPouch, 1);
		}
		else if(event.equalsIgnoreCase("32571-10.htm"))
		{
			st.set("cond", "9");
			st.playSound(SOUND_MIDDLE);
			st.takeItems(GrandmaTessCandyPouch, 1);
		}
		else if(event.equalsIgnoreCase("32571-12.htm"))
		{
			st.set("cond", "10");
			st.playSound(SOUND_MIDDLE);
			if(player.getTransformation() != 0)
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			if(player.isMounted()) // Если чар на страйдере и тд, снимаем чара с него.
				player.setMount(0, 0, 0);
			SkillTable.getInstance().getInfo(SkillNative, 1).getEffects(player, player, false, false);
		}
		else if(event.equalsIgnoreCase("32574-04.htm"))
		{
			st.set("cond", "11");
			st.playSound(SOUND_MIDDLE);
			st.giveItems(NativesGlove, 1);
		}
		else if(event.equalsIgnoreCase("32571-14.htm"))
		{
			st.set("cond", "12");
			st.playSound(SOUND_MIDDLE);
			st.takeItems(NativesGlove, 1);
		}
		else if(event.equalsIgnoreCase("31001-03.htm"))
		{
			if(!player.isSubClassActive())
			{
				st.addExpAndSp(52518015, 5817677);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
			else
				htmltext = "<html><body>Only characters who are <font color=\"LEVEL\">main class</font>.</body></html>";
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int id = st.getState();
		L2Player player = st.getPlayer();
		if(npcId == SirGustavAthebaldt)
		{
			if(id == CREATED)
			{
				if(player.getLevel() < 79 && player.isSubClassActive())
				{
					st.exitCurrentQuest(true);
					return "30760-00.htm";
				}
				QuestState qs = player.getQuestState(_193_SevenSignDyingMessage.class);
				if(qs == null || !qs.isCompleted())
				{
					st.exitCurrentQuest(true);
					return "noquest";
				}
				return "30760-01.htm";
			}
			else if(cond == 1)
				return "30760-02.htm";
			else if(cond == 2)
				return "30760-06.htm";
			else if(cond == 3)
				return "30760-08a.htm";
		}
		else if(npcId == SecretAgentColin)
		{
			if(cond == 3)
				return "32571-01.htm";
			else if(cond == 4)
			{
				if(player.getTransformation() == 0)
				{
					if(player.isMounted()) // Если чар на страйдере и тд, снимаем чара с него.
						player.setMount(0, 0, 0);
					SkillTable.getInstance().getInfo(SkillFrog, 1).getEffects(player, player, false, false);
				}
				return "32571-04.htm";
			}
			else if(cond == 5)
				return "32571-05.htm";
			else if(cond == 6)
				return "32571-07.htm";
			else if(cond == 7)
			{
				if(player.getTransformation() == 0)
				{
					if(player.isMounted()) // Если чар на страйдере и тд, снимаем чара с него.
						player.setMount(0, 0, 0);
					SkillTable.getInstance().getInfo(SkillChild, 1).getEffects(player, player, false, false);
				}
				return "32571-08.htm";
			}
			else if(cond == 8)
				return "32571-09.htm";
			else if(cond == 9)
				return "32571-11.htm";
			else if(cond == 10)
			{
				if(player.getTransformation() == 0)
				{
					if(player.isMounted()) // Если чар на страйдере и тд, снимаем чара с него.
						player.setMount(0, 0, 0);
					SkillTable.getInstance().getInfo(SkillNative, 1).getEffects(player, player, false, false);
				}
				return "32571-12.htm";
			}
			else if(cond == 11)
				return "32571-13.htm";
			else if(cond == 12)
				return "32571-14a.htm";
		}
		else if(npcId == FrogKing)
		{
			if(player.getTransformation() != TransformFrog)
				return "32572-00.htm";
			if(cond == 4)
				return "32572-01.htm";
			else if(cond == 5)
				return "32572-04a.htm";
		}
		else if(npcId == GrandmaTess)
		{
			if(player.getTransformation() != TransformChild)
				return "32573-00.htm";
			if(cond == 7)
				return "32573-01.htm";
			else if(cond == 8)
				return "32573-03a.htm";
		}
		else if(npcId == VillagerKuta)
		{
			if(player.getTransformation() != TransformNative)
				return "32574-00.htm";
			if(cond == 10)
				return "32574-01.htm";
			else if(cond == 11)
				return "32574-04.htm";
		}
		else if(npcId == ClaudiaAthebaldt)
		{
			if(cond == 12)
				return "31001-01.htm";
		}
		return "noquest";
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}
