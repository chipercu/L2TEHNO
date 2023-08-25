package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.tables.GmListTable;

public class AdminGmChat implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_q,
		admin_gmchat,
		admin_snoop
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanAnnounce)
			return false;

		switch(command)
		{
			case admin_q:
				try
				{
					String text = fullString.replaceFirst(Commands.admin_q.name(), "");
					Say2 cs = new Say2(0, Integer.parseInt(text.trim()), activeChar.getName(), "Test: "+Integer.parseInt(text.trim()));
					activeChar.sendPacket(cs);
				}
				catch(StringIndexOutOfBoundsException e)
				{}
				break;
			case admin_gmchat:
				try
				{
					String text = fullString.replaceFirst(Commands.admin_gmchat.name(), "");
					Say2 cs = new Say2(0, 9, activeChar.getName(), text);
					GmListTable.broadcastToGMs(cs);
				}
				catch(StringIndexOutOfBoundsException e)
				{}
				break;
			case admin_snoop:
			{
				L2Object target = activeChar.getTarget();

				if(wordList.length > 1)
				{
					target = L2ObjectsStorage.getPlayer(wordList[1]);
				}
				if(target == null)
				{
					activeChar.sendMessage("Вы должны взять игрока в таргет или ввести его имя.");
					return false;
				}
				if(!target.isPlayer())
				{
					activeChar.sendMessage("Выберите игрока.");
					return false;
				}
				L2Player player = (L2Player) target;
				player.addSnooper(activeChar);
				activeChar.addSnooped(player);
			}
		}
		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
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