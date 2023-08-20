package commands.admin;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;

public class AdminDelete implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_delete
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_delete:
				L2Object obj = activeChar.getTarget();
				if(obj != null && obj.isNpc())
				{
					L2NpcInstance target = (L2NpcInstance) obj;
					L2Spawn spawn = target.getSpawn();
					if(spawn != null)
						spawn.stopRespawn();
					if(ConfigValue.saveAdminDeSpawn)
						deleteSpawn(spawn);
					target.deleteMe();
				}
				else
					activeChar.sendPacket(Msg.INVALID_TARGET);
				break;
		}

		return true;
	}

	public void deleteSpawn(L2Spawn spawn)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM `spawnlist` WHERE `npc_templateid`=? AND `locx`=? AND `locy`=? AND `locz`=? AND `loc_id`=?");
			statement.setInt(1, spawn.getNpcId());
			statement.setInt(2, spawn.getLocx());
			statement.setInt(3, spawn.getLocy());
			statement.setInt(4, spawn.getLocz());
			statement.setInt(5, spawn.getLocation());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("spawn couldnt be deleted in db:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}	
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