package quests._646_SignsOfRevolt;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _646_SignsOfRevolt extends Quest implements ScriptFile
{
	// NPCs
	private static int TORRANT = 32016;
	// Mobs
	private static int Ragna_Orc = 22691; // First in Range
	private static int Ragna_Orc_Sniper = 22699; // Last in Range
	// Items
	private static int Steel = 1880;
	private static int Coarse_Bone_Powder = 1881;
	private static int Leather = 1882;
	// Quest Items
	private static int CURSED_DOLL = 8087;
	// Chances
	private static int CURSED_DOLL_Chance = 75;

	public _646_SignsOfRevolt()
	{
		super(false);
		addStartNpc(TORRANT);
		for(int Ragna_Orc_id = Ragna_Orc; Ragna_Orc_id <= Ragna_Orc_Sniper; Ragna_Orc_id++)
			addKillId(Ragna_Orc_id);
		addQuestItem(CURSED_DOLL);
	}

	private static String doReward(QuestState st, int reward_id, int _count)
	{
		if(st.getQuestItemsCount(CURSED_DOLL) < 180)
			return null;
		st.takeItems(CURSED_DOLL, -1);
		st.giveItems(reward_id, _count, true);
		st.playSound(SOUND_FINISH);
		st.exitCurrentQuest(true);
		return "torant_q0646_0202.htm";
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int _state = st.getState();
		if(event.equalsIgnoreCase("torant_q0646_0103.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("reward_adena") && _state == STARTED)
			return doReward(st, ADENA_ID, 21600);
		else if(event.equalsIgnoreCase("reward_cbp") && _state == STARTED)
			return doReward(st, Coarse_Bone_Powder, 12);
		else if(event.equalsIgnoreCase("reward_steel") && _state == STARTED)
			return doReward(st, Steel, 9);
		else if(event.equalsIgnoreCase("reward_leather") && _state == STARTED)
			return doReward(st, Leather, 20);

		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(npc.getNpcId() != TORRANT)
			return htmltext;
		int _state = st.getState();

		if(_state == CREATED)
		{
			if(st.getPlayer().getLevel() < 81)
			{
				htmltext = "torant_q0646_0102.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "torant_q0646_0101.htm";
				st.set("cond", "0");
			}
		}
		else if(_state == STARTED)
			htmltext = st.getQuestItemsCount(CURSED_DOLL) >= 180 ? "torant_q0646_0105.htm" : "torant_q0646_0106.htm";

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		L2Player player = qs.getRandomPartyMember(STARTED, ConfigValue.AltPartyDistributionRange);
		if(player == null)
			return null;
		QuestState st = player.getQuestState(qs.getQuest().getName());

		long CURSED_DOLL_COUNT = st.getQuestItemsCount(CURSED_DOLL);
		if(CURSED_DOLL_COUNT < 180 && Rnd.chance(CURSED_DOLL_Chance))
		{
			st.giveItems(CURSED_DOLL, 1);
			if(CURSED_DOLL_COUNT == 179)
			{
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "2");
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}