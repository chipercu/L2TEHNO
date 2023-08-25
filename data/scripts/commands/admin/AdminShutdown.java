package commands.admin;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import l2open.config.ConfigValue;
import l2open.common.StatsUtil;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.GameStart;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.Shutdown;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.FakePlayersTable;
import l2open.gameserver.taskmanager.MemoryWatchDog;
import l2open.util.RateService;
import l2open.util.Util;

public class AdminShutdown implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_server_shutdown,
		admin_server_restart,
		admin_server_abort,
		admin_disconect_all
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanRestart)
			return false;

		try
		{
			switch(command)
			{
				case admin_server_shutdown:
					serverShutdown(activeChar, Integer.parseInt(wordList[1]), false);
					break;
				case admin_server_restart:
					serverShutdown(activeChar, Integer.parseInt(wordList[1]), true);
					break;
				case admin_server_abort:
					serverAbort(activeChar);
					break;
				case admin_disconect_all:
					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(!player.isGM())
							try
							{
								player.logout(true, false, false, true);
							}
							catch(Exception e)
							{
								System.out.println("Error while disconnect char: " + player.getName());
								e.printStackTrace();
							}
					break;
			}
		}
		catch(Exception e)
		{
			sendHtmlForm(activeChar);
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void sendHtmlForm(L2Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60;
		int m = t % 60;

		long uptime = (System.currentTimeMillis() - GameStart.serverUpTime()) / 1000;
		long mUpT = uptime/60%60;
		long sUpT = uptime%60;
		long hUpT = uptime/3600%24;
		long dUpT = uptime/3600/24;
		
		int offline = L2ObjectsStorage.getAllOfflineCount();
		int all = L2ObjectsStorage.getAllPlayersCount();
		int bot = L2ObjectsStorage.getBotPlayersCount();
		int fake = FakePlayersTable.getFakePlayersCount();

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Server Management Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<table>");
		replyMSG.append("<tr><td>Players Online: " + (all-offline-bot) + "</td></tr>");
		replyMSG.append("<tr><td>Players Offline: " + offline + "</td></tr>");
		replyMSG.append("<tr><td>Players Fake: " + fake + "</td></tr>");
		replyMSG.append("<tr><td>Players Bot: " + bot + "</td></tr>");
		replyMSG.append("<tr><td>Max Online Day: " + Util.max_online_day + "</td></tr>");
		replyMSG.append("<tr><td>Max Online: " + Util.max_online + "</td></tr>");
		replyMSG.append("<tr><td>Server Rates: " + ConfigValue.RateXp + "x, " + ConfigValue.RateSp + "x, " + ConfigValue.RateDropAdena + "x, " + ConfigValue.RateDropItems + "x</td></tr>");
		replyMSG.append("<tr><td>PlayerS Rates: " + RateService.getRateXp(activeChar) + "x, " + RateService.getRateSp(activeChar) + "x, " + RateService.getRateDropAdena(activeChar) + "x, " + RateService.getRateDropItems(activeChar) + "x</td></tr>");

		if(dUpT > 0)
			replyMSG.append("<tr><td>Server up Time: "+dUpT+"d:" +  hUpT + "h:" + mUpT + "m:" + sUpT + "s</td></tr>");
		else
			replyMSG.append("<tr><td>Server up Time: " +  hUpT + "h:" + mUpT + "m:" + sUpT + "s</td></tr>");
		replyMSG.append("<tr><td>Game Time: " + format.format(cal.getTime()) + "</td></tr>");
		replyMSG.append("<tr><td>Real Time: " + format.format(cal2.getTime()) + "</td></tr>");
		replyMSG.append("<tr></tr>");
		replyMSG.append("<tr></tr>");
		String memUsage = StatsUtil.getMemUsageAdm().toString();
		for (String line : memUsage.split("\n"))
		    replyMSG.append(line);
		replyMSG.append("<tr></tr>");
		replyMSG.append("<tr><td width=60><center><button value=\"Обновить\" action=\"bypass -h admin_server_shutdown\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td></tr>");
		replyMSG.append("<tr></tr>");
		replyMSG.append("<tr></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td>Enter in seconds the time till the server shutdowns bellow:</td></tr>");
		replyMSG.append("<br>");
		replyMSG.append("<tr><td><center>Seconds till: <edit var=\"shutdown_time\" width=60></center></td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<button value=\"Shutdown\" action=\"bypass -h admin_server_shutdown $shutdown_time\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Restart\" action=\"bypass -h admin_server_restart $shutdown_time\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Abort\" action=\"bypass -h admin_server_abort\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</td></tr></table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void serverShutdown(L2Player activeChar, int seconds, boolean restart)
	{
		Shutdown.getInstance().startShutdown(activeChar, seconds, restart);
	}

	private void serverAbort(L2Player activeChar)
	{
		Shutdown.getInstance().abort(activeChar);
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}