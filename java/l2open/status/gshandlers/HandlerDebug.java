package l2open.status.gshandlers;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.debug.HeapDumper;
import l2open.gameserver.ai.L2PlayableAI.ThinkFollow;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.geodata.GeoMove;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2World;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.status.DummyL2Object;
import l2open.util.Location;
import l2open.util.Util;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class HandlerDebug
{
	public static void Debug(String fullCmd, String[] argv, PrintWriter _print, Socket _csocket)
	{
		if(argv.length < 2 || argv[1] == null || argv[1].isEmpty() || argv[1].equalsIgnoreCase("?"))
			_print.println("USAGE: debug decay|geo");
		else if(argv[1].equalsIgnoreCase("decay"))
			_print.print(DecayTaskManager.getInstance());
		else if(argv[1].equalsIgnoreCase("ThinkFollow"))
		{
			/*for(Runnable ai_task : ThreadPoolManager.getInstance().getPlayerAiScheduledThreadPool().getQueue())
				if(ai_task instanceof ThinkFollow)
				{
					L2Character actor = ((ThinkFollow) ai_task).getActor();
					_print.print(actor.getName() + " [" + actor.getClass().getSimpleName() + "] follow ");
					actor = actor.getFollowTarget();
					if(actor == null)
						_print.println("NOTARGET");
					else
						_print.println(actor.getName() + " [" + actor.getClass().getSimpleName() + "]");

				}*/
		}
		else if(argv[1].equalsIgnoreCase("geo"))
		{
			if(argv.length < 3 || argv[2] == null || argv[2].isEmpty() || argv[2].equalsIgnoreCase("?"))
				_print.println("USAGE: debug geo x,y,z=>x,y,z");
			else
				_print.println(DebugGeo(Util.joinStrings(" ", argv, 2)));
		}
		else
			_print.println("Unknown debug type: " + argv[1]);
	}

	public static void HprofMemDump(String fullCmd, String[] argv, PrintWriter _print)
	{
		boolean live = true;
		String dir = ConfigValue.SnapshotsDirectory;
		if(argv.length > 1)
		{
			if(argv[1].equalsIgnoreCase("?"))
			{
				_print.println("USAGE: dumpmem [Directory] [live:true|false]");
				return;
			}
			dir = argv[1];
			if(argv.length > 2)
				live = Boolean.parseBoolean(argv[2]);
		}
		try
		{
			_print.println("Memory snapshot saved: " + HeapDumper.dumpHeap(dir, live));
		}
		catch(Exception e)
		{
			e.printStackTrace(_print);
		}
	}

	public static String DebugGeo(String arg)
	{
		String[] args = arg.split("=>");
		if(args.length < 2)
			return "You should define source & dest location (x,y,z=>x,y,z)";
		Location src_loc = new Location(args[0]);
		Location dst_loc = new Location(args[1]);

		String result = "Move checking " + src_loc.toXYZString() + " => " + dst_loc.toXYZString() + "\r\n";
		result += "\t isWater...........................: " + L2World.isWater(dst_loc.x, dst_loc.y, dst_loc.z) + "\r\n";
		result += "\t canMoveToCoord....................: " + GeoEngine.canMoveToCoord(src_loc.x, src_loc.y, src_loc.z, dst_loc.x, dst_loc.y, dst_loc.z, 0) + "\r\n";
		result += "\t moveCheck.........................: " + GeoEngine.moveCheck(src_loc.x, src_loc.y, src_loc.z, dst_loc.x, dst_loc.y, 0).toXYZString() + "\r\n";
		result += "\t moveCheckWithCollision............: " + GeoEngine.moveCheckWithCollision(src_loc.x, src_loc.y, src_loc.z, dst_loc.x, dst_loc.y, 0).toXYZString() + "\r\n";
		result += "\t moveCheckWithCollision[pf_1]......: " + GeoEngine.moveCheckWithCollision(src_loc.x, src_loc.y, src_loc.z, dst_loc.x, dst_loc.y, true, 0).toXYZString() + "\r\n";
		result += "\t moveCheckWithCollision[pf_2]......: " + GeoEngine.moveCheckBackwardWithCollision(dst_loc.x, dst_loc.y, dst_loc.z, src_loc.x, src_loc.y, true, 0).toXYZString() + "\r\n";

		result += "\t findPath: ";
		/*List<Location> targets = GeoMove.findPath(src_loc.x, src_loc.y, src_loc.z, dst_loc.clone(), new DummyL2Object(), false, 0);
		if(targets.isEmpty())
			result += "Empty";
		else
			for(int i = 0; i < targets.size(); i++)
				result += "\r\n\t\t\t [" + i + "]: " + targets.get(i).toXYZString();
		result += "\r\n";*/
		return result;
	}
}