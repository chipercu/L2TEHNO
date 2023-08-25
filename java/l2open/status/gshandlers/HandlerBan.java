package l2open.status.gshandlers;

import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.BanIP;
import l2open.gameserver.loginservercon.gspackets.UnbanIP;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.util.BannedIp;
import l2open.util.GArray;

import java.io.PrintWriter;
import java.net.Socket;

public class HandlerBan
{
	public static void BanIP(String fullCmd, String[] argv, PrintWriter _print, Socket _csocket)
	{
		if(argv.length < 2 || argv[1] == null || argv[1].isEmpty())
		{
			/*GArray<BannedIp> baniplist = LSConnection.getInstance().getBannedIpList();
			if(baniplist != null && baniplist.size() > 0)
			{
				_print.println("Ban IP List:");
				for(BannedIp temp : baniplist)
					_print.println("Ip:" + temp.ip + " banned by " + temp.admin);
			}
			else
				_print.println("No banned ips.");*/
		}
		else if(argv[1].equalsIgnoreCase("?"))
			_print.println("USAGE: banip [IP]");
		else
		{
			//LSConnection.getInstance().sendPacket(new BanIP(argv[1], "Telnet: " + _csocket.getInetAddress().getHostAddress()));
			_print.println("IP " + argv[1] + " banned");
		}
	}

	public static void UnBanIP(String fullCmd, String[] argv, PrintWriter _print)
	{
		if(argv.length < 2 || argv[1] == null || argv[1].isEmpty() || argv[1].equalsIgnoreCase("?"))
			_print.println("USAGE: unbanip IP");
		else
		{
			//LSConnection.getInstance().sendPacket(new UnbanIP(argv[1]));
			_print.println("IP " + argv[1] + " unbanned");
		}
	}

	public static void Kick(String fullCmd, String[] argv, PrintWriter _print)
	{
		if(argv.length < 2 || argv[1] == null || argv[1].isEmpty() || argv[1].equalsIgnoreCase("?"))
			_print.println("USAGE: kick Player");
		else
		{
			L2Player player = L2World.getPlayer(argv[1]);
			if(player == null)
				_print.println("Unable to find Player: " + argv[1]);
			else
			{
				player.sendMessage("You are kicked by admin");
				player.logout(false, false, true, true);
				_print.println("Player " + argv[1] + " kicked");
			}
		}
	}
}