package communityboard;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.LockAccountIP;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;
import l2open.util.NetList;
import l2open.util.Util;

public class Security extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private final String[] _commandList = new String[] { "security", "lock_hwid", "unlock_hwid", "show_ip", "lock_ip", "unlock_ip" };

	public static String defaultPage = "data/scripts/commands/voiced/security.htm";
	public static String ipPage = "data/scripts/commands/voiced/security_ip.htm";

	private static Security _instance;

	public static Security getInstance()
	{
		if(_instance == null)
			_instance = new Security();
		return _instance;
	}

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void showDefaultPage(L2Player player, String page)
	{
		String html = Files.read(page, player);
		html = html.replaceFirst("%ip%", player.getIP());
		show(html, player);
	}

	public boolean useVoicedCommand(String command, L2Player player, String target)
	{
		command = command.intern();
		if(command.equalsIgnoreCase("security"))
		{
			showDefaultPage(player, defaultPage);
			return true;
		}
		if(command.equalsIgnoreCase("lock_hwid"))
		{
			if(ConfigValue.AccHwidLockEnable && ConfigValue.ProtectEnable)
			{
				if(player.getAccLock() != null && Util.contains(player.getAccLock(), player.getHWIDs()))
				{
					player.sendMessage("Ваш аккаунт, уже привязан к данному компьютеру.");
					return true;
				}
				else if(DifferentMethods.getPay(player, ConfigValue.AccHwidLockPriceId, ConfigValue.AccHwidLockPriceCount, true))
				{
					player.addAccLock(player.getHWIDs());
					player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.hwid.lock", player));
				}
			}
			else
				player.sendMessage(new CustomMessage("scripts.services.off", player));

			return true;
		}
		else if(command.equalsIgnoreCase("unlock_hwid") && player.getAccLock() != null && Util.contains(player.getAccLock(), player.getHWIDs()))
		{
			if(ConfigValue.AccHwidLockEnable && ConfigValue.ProtectEnable)
			{
				player.removeAccLock(player.getHWIDs());
				player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.hwid.unlock", player));
				return true;
			}
			else
				player.sendMessage(new CustomMessage("scripts.services.off", player));
		}
		else if(command.equalsIgnoreCase("show_ip"))
		{
			showDefaultPage(player, ipPage);
			return true;
		}
		else if(command.equalsIgnoreCase("unlock_ip"))
		{
			if(ConfigValue.LockAccountIP)
			{
				LSConnection.getInstance(player.getNetConnection().getLSId()).sendPacket(new LockAccountIP(player.getAccountName(), "*", -1));
				player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.ip.unlock", player));
				showDefaultPage(player, defaultPage);
				return true;
			}
			else
				return false;
		}
		else if(command.equals("lock_ip") || command.startsWith("lock_ip"))
		{

			if(ConfigValue.LockAccountIP)
			{
				int days = 7;
				int time = 60 * 60 * 24 * 7;
				String ip = player.getIP();

				String[] param = target.split(" ");
				if(param.length > 0)
				{
					if(param[0].contains("."))
						ip = param[0];
					else
					{
						try
						{
							days = Integer.parseInt(param[0]);
							days = days > 14 ? 14 : days < 7 ? 7 : days;
							time = days * 60 * 60 * 24;
						}
						catch(NumberFormatException e)
						{}
					}
				}

				boolean invalid = false;
				if(param.length > 1)
				{
					if(param[1].contains("."))
						ip = param[1];
					else
						invalid = true;
				}

				if(!invalid)
				{
					try
					{
						NetList allowedList = new NetList();
						allowedList.LoadFromString(ip, ",");
						if(!allowedList.isIpInNets(player.getIP()))
							invalid = true;
					}
					catch(Exception e)
					{
						invalid = true;
					}
				}

				if(invalid)
				{
					player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.ip.error", player));
					return false;
				}

				time = Math.min(time, 60 * 60 * 24 * 14);
				LSConnection.getInstance(player.getNetConnection().getLSId()).sendPacket(new LockAccountIP(player.getAccountName(), ip, time));
				player.sendMessage(new CustomMessage("scripts.commands.voiced.Security.ip.lock", player).addString(player.getIP()).addNumber(days).addString(DifferentMethods.declension(player, days, "Days")));
				showDefaultPage(player, defaultPage);
			}
			else
				player.sendMessage(new CustomMessage("scripts.services.off", player));

			return true;
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}