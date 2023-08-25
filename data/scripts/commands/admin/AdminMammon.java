package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.GArray;

@SuppressWarnings("unused")
public class AdminMammon implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_find_mammon,
		admin_show_mammon,
		admin_hide_mammon,
		admin_list_spawns
	}

	GArray<Integer> npcIds = new GArray<Integer>();

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		npcIds.clear();

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		else if(fullString.startsWith("admin_find_mammon"))
		{
			npcIds.add(31113);
			npcIds.add(31126);
			npcIds.add(31092); // Add the Marketeer of Mammon also
			int teleportIndex = -1;

			try
			{
				if(fullString.length() > 16)
					teleportIndex = Integer.parseInt(fullString.substring(18));
			}
			catch(Exception NumberFormatException)
			{
				// activeChar.sendPacket(SystemMessage.sendString("Command format is
				// //find_mammon <teleportIndex>"));
			}

			findAdminNPCs(activeChar, npcIds, teleportIndex, -1);
		}

		else if(fullString.equals("admin_show_mammon"))
		{
			npcIds.add(31113);
			npcIds.add(31126);

			findAdminNPCs(activeChar, npcIds, -1, 1);
		}

		else if(fullString.equals("admin_hide_mammon"))
		{
			npcIds.add(31113);
			npcIds.add(31126);

			findAdminNPCs(activeChar, npcIds, -1, 0);
		}

		else if(fullString.startsWith("admin_list_spawns"))
		{
			int npcId = 0;

			try
			{
				npcId = Integer.parseInt(fullString.substring(18).trim());
			}
			catch(Exception NumberFormatException)
			{
				activeChar.sendMessage("Command format is //list_spawns <NPC_ID>");
			}

			npcIds.add(npcId);
			findAdminNPCs(activeChar, npcIds, -1, -1);
		}

		// Used for testing SystemMessage IDs - Use //msg <ID>
		else if(fullString.startsWith("admin_msg"))
			activeChar.sendPacket(new SystemMessage(Integer.parseInt(fullString.substring(10).trim())));

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void findAdminNPCs(L2Player activeChar, GArray<Integer> npcIdList, int teleportIndex, int makeVisible)
	{
		int index = 0;

		for(L2NpcInstance npcInst : L2ObjectsStorage.getAllNpcs())
		{
			int npcId = npcInst.getNpcId();
			if(npcIdList.contains(npcId))
			{
				if(makeVisible == 1)
					npcInst.spawnMe();
				else if(makeVisible == 0)
					npcInst.decayMe();

				if(npcInst.isVisible())
				{
					index++;

					if(teleportIndex > -1)
					{
						if(teleportIndex == index)
							activeChar.teleToLocation(npcInst.getLoc());
					}
					else
						activeChar.sendMessage(index + " - " + npcInst.getName() + " (" + npcInst.getObjectId() + "): " + npcInst.getX() + " " + npcInst.getY() + " " + npcInst.getZ());
				}
			}
		}
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