package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.LockAccountIP;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;
import l2open.util.NetList;

/**
 * @Author: SYS
 * @Date: 10/4/2008
 */
public class Lock extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private final String[] _commandList = new String[] { "lock" };

	private static String defaultPage = "data/scripts/commands/voiced/lock.html";

	public void onLoad()
	{
		if(ConfigValue.LockAccountIP)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private void showDefaultPage(L2Player activeChar)
	{
		String html = Files.read(defaultPage, activeChar);
		html = html.replaceFirst("%IP%", activeChar.getIP());
		show(html, activeChar);
	}

	public boolean useVoicedCommand(String command, L2Player activeChar, String target)
	{
		if(command.equals("lock") && (target == null || target.equals("")))
		{
			showDefaultPage(activeChar);
			return true;
		}

		String[] param = target.split(" ");

		if(param.length > 0)
		{
			int time = 60 * 60 * 24 * 7;
			String ip = activeChar.getIP();
			if(param.length > 1)
				for(int i = 1; i < param.length; i++)
					if(param[i].contains("."))
						ip = param[i];
					else
						try
						{
							time = Integer.parseInt(param[1]) * 60 * 60 * 24;
						}
						catch(NumberFormatException e)
						{}

			boolean invalid = false;
			try
			{
				NetList allowedList = new NetList();
				allowedList.LoadFromString(ip, ",");
				if(!allowedList.isIpInNets(activeChar.getIP()))
					invalid = true;
			}
			catch(Exception e)
			{
				invalid = true;
			}
			if(invalid)
			{
				activeChar.sendMessage("Invalid IP mask: you 'll be unable to login from your current address!");
				return false;
			}

			time = Math.min(time, 60 * 60 * 24 * 14);
			if(param[0].equalsIgnoreCase("on"))
			{
				LSConnection.getInstance(activeChar.getNetConnection().getLSId()).sendPacket(new LockAccountIP(activeChar.getAccountName(), ip, time));
				activeChar.sendMessage("Account locked.");
				return true;
			}

			if(param[0].equalsIgnoreCase("off"))
			{
				LSConnection.getInstance(activeChar.getNetConnection().getLSId()).sendPacket(new LockAccountIP(activeChar.getAccountName(), "*", -1));
				activeChar.sendMessage("Account unlocked.");
				return true;
			}
		}

		showDefaultPage(activeChar);
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}