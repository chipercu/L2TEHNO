package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.tables.DoorTable;

public class AdminDoorControl implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_open,
		admin_close,
		admin_openall,
		admin_closeall
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Door)
			return false;

		switch(command)
		{
			case admin_open:
				if(wordList.length > 1)
				{
					//System.out.println(fullString);
					DoorTable.getInstance().getDoor(Integer.parseInt(wordList[1])).openMe();
				}
				else
				{
					L2Object target = activeChar.getTarget();
					if(target.isDoor())
					{
						/*System.out.println("getObjectId: "+((L2DoorInstance) target).getObjectId());
						System.out.println("getDoorId: "+((L2DoorInstance) target).getDoorId());
						System.out.println("isOpen: "+(((L2DoorInstance) target).isOpen() ? "Open" : "Close"));
						System.out.println("geoOpen: "+(((L2DoorInstance) target).geoOpen ? "Geo Open" : "Geo Close"));*/
						((L2DoorInstance) target).openMe();
					}
					else
						activeChar.sendPacket(Msg.INVALID_TARGET);
				}
				break;
			case admin_close:
				if(wordList.length > 1)
					DoorTable.getInstance().getDoor(Integer.parseInt(wordList[1])).closeMe();
				else
				{
					L2Object target = activeChar.getTarget();
					if(target.isDoor())
					{
						/*System.out.println("getObjectId: "+((L2DoorInstance) target).getObjectId());
						System.out.println("getDoorId: "+((L2DoorInstance) target).getDoorId());
						System.out.println("isOpen: "+(((L2DoorInstance) target).isOpen() ? "Open" : "Close"));
						System.out.println("geoOpen: "+(((L2DoorInstance) target).geoOpen ? "Geo Open" : "Geo Close"));*/
						((L2DoorInstance) target).closeMe();
					}
					else
						activeChar.sendPacket(Msg.INVALID_TARGET);
				}
				break;
			case admin_closeall:
				for(L2DoorInstance door : DoorTable.getInstance().getDoors())
					door.closeMe();
				break;
			case admin_openall:
				for(L2DoorInstance door : DoorTable.getInstance().getDoors())
					door.openMe();
				break;
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