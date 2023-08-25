package quests._382_KailsMagicCoin;

import java.util.HashMap;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _382_KailsMagicCoin extends Quest implements ScriptFile
{
	//Quest items
	private static int ROYAL_MEMBERSHIP = 5898;
	//NPCs
	private static int VERGARA = 30687;
	//MOBs and CHANCES
	private static final HashMap<Integer, int[]> MOBS = new HashMap<Integer, int[]>();
	static
	{
		MOBS.put(21017, new int[] { 5961 }); // Fallen Orc
		MOBS.put(21019, new int[] { 5962 }); // Fallen Orc Archer
		MOBS.put(21020, new int[] { 5963 }); // Fallen Orc Shaman
		MOBS.put(21022, new int[] { 5961, 5962, 5963 }); // Fallen Orc Captain
		//MOBS.put(21258, new int[] { 5961, 5962, 5963 }); // Fallen Orc Shaman - WereTiger
		//MOBS.put(21259, new int[] { 5961, 5962, 5963 }); // Fallen Orc Shaman - WereTiger, transformed
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _382_KailsMagicCoin()
	{
		super(false);

		addStartNpc(VERGARA);

		for(int mobId : MOBS.keySet())
			addKillId(mobId);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("head_blacksmith_vergara_q0382_03.htm"))
			if(st.getPlayer().getLevel() >= 55 && st.getQuestItemsCount(ROYAL_MEMBERSHIP) > 0)
			{
				st.set("cond", "1");
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}
			else
			{
				htmltext = "head_blacksmith_vergara_q0382_01.htm";
				st.exitCurrentQuest(true);
			}
		else if(event.equalsIgnoreCase("list"))
		{
			L2Multisell.getInstance().SeparateAndSend(ConfigValue.Alt100PercentRecipesA ? 383 : 382, st.getPlayer(), 0);
			htmltext = null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(st.getQuestItemsCount(ROYAL_MEMBERSHIP) == 0 || st.getPlayer().getLevel() < 55)
		{
			htmltext = "head_blacksmith_vergara_q0382_01.htm";
			st.exitCurrentQuest(true);
		}
		else if(cond == 0)
			htmltext = "head_blacksmith_vergara_q0382_02.htm";
		else
			htmltext = "head_blacksmith_vergara_q0382_04.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() != STARTED || st.getQuestItemsCount(ROYAL_MEMBERSHIP) == 0)
			return null;

		int[] droplist = MOBS.get(npc.getNpcId());
		st.rollAndGive(droplist[Rnd.get(droplist.length)], 1, 10);
		return null;
	}
}