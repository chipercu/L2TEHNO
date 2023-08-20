package quests._160_NerupasFavor;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class _160_NerupasFavor extends Quest implements ScriptFile
{
	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	private static int SILVERY_SPIDERSILK = 1026;
	private static int UNOS_RECEIPT = 1027;
	private static int CELS_TICKET = 1028;
	private static int NIGHTSHADE_LEAF = 1029;
	private static int LESSER_HEALING_POTION = 1060;

	private static int NERUPA = 30370;
	private static int UNOREN = 30147;
	private static int CREAMEES = 30149;
	private static int JULIA = 30152;

	/**
	 * Delivery of Goods
	 * Trader Unoren asked Nerupa to collect silvery spidersilks for him.
	 * Norupa doesn't want to enter the village and asks you to deliver the silvery spidersilks to Trader Unoren in the weapons shop and bring back a nightshade leaf.	 * 
	 */
	private static int COND1 = 1;

	/**
	 * Nightshade Leaf
	 * Nightshade leaves are very rare. Fortunately, Trader Creamees of the magic shop has obtained a few of them. Go see him with Unoren's receipt.
	 */
	private static int COND2 = 2;

	/**
	 * Go to the Warehouse
	 * Since nightshade leaf is so rare it has been stored in the warehouse. Take Creamees' ticket to Warehouse Keeper Julia.
	 */
	private static int COND3 = 3;

	/**
	 * Goods to be Delivered to Nerupa
	 * You've obtained the nightshade leaf that Creamees stored in the warehouse. Deliver it to Nerupa.
	 */
	private static int COND4 = 4;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _160_NerupasFavor()
	{
		super(false);

		addStartNpc(NERUPA);

		addTalkId(NERUPA, UNOREN, CREAMEES, JULIA);

		addQuestItem(SILVERY_SPIDERSILK, UNOS_RECEIPT, CELS_TICKET, NIGHTSHADE_LEAF);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("quest_accept"))
		{
			htmltext = "nerupa_q0160_04.htm";
			st.setCond(COND1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(SILVERY_SPIDERSILK, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if(npcId == NERUPA)
		{
			if(st.getState() == CREATED)
			{
				if(st.getPlayer().getRace() != Race.elf)
					htmltext = "nerupa_q0160_00.htm";
				else if(st.getPlayer().getLevel() < 3)
				{
					htmltext = "nerupa_q0160_02.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "nerupa_q0160_03.htm";
			}
			else if(cond == COND1)
				htmltext = "nerupa_q0160_04.htm";
			else if(cond == COND4 && st.getQuestItemsCount(NIGHTSHADE_LEAF) > 0)
			{
				st.takeItems(NIGHTSHADE_LEAF, -1);
				st.giveItems(LESSER_HEALING_POTION, 5);
				st.addExpAndSp(1000, 0);
				st.playSound(SOUND_FINISH);
				htmltext = "nerupa_q0160_06.htm";
				st.exitCurrentQuest(false);
			}
			else
				htmltext = "nerupa_q0160_05.htm";
		}
		else if(npcId == UNOREN)
		{
			if(cond == COND1)
			{
				st.takeItems(SILVERY_SPIDERSILK, -1);
				st.giveItems(UNOS_RECEIPT, 1);
				st.setCond(COND2);
				htmltext = "uno_q0160_01.htm";
			}
			else if(cond == COND2 || cond == COND3)
				htmltext = "uno_q0160_02.htm";
			else if(cond == COND4)
				htmltext = "uno_q0160_03.htm";
		}
		else if(npcId == CREAMEES)
		{
			if(cond == COND2)
			{
				st.takeItems(UNOS_RECEIPT, -1);
				st.giveItems(CELS_TICKET, 1);
				st.setCond(COND3);
				htmltext = "cel_q0160_01.htm";
			}
			else if(cond == COND3)
				htmltext = "cel_q0160_02.htm";
			else if(cond == COND4)
				htmltext = "cel_q0160_03.htm";
		}
		else if(npcId == JULIA)
			if(cond == COND3)
			{
				st.takeItems(CELS_TICKET, -1);
				st.giveItems(NIGHTSHADE_LEAF, 1);
				htmltext = "jud_q0160_01.htm";
				st.setCond(COND4);
			}
			else if(cond == COND4)
				htmltext = "jud_q0160_02.htm";
		return htmltext;
	}
}