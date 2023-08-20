package quests._653_WildMaiden;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SpawnTable;
import l2open.util.GArray;

public class _653_WildMaiden extends Quest implements ScriptFile
{
	// Npc
	public final int SUKI = 32013;
	public final int GALIBREDO = 30181;

	// Items
	public final int SOE = 736;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _653_WildMaiden()
	{
		super(false);

		addStartNpc(SUKI);

		addTalkId(SUKI);
		addTalkId(GALIBREDO);
	}

	private L2NpcInstance findNpc(int npcId, L2Player player)
	{
		L2NpcInstance instance = null;
		GArray<L2NpcInstance> npclist = new GArray<L2NpcInstance>();
		for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
			if(spawn.getNpcId() == npcId)
			{
				instance = spawn.getLastSpawn();
				npclist.add(instance);
			}

		for(L2NpcInstance npc : npclist)
			if(player.isInRange(npc, 1600))
				return npc;

		return instance;
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		L2Player player = st.getPlayer();
		if(event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm"))
		{
			if(st.getQuestItemsCount(SOE) > 0)
			{
				st.set("cond", "1");
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.takeItems(SOE, 1);
				htmltext = "spring_girl_sooki_q0653_04a.htm";
				L2NpcInstance n = findNpc(SUKI, player);
				n.broadcastSkill(new MagicSkillUse(n, n, 2013, 1, 20000, 0));
				st.startQuestTimer("suki_timer", 20000);
			}
		}
		else if(event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm"))
		{
			st.exitCurrentQuest(false);
			st.playSound(SOUND_GIVEUP);
		}
		else if(event.equalsIgnoreCase("suki_timer"))
		{
			L2NpcInstance n = findNpc(SUKI, player);
			n.deleteMe();
			htmltext = null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";

		int npcId = npc.getNpcId();
		int id = st.getState();
		if(npcId == SUKI && id == CREATED)
		{
			if(st.getPlayer().getLevel() >= 36)
				htmltext = "spring_girl_sooki_q0653_01.htm";
			else
			{
				htmltext = "spring_girl_sooki_q0653_01a.htm";
				st.exitCurrentQuest(false);
			}
		}
		else if(npcId == GALIBREDO && st.getInt("cond") == 1)
		{
			htmltext = "galicbredo_q0653_01.htm";
			st.giveItems(ADENA_ID, 2883);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}
}