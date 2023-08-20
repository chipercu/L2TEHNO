package l2open.status.gshandlers;

import l2open.config.ConfigValue;
import l2open.extensions.Stat;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.Shutdown;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.GmListTable;
import l2open.gameserver.taskmanager.MemoryWatchDog;
import l2open.util.Util;

import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HandlerStatus
{
	public static void Version(String fullCmd, String[] argv, PrintWriter _print)
	{
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
		_print.println("L2Open server : " + ConfigValue.version + " builded " + ConfigValue.builddate);
		_print.println("JVM .............: " + rt.getVmVendor() + " " + rt.getVmName() + " " + rt.getVmVersion());
		_print.println("OS ..............: " + os.getName() + " " + os.getVersion() + " " + os.getArch() + ", " + os.getAvailableProcessors() + " CPUs, Load Average: " + os.getSystemLoadAverage());
		_print.println("CPU Cores .......: " + Runtime.getRuntime().availableProcessors());
	}

	public static void Config(String fullCmd, String[] argv, PrintWriter _print)
	{
		_print.println("USAGE: config ololosh...");
	}

	public static void GmList(String fullCmd, String[] argv, PrintWriter _print)
	{
		int gmsCount = 0;
		String gmList = "";

		for(L2Player player : GmListTable.getAllGMs())
		{
			gmList = gmList + ", " + player.getName();
			gmsCount++;
		}
		_print.print("There are currently " + gmsCount + " GM(s) online");
		_print.println(gmsCount > 0 ? ": " + gmList : "...");
	}

	public static void Database(String fullCmd, String[] argv, PrintWriter _print)
	{
		_print.println("Database Usage Status: ");
		_print.println("+... Players operation: ");
		_print.println("-->  Update characters: " + Stat.getUpdatePlayerBase());
		_print.println("+..... Items operation: ");
		_print.println("-->      Insert: " + Stat.getInsertItemCount());
		_print.println("-->      Delete: " + Stat.getDeleteItemCount());
		_print.println("-->      Update: " + Stat.getUpdateItemCount());
		_print.println("--> Lazy Update: " + Stat.getLazyUpdateItem());
		_print.println("+... Lazy items update: " + ConfigValue.LazyItemUpdate);
		_print.println("+... Released ObjectId: " + IdFactory.getInstance().getReleasedCount());
	}

	public static void Status(String fullCmd, String[] argv, PrintWriter _print)
	{
		if(argv.length >= 2 && argv[1] != null && argv[1].equalsIgnoreCase("mobs"))
		{
			String mobType;
			int mobCount;
			HashMap<String, Integer> mobs = new HashMap<String, Integer>();
			for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcs())
				if(npc.isMonster())
				{
					mobType = npc.getTypeName();
					mobCount = mobs.containsKey(mobType) ? mobs.remove(mobType) : 0;
					mobCount++;
					mobs.put(mobType, mobCount);
				}
			for(String mob : mobs.keySet())
				_print.println("\t" + mob + ": \t" + mobs.get(mob));
			return;
		}

		int playerCount = L2ObjectsStorage.getAllPlayersCount();
		int offtradeCount = L2ObjectsStorage.getAllOfflineCount();
		int onlineCount = playerCount - offtradeCount;
		int objectCount = L2ObjectsStorage.getAllObjectsCount();
		int itemsVoid = 0, itemsDummy = 0, itemsClan = 0, itemsChar = 0, itemsFreight = 0, itemsCharWh = 0, itemsNpc = 0;
		int monsterCount = 0;
		int minionCount = 0;
		int npcCount = 0;
		int guardCount = 0;
		int charCount = 0;
		int doorCount = 0;
		int summonCount = 0;
		int AICount = 0;
		int extendedAICount = 0;
		int summonAICount = 0;
		int activeAICount = 0;

		for(L2Object obj : L2ObjectsStorage.getAllObjects())
			if(obj.isCharacter())
			{
				charCount++;
				if(obj.isNpc())
				{
					npcCount++;
					if(obj.isMonster())
					{
						monsterCount++;
						minionCount += ((L2MonsterInstance) obj).getTotalSpawnedMinionsInstances();
					}
				}
				else if(obj.isSummon() || obj.isPet())
					summonCount++;
				else if(obj.isDoor())
					doorCount++;
				if(obj.hasAI())
				{
					AICount++;
					if(obj.getAI().isActive())
						activeAICount++;
					if(obj.isNpc())
						extendedAICount++;
					else if(obj.isSummon() || obj.isPet())
						summonAICount++;
				}
			}
			else if(obj.isItem())
				switch(((L2ItemInstance) obj).getLocation())
				{
					case VOID:
						itemsVoid++;
						break;
					case CLANWH:
						itemsClan++;
						break;
					case INVENTORY:
					case PAPERDOLL:
						itemsChar++;
						break;
					case FREIGHT:
						itemsFreight++;
						break;
					case WAREHOUSE:
						itemsCharWh++;
						break;
					case MONSTER:
						itemsNpc++;
						break;
					case DUMMY:
						itemsDummy++;
						break;
				}
		_print.println("Server Status: ");
		_print.println(" +.............. Players: " + playerCount + "/" + ConfigValue.MaximumOnlineUsers);
		_print.println(" +............... Online: " + onlineCount);
		_print.println(" +.............. Offline: " + offtradeCount);
		_print.println(" +.............. Summons: " + summonCount);
		_print.println(" +............. Monsters: " + monsterCount);
		_print.println(" +.............. Minions: " + minionCount);
		_print.println(" +........ Castle Guards: " + guardCount);
		_print.println(" +................ Doors: " + doorCount);
		_print.println(" +................. Npcs: " + npcCount);
		_print.println(" +........... Characters: " + charCount);
		_print.println(" +.............. Objects: " + objectCount);
		_print.println(" +............... All AI: " + AICount);
		_print.println(" +...... Active AI Count: " + activeAICount);
		_print.println(" +.......... Extended AI: " + extendedAICount);
		_print.println(" +............ Summon AI: " + summonAICount);
		_print.println(" +......... Ground Items: " + itemsVoid);
		_print.println(" +.......... Owned Items: " + itemsChar);
		if(itemsCharWh > 0)
			_print.println(" +...... Owned(WH) Items: " + itemsCharWh);
		if(itemsFreight > 0)
			_print.println(" +........ Freight Items: " + itemsFreight);
		if(itemsDummy > 0)
			_print.println(" +.......... Dummy Items: " + itemsDummy);
		if(itemsClan > 0)
			_print.println(" +......... ClanWh Items: " + itemsClan);
		if(itemsNpc > 0)
			_print.println(" +...........  NPC Items: " + itemsNpc);
		_print.println(" +................... GM: " + GmListTable.getAllGMs().size());
		_print.println(" + Game Time / Real Time: " + GameTime() + " / " + getCurrentTime());
		_print.println(" +.. Start Time / Uptime: " + getStartTime() + " / " + getUptime());
		_print.println(" +...... Shutdown / mode: " + Util.formatTime(Shutdown.getInstance().getSeconds()) + " / " + Shutdown.getInstance().getMode());
		_print.println(" +....... Active Regions: " + L2World.getRegionsCount(true) + " / " + L2World.getRegionsCount(null));
		_print.println(" +.............. Threads: " + Thread.activeCount());
		_print.println(" +............. RAM Used: " + MemoryWatchDog.getMemUsedMb());
	}

	public static String GameTime()
	{
		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60;
		int m = t % 60;
		SimpleDateFormat format = new SimpleDateFormat("H:mm");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		return format.format(cal.getTime());
	}

	public static String getUptime()
	{
		return Util.formatTime(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
	}

	public static String getStartTime()
	{
		return new Date(ManagementFactory.getRuntimeMXBean().getStartTime()).toString();
	}

	public static String getCurrentTime()
	{
		return new Date().toString();
	}
}