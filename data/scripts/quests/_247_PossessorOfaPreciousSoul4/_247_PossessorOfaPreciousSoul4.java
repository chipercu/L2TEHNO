package quests._247_PossessorOfaPreciousSoul4;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Util;
//import quests._246_PossessorOfaPreciousSoul3._246_PossessorOfaPreciousSoul3;

public class _247_PossessorOfaPreciousSoul4 extends Quest implements ScriptFile
{
	private static int CARADINE = 31740;
	private static int LADY_OF_LAKE = 31745;

	private static int CARADINE_LETTER_LAST = 7679;
	private static int NOBLESS_TIARA = 7694;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _247_PossessorOfaPreciousSoul4()
	{
		super(false);

		addStartNpc(CARADINE);

		addTalkId(LADY_OF_LAKE);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getInt("cond");
		if(cond == 0 && event.equals("caradine_q0247_03.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(cond == 1)
		{
			if(event.equals("caradine_q0247_04.htm"))
				return htmltext;
			else if(event.equals("caradine_q0247_05.htm") && st.getQuestItemsCount(CARADINE_LETTER_LAST) >= 1)
			{
				st.set("cond", "2");
				st.takeItems(CARADINE_LETTER_LAST, 1);
				st.getPlayer().teleToLocation(143230, 44030, -3030);
				return htmltext;
			}
		}
		else if(cond == 2)
			if(event.equals("caradine_q0247_06.htm"))
				return htmltext;
			else if(event.equals("caradine_q0247_05.htm"))
			{
				st.getPlayer().teleToLocation(143230, 44030, -3030);
				return htmltext;
			}
			else if(event.equals("lady_of_the_lake_q0247_02.htm"))
				return htmltext;
			else if(event.equals("lady_of_the_lake_q0247_03.htm"))
				return htmltext;
			else if(event.equals("lady_of_the_lake_q0247_04.htm"))
				return htmltext;
			else if(event.equals("lady_of_the_lake_q0247_05.htm"))
				if(st.getPlayer().getLevel() >= 75)
				{
					st.giveItems(NOBLESS_TIARA, 1);
					st.addExpAndSp(93836, 0);
					st.playSound(SOUND_FINISH);
					st.unset("cond");
					st.exitCurrentQuest(false);
					Olympiad.addNoble(st.getPlayer());
					st.getPlayer().setNoble(true);
					npc.setTarget(st.getPlayer());
					npc.doCast(SkillTable.getInstance().getInfo(4339, 1), st.getPlayer(), false);
					st.getPlayer().broadcastPacket(new SocialAction(st.getPlayer().getObjectId(), SocialAction.VICTORY));
					st.getPlayer().updatePledgeClass();
					st.getPlayer().updateNobleSkills();
					st.getPlayer().sendPacket(new SkillList(st.getPlayer()));
					st.getPlayer().broadcastUserInfo(true);
				}
				else
					htmltext = "lady_of_the_lake_q0247_06.htm";
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		if(!st.getPlayer().isSubClassActive())
			return "Subclass only!";

		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");
		if(npcId == CARADINE)
		{
			//QuestState previous = st.getPlayer().getQuestState(_246_PossessorOfaPreciousSoul3.class);
			if(id == CREATED && st.getQuestItemsCount(CARADINE_LETTER_LAST) >= 1)
			{
				if(st.getPlayer().getLevel() < 75)
				{
					htmltext = "caradine_q0247_02.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "caradine_q0247_01.htm";
			}
			else if(cond == 1)
				htmltext = "caradine_q0247_03.htm";
			else if(cond == 2)
				htmltext = "caradine_q0247_06.htm";
		}
		else if(npcId == LADY_OF_LAKE && cond == 2)
			if(st.getPlayer().getLevel() >= 75)
				htmltext = "lady_of_the_lake_q0247_01.htm";
			else
				htmltext = "lady_of_the_lake_q0247_06.htm";
		return htmltext;
	}
}