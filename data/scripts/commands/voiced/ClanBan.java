package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.util.*;
/**
 * @author: Diagod
 * open-team.ru
 **/
// 2. .clanblock NICK 0-240, позволяющая заблокировать клан чат определенному игроку на 0-240 минут. Доступна только Лидеру клана.
public class ClanBan extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "clanblock"};

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.startsWith("clanblock"))
		{
			if(!activeChar.isClanLeader())
			{
				activeChar.sendMessage("У вас нет прав для использования команды .clanblock");
				return false;
			}
			int ban_time = 240;

			L2Player target = null;
			if(!args.isEmpty())
			{
				String[] a = args.trim().split(" ");
				String player_name = a[0];
				try
				{
					ban_time = Math.min(240, Integer.parseInt(a[1]));
				}
				catch(Exception e)
				{}
				target = L2World.getPlayer(player_name);
			}
			else
				activeChar.sendMessage(".clanblock имя время(в минутах, макс 240)");
			if(target != null)
				target.setVar("ClanChatBan",String.valueOf(System.currentTimeMillis()+ban_time*60*1000L));
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}