package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;

public class AdminHeal implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_heal,
		admin_healthy
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Heal)
			return false;

		switch(command)
		{
			case admin_heal:
			case admin_healthy:
				if(wordList.length == 1)
					handleRes(activeChar);
				else
					handleRes(activeChar, wordList[1]);
				break;
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleRes(L2Player activeChar)
	{
		handleRes(activeChar, null);
	}

	private void handleRes(L2Player activeChar, String player)
	{

		L2Object obj = activeChar.getTarget();
		if(player != null)
		{
			L2Player plyr = L2World.getPlayer(player);

			if(plyr != null)
				obj = plyr;
			else
			{
				int radius = Math.max(Integer.parseInt(player), 100);
				for(L2Character character : activeChar.getAroundCharacters(radius, 200))
				{
					character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
					if(character.isPlayer())
						character.setCurrentCp(character.getMaxCp());
				}
				activeChar.sendMessage("Healed within " + radius + " unit radius.");
				return;
			}
		}

		if(obj == null)
			obj = activeChar;

		if(obj.isCharacter())
		{
			L2Character target = (L2Character) obj;
			target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
			if(target.isPlayer())
				target.setCurrentCp(target.getMaxCp());
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