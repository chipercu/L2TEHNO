package l2open.status.gshandlers;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.network.MMOConnection;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.taskmanager.MemoryWatchDog;
import l2open.util.Util;

import java.io.PrintWriter;
import java.util.StringTokenizer;

public class HandlerPerfomance
{
	public static void LazyItems(String fullCmd, String[] argv, PrintWriter _print)
	{
		ConfigValue.LazyItemUpdate = !ConfigValue.LazyItemUpdate;
		_print.println("Lazy items update set to: " + ConfigValue.LazyItemUpdate);
	}

	public static void ThreadPool(String fullCmd, String[] argv, PrintWriter _print)
	{
		/*if(argv.length < 2 || argv[1] == null || argv[1].isEmpty())
			for(String line : ThreadPoolManager.getInstance().getStats())
				_print.println(line);
		else if(argv[1].equalsIgnoreCase("packets") || argv[1].equalsIgnoreCase("p"))
			_print.println(ThreadPoolManager.getInstance().getGPacketStats());
		else if(argv[1].equalsIgnoreCase("iopackets") || argv[1].equalsIgnoreCase("iop"))
			_print.println(ThreadPoolManager.getInstance().getIOPacketStats());
		else if(argv[1].equalsIgnoreCase("general") || argv[1].equalsIgnoreCase("g"))
			_print.println(ThreadPoolManager.getInstance().getGeneralPoolStats());
		else if(argv[1].equalsIgnoreCase("move") || argv[1].equalsIgnoreCase("m"))
			_print.println(ThreadPoolManager.getInstance().getMovePoolStats());
		else if(argv[1].equalsIgnoreCase("pathfind") || argv[1].equalsIgnoreCase("f"))
			_print.println(ThreadPoolManager.getInstance().getPathfindPoolStats());
		else if(argv[1].equalsIgnoreCase("npcAi"))
			_print.println(ThreadPoolManager.getInstance().getNpcAIPoolStats());
		else if(argv[1].equalsIgnoreCase("playerAi"))
			_print.println(ThreadPoolManager.getInstance().getPlayerAIPoolStats());
		else if(argv[1].equalsIgnoreCase("interest") || argv[1].equalsIgnoreCase("i"))
			_print.println(ThreadPoolManager.getThreadPoolStats(MMOConnection.getPool(), "interest"));
		else if(argv[1].equalsIgnoreCase("?"))
			_print.println("USAGE: performance [packets(p)|iopackets(iop)|general(g)|move(m)|pathfind(f)|npcAi|playerAi]");
		else
			_print.println("Unknown ThreadPool: " + argv[1]);*/
	}
	
	public static void GiveItem(String fullCmd, String[] argv, PrintWriter _print)
	{
		if(argv.length < 2 || argv[1] == null || argv[1].isEmpty() || argv[1].equalsIgnoreCase("?"))
			_print.println("USAGE: give player itemId amount");
		else
		{
			StringTokenizer st = new StringTokenizer(fullCmd.substring(5));
			try
			{
				L2Player player = L2World.getPlayer(st.nextToken());
				int itemId = Integer.parseInt(st.nextToken());
				int amount = Integer.parseInt(st.nextToken());

				if(player != null)
				{
					player.getInventory().addItem(itemId, amount);
					_print.println("ok");
				}
			}
			catch(Exception e)
			{

			}
		
		}
	}

	public static void GC(String fullCmd, String[] argv, PrintWriter _print)
	{
		long collected = Util.gc(1, 0);
		_print.println("Collected: " + collected / 0x100000 + " Mb / Now used memory " + MemoryWatchDog.getMemUsedMb() + " of " + MemoryWatchDog.getMemMaxMb() + " (" + MemoryWatchDog.getMemFreeMb() + " Mb is free)");
	}
}