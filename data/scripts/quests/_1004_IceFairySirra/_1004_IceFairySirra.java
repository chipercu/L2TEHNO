package quests._1004_IceFairySirra;

import java.util.concurrent.ScheduledFuture;

import javolution.util.FastList;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.SpawnTable;
import l2open.util.Location;
import l2open.util.Rnd;

public class _1004_IceFairySirra extends Quest implements ScriptFile
{
	private static final int STEWARD = 32029;
	private static final int SILVER_HEMOCYTE = 8057;

	private static FastList<L2NpcInstance> _allMobs = new FastList<L2NpcInstance>();

	private static ScheduledFuture<?> _startTask = null;
	private static ScheduledFuture<?> _partyPortTask = null;
	private static ScheduledFuture<?> _30MinutesRemainingTask = null;
	private static ScheduledFuture<?> _20MinutesRemainingTask = null;
	private static ScheduledFuture<?> _10MinutesRemainingTask = null;
	private static ScheduledFuture<?> _endTask = null;
	private static ScheduledFuture<?> _respawnTask = null;

	// x, y, z, heading, npcId
	private static Location[] _spawns = { new Location(105546, -127892, -2768, 0, 29060),
			new Location(102779, -125920, -2840, 0, 29056), new Location(111719, -126646, -2992, 0, 22100),
			new Location(109509, -128946, -3216, 0, 22102), new Location(109680, -125756, -3136, 0, 22104) };

	public _1004_IceFairySirra()
	{
		super("Ice Fairy Sirra", false);

		addFirstTalkId(STEWARD);
		addKillId(STEWARD, 22100, 22102, 22104, 29056);

		long remain = ServerVariables.getLong("Sirra_Respawn", 0) - System.currentTimeMillis();
		if(remain <= 0)
			setBusy(false);
		else
		{
			setBusy(true);
			_respawnTask = ThreadPoolManager.getInstance().schedule(new EventTask("respawn", null), remain);
		}

		openGates();
	}

	private static class EventTask extends l2open.common.RunnableImpl
	{
		String event;
		L2Player player;

		public EventTask(String event, L2Player player)
		{
			this.event = event;
			this.player = player;
		}

		public void runImpl()
		{
			if(event.equalsIgnoreCase("start"))
			{
				closeGates();
				doSpawns();
				_partyPortTask = ThreadPoolManager.getInstance().schedule(new EventTask("Party_Port", player), 2000);
				_endTask = ThreadPoolManager.getInstance().schedule(new EventTask("End", player), 1802000);
				_startTask = null;
			}
			else if(event.equalsIgnoreCase("Party_Port"))
			{
				teleportInside(player);
				screenMessage(player, "Steward: Please restore the Queen's appearance!", 10000);
				_30MinutesRemainingTask = ThreadPoolManager.getInstance().schedule(new EventTask("30MinutesRemaining", player), 300000);
				_partyPortTask = null;
			}
			else if(event.equalsIgnoreCase("30MinutesRemaining"))
			{
				screenMessage(player, "30 minute(s) are remaining.", 10000);
				_20MinutesRemainingTask = ThreadPoolManager.getInstance().schedule(new EventTask("20MinutesRemaining", player), 600000);
				_30MinutesRemainingTask = null;
			}
			else if(event.equalsIgnoreCase("20MinutesRemaining"))
			{
				screenMessage(player, "20 minute(s) are remaining.", 10000);
				_10MinutesRemainingTask = ThreadPoolManager.getInstance().schedule(new EventTask("10MinutesRemaining", player), 600000);
				_20MinutesRemainingTask = null;
			}
			else if(event.equalsIgnoreCase("10MinutesRemaining"))
			{
				screenMessage(player, "Steward: Waste no time! Please hurry!", 10000);
				_10MinutesRemainingTask = null;
			}
			else if(event.equalsIgnoreCase("End"))
			{
				screenMessage(player, "Steward: Was it indeed too much to ask.", 10000);
				cleanUp();
			}
			else if(event.equalsIgnoreCase("respawn"))
				cleanUp();
		}
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState hostQuest = player.getQuestState("_10285_MeetingSirra");
		if(hostQuest == null || hostQuest.getInt("cond") != 7)
		{
			if(player.getQuestState(getClass()) == null)
				newQuestState(player, STARTED);
			if(npc.isBusy())
				return "32029-10.htm";
			return "32029.htm";
		}
		else
			return "";
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		if(event.equalsIgnoreCase("check_condition"))
		{
			if(player.getLevel() >= 82)
			{
				player.teleToLocation(103045,-124361,-2768);
				return "";
			}
			else
			{
				if(player.isInParty())
				{
					if(player.getParty().getPartyLeader().getObjectId() == player.getObjectId())
					{
						if(checkItems(player))
						{
							cleanUp();
							setBusy(true);
							_startTask = ThreadPoolManager.getInstance().schedule(new EventTask("start", player), 100000);
							destroyItems(player);
							st.giveItems(8379, 3);
							screenMessage(player, "Steward: Please wait a moment.", 100000);
							return "32029-3.htm";
						}
						return "32029-2.htm";
					}
					return "32029-1.htm";
				}
				return "32029-1a.htm";
			}
		}
		return event;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(npc.getNpcId() == 29056)
		{
			cleanUp();
			setBusy(true);
			int respawn_delay = Rnd.get(43200000, 129600000);
			ServerVariables.set("Sirra_Respawn", System.currentTimeMillis() + respawn_delay);
			_respawnTask = ThreadPoolManager.getInstance().schedule(new EventTask("respawn", null), respawn_delay);
			screenMessage(st.getPlayer(), "Steward: Thank you for restoring the Queen's appearance!", 10000);
		}
		return null;
	}

	public static void cleanUp()
	{
		if(_startTask != null)
		{
			_startTask.cancel(false);
			_startTask = null;
		}
		if(_partyPortTask != null)
		{
			_partyPortTask.cancel(false);
			_partyPortTask = null;
		}
		if(_30MinutesRemainingTask != null)
		{
			_30MinutesRemainingTask.cancel(false);
			_30MinutesRemainingTask = null;
		}
		if(_20MinutesRemainingTask != null)
		{
			_20MinutesRemainingTask.cancel(false);
			_20MinutesRemainingTask = null;
		}
		if(_10MinutesRemainingTask != null)
		{
			_10MinutesRemainingTask.cancel(false);
			_10MinutesRemainingTask = null;
		}
		if(_endTask != null)
		{
			_endTask.cancel(false);
			_endTask = null;
		}
		if(_respawnTask != null)
		{
			_respawnTask.cancel(false);
			_respawnTask = null;
		}
		for(L2NpcInstance mob : _allMobs)
			mob.deleteMe();
		_allMobs.clear();
		setBusy(false);
	}

	public static void setBusy(boolean value)
	{
		L2NpcInstance steward = findTemplate(STEWARD);
		if(steward != null)
			steward.setBusy(value);
	}

	public static L2NpcInstance findTemplate(int npcId)
	{
		for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
			if(spawn.getNpcId() == npcId)
				return spawn.getLastSpawn();
		return null;
	}

	public static void openGates()
	{
		for(int i = 23140001; i < 23140003; i++)
			DoorTable.getInstance().getDoor(i).openMe();
	}

	public static void closeGates()
	{
		for(int i = 23140001; i < 23140003; i++)
			DoorTable.getInstance().getDoor(i).closeMe();
	}

	public static boolean checkItems(L2Player player)
	{
		if(player.getParty() == null)
			return false;
		for(L2Player pc : player.getParty().getPartyMembers())
		{
			L2ItemInstance i = pc.getInventory().getItemByItemId(SILVER_HEMOCYTE);
			if(i == null || i.getCount() < 10)
				return false;
		}
		return true;
	}

	public static void destroyItems(L2Player player)
	{
		L2Party party = player.getParty();
		if(party != null)
			for(L2Player pc : party.getPartyMembers())
				pc.getInventory().destroyItemByItemId(SILVER_HEMOCYTE, 10, true);
		else
			cleanUp();
	}

	public static void teleportInside(L2Player player)
	{
		if(player.getParty() != null)
			for(L2Player pc : player.getParty().getPartyMembers())
				pc.teleToLocation(113533, -126159, -3488);
		else
			cleanUp();
	}

	public static void screenMessage(L2Player player, String text, int time)
	{
		if(player.getParty() != null)
			for(L2Player pc : player.getParty().getPartyMembers())
				pc.sendPacket(new ExShowScreenMessage(text, time));
		else
			cleanUp();
	}

	public static void doSpawns()
	{
		for(Location spawn : _spawns)
			_allMobs.add(Functions.spawn(spawn, spawn.id));
	}

	public void onLoad()
	{
		ScriptFile._log.info("Loaded Quest: 1004: Ice Fairy Sirra");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}