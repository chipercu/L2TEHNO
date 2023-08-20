package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.util.Log;

public class AdminCancel implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_cancelid,
		admin_cancel
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		switch(command)
		{
			case admin_cancelid:
				L2Object obj = activeChar.getTarget();
				
				if(obj == null)
					obj = activeChar;
				if(obj.isCharacter() && wordList.length > 1)
				{
					((L2Character) obj).getEffectList().stopEffect(Integer.parseInt(wordList[1]));
					Log.LogCommand(activeChar, Log.Adm_DelSkill, "admin_cancel", 1);
				}
				else
					activeChar.sendPacket(Msg.INVALID_TARGET);
				break;
			case admin_cancel:
				handleCancel(activeChar, wordList.length > 1 ? wordList[1] : null);
				break;
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleCancel(L2Player activeChar, String targetName)
	{
		L2Object obj = activeChar.getTarget();
		if(targetName != null)
		{
			L2Player plyr = L2World.getPlayer(targetName);
			if(plyr != null)
				obj = plyr;
			else
				try
				{
					int radius = Math.max(Integer.parseInt(targetName), 100);
					for(L2Character character : activeChar.getAroundCharacters(radius, 200))
						character.getEffectList().stopAllEffects();
					activeChar.sendMessage("Apply Cancel within " + radius + " unit radius.");
					return;
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Enter valid player name or radius");
					return;
				}
		}

		if(obj == null)
			obj = activeChar;
		if(obj.isCharacter())
		{
			((L2Character) obj).getEffectList().stopAllEffects();
			Log.LogCommand(activeChar, Log.Adm_DelSkill, "admin_cancel", 1);
		}
		else
			activeChar.sendPacket(Msg.INVALID_TARGET);
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