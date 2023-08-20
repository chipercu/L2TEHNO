package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;

public class AdminPolymorph implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_polyself,
		admin_polymorph,
		admin_poly,
		admin_unpolyself,
		admin_unpolymorph,
		admin_unpoly
	}

	@SuppressWarnings("fallthrough")
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanPolymorph)
			return false;

		L2Object target = activeChar.getTarget();

		switch(command)
		{
			case admin_polyself:
				target = activeChar;
			case admin_polymorph:
			case admin_poly:
				if(target == null)
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				try
				{
					int id = Integer.parseInt(wordList[1]);
					int type = L2Object.POLY_NPC;
					if(wordList.length > 2 && (wordList[2].equalsIgnoreCase("item") || wordList[2].equalsIgnoreCase("i")))
						type = L2Object.POLY_ITEM;
					target.setPolyInfo(type, id);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("USAGE: //poly id [type:npc|item]");
					return false;
				}
				break;
			case admin_unpolyself:
				target = activeChar;
			case admin_unpolymorph:
			case admin_unpoly:
				if(target == null)
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				target.setPolyInfo(0, 0);
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