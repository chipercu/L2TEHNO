package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.CameraMode;
import l2open.gameserver.serverpackets.SpecialCamera;

public class AdminCamera implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_freelook,
		admin_cinematic,
		admin_scene
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		switch(command)
		{
			case admin_freelook:
			{
				if(fullString.length() > 15)
					fullString = fullString.substring(15);
				else
				{
					activeChar.sendMessage("Usage: //freelook 1 or //freelook 0");
					return false;
				}

				int mode = Integer.parseInt(fullString);
				if(mode == 1)
				{
					activeChar.setInvisible(true);
					activeChar.setIsInvul(true);
					activeChar.setNoChannel(-1);
					activeChar.setFlying(true);
				}
				else
				{
					activeChar.setInvisible(false);
					activeChar.setIsInvul(false);
					activeChar.setNoChannel(0);
					activeChar.setFlying(false);
					activeChar.broadcastRelationChanged();
				}
				activeChar.sendPacket(new CameraMode(mode));

				break;
			}
			case admin_cinematic:
			{
				/*int id = Integer.parseInt(wordList[1]);
				int dist = Integer.parseInt(wordList[2]);
				int yaw = Integer.parseInt(wordList[3]);
				int pitch = Integer.parseInt(wordList[4]);
				int time = Integer.parseInt(wordList[5]);
				int duration = Integer.parseInt(wordList[6]);
				activeChar.sendPacket(new SpecialCamera(id, dist, yaw, pitch, time, duration));
				*/
				activeChar.sendMessage("This command don't work.");
				break;
			}
			case admin_scene:
			{
				int id = Integer.parseInt(wordList[1]);
				activeChar.showQuestMovie(id);
				break;
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