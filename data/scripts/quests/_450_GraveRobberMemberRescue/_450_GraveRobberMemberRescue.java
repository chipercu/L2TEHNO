package quests._450_GraveRobberMemberRescue;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.util.Location;
import l2open.util.Rnd;

public class _450_GraveRobberMemberRescue extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static int KANEMIKA = 32650;
	private static int WARRIOR_NPC = 32651;

	private static int WARRIOR_MON = 22741;

	private static int EVIDENCE_OF_MIGRATION = 14876;

	public _450_GraveRobberMemberRescue()
	{
		super(false);

		addStartNpc(KANEMIKA);
		addTalkId(WARRIOR_NPC);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32650-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		L2Player player = st.getPlayer();

		if(npcId == KANEMIKA)
		{
			if(id == CREATED)
			{
				if(player.getLevel() < 80)
				{
					htmltext = "32650-00.htm";
					st.exitCurrentQuest(true);
				}
				else if(!canEnter(player))
				{
					htmltext = "32650-09.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "32650-01.htm";
			}
			else if(cond == 1)
			{
				if(st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) >= 1)
					htmltext = "32650-07.htm";
				else
					htmltext = "32650-06.htm";
			}
			else if(cond == 2 && st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) == 10)
			{
				htmltext = "32650-08.htm";
				st.giveItems(ADENA_ID, 65000);
				st.takeItems(EVIDENCE_OF_MIGRATION, -1);
				st.exitCurrentQuest(true);
				st.playSound(SOUND_FINISH);
				st.getPlayer().setVarInst(getName(), String.valueOf(System.currentTimeMillis()));
			}
		}
		else if(cond == 1 && npcId == WARRIOR_NPC)
		{
			if(Rnd.chance(50))
			{
				htmltext = "32651-01.htm";
				st.giveItems(EVIDENCE_OF_MIGRATION, 1);
				st.playSound(SOUND_ITEMGET);
				npc.moveToLocation(new Location(npc.getX() + 200, npc.getY() + 200, npc.getZ()), 0, false);
				DecayTaskManager.getInstance().addDecayTask(npc, 2500);
				if(st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) == 10)
				{
					st.set("cond", "2");
					st.playSound(SOUND_MIDDLE);
				}
			}
			else
			{
				htmltext = "";
				player.sendPacket(new ExShowScreenMessage("The grave robber warrior has been filled with dark energy and is attacking you!", 4000, ScreenMessageAlign.MIDDLE_CENTER, false));
				L2NpcInstance warrior = st.addSpawn(WARRIOR_MON, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 100, 120000);
				warrior.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Rnd.get(1, 100));

				if(Rnd.chance(50))
					Functions.npcSay(warrior, "...Grunt... oh...");
				else
					Functions.npcSay(warrior, "Grunt... What's... wrong with me...");

				npc.onDecay();

				return null;
			}
		}

		return htmltext;
	}

	private boolean canEnter(L2Player player)
	{
		if(player.isGM())
			return true;
		String var = player.getVar(getName());
		if(var == null)
			return true;
		return Long.parseLong(var) - System.currentTimeMillis() > 24 * 60 * 60 * 1000;
	}
}