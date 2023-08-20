package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.tables.PetDataTable;

public class AdminRide implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_ride, //
		admin_ride_radius, //
		admin_ride_wyvern, //
		admin_ride_wyvern_radius,
		admin_ride_strider, //
		admin_ride_strider_radius,
		admin_unride, //
		admin_unride_radius, //
		admin_wr, //
		admin_wr_radius, //
		admin_sr, //
		admin_sr_radius, //
		admin_ur, //
		admin_ur_radius, //
		admin_ml, // 
		admin_msu, //
		admin_dc, // 
		admin_cs
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		final Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Rider)
			return false;

		L2Object target = activeChar.getTarget();

		if(target == null || !target.isPlayer())
			target = activeChar;

		switch(command)
		{
			case admin_ride:
			{
				if(((L2Player) target).isMounted() || ((L2Player) target).getPet() != null)
				{
					activeChar.sendMessage("Already Have a Pet or Mounted.");
					return false;
				}
				if(wordList.length != 2)
				{
					activeChar.sendMessage("USAGE: //ride <int id>");
					return false;
				}
				((L2Player) target).setMount(Integer.parseInt(wordList[1]), 0, 0);
				break;
			}
			case admin_ride_wyvern:
			case admin_wr:
			{
				if(((L2Player) target).isMounted() || ((L2Player) target).getPet() != null)
				{
					activeChar.sendMessage("Already Have a Pet or Mounted.");
					return false;
				}
				((L2Player) target).setMount(PetDataTable.WYVERN_ID, 0, 0);
				break;
			}
			case admin_ride_strider:
			case admin_sr:
			{
				if(((L2Player) target).isMounted() || ((L2Player) target).getPet() != null)
				{
					activeChar.sendMessage("Already Have a Pet or Mounted.");
					return false;
				}
				((L2Player) target).setMount(PetDataTable.STRIDER_WIND_ID, 0, 0);
				break;
			}
			case admin_unride:
			case admin_ur:
			{
				((L2Player) target).setMount(0, 0, 0);
				break;
			}
			case admin_ride_radius:
			{
				final int radius = Integer.parseInt(wordList[2]);
				for(final L2Object element : L2World.getAroundPlayers(activeChar, radius, 200))
				{
					if(((L2Player) element).isMounted() || ((L2Player) element).getPet() != null)
					{
						activeChar.sendMessage("Already Have a Pet or Mounted.");
						return false;
					}
					if(wordList.length != 3)
					{
						activeChar.sendMessage("USAGE: //ride <int id> <int radius>");
						return false;
					}
					((L2Player) element).setMount(Integer.parseInt(wordList[1]), 0, 0);
				}
				break;
			}
			case admin_unride_radius:
			case admin_ur_radius:
			{
				if(wordList.length != 2)
				{
					activeChar.sendMessage("USAGE: //ur <int radius>");
					return false;
				}
				final int radius = Integer.parseInt(wordList[1]);
				for(final L2Object element : L2World.getAroundPlayers(activeChar, radius, 200))
				{
					((L2Player) element).setMount(0, 0, 0);
				}
				break;
			}
			case admin_ride_strider_radius:
			case admin_sr_radius:
			{
				final int radius = Integer.parseInt(wordList[1]);
				for(final L2Object element : L2World.getAroundPlayers(activeChar, radius, 200))
				{
					if(wordList.length != 2)
					{
						activeChar.sendMessage("USAGE: //sr <int radius>");
						return false;
					}
					if(((L2Player) element).isMounted() || ((L2Player) element).getPet() != null)
					{
						activeChar.sendMessage("Already Have a Pet or Mounted.");
						return false;
					}
					((L2Player) element).setMount(PetDataTable.STRIDER_WIND_ID, 0, 0);
				}
				break;
			}
			case admin_ride_wyvern_radius:
			case admin_wr_radius:
			{
				final int radius = Integer.parseInt(wordList[1]);
				for(final L2Object element : L2World.getAroundPlayers(activeChar, radius, 200))
				{
					if(wordList.length != 2)
					{
						activeChar.sendMessage("USAGE: //wr <int radius>");
						return false;
					}
					if(((L2Player) element).isMounted() || ((L2Player) element).getPet() != null)
					{
						activeChar.sendMessage("Already Have a Pet or Mounted.");
						return false;
					}
					((L2Player) element).setMount(PetDataTable.WYVERN_ID, 0, 0);
				}
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