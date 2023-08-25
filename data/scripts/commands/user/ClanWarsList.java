package commands.user;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.GArray;

/**
 * Support for /attacklist /underattacklist /warlist commands
 */
public class ClanWarsList implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 88, 89, 90 };

	public boolean useUserCommand(int id, L2Player activeChar)
	{
		if(id != COMMAND_IDS[0] && id != COMMAND_IDS[1] && id != COMMAND_IDS[2])
			return false;

		L2Clan clan = activeChar.getClan();
		if(clan == null)
		{
			activeChar.sendPacket(Msg.NOT_JOINED_IN_ANY_CLAN);
			return false;
		}

		SystemMessage sm;
		GArray<L2Clan> data = new GArray<L2Clan>();
		if(id == 88)
		{
			// attack list
			activeChar.sendPacket(Msg._ATTACK_LIST_);
			data = clan.getEnemyClans();
		}
		else if(id == 89)
		{
			// under attack list
			activeChar.sendPacket(Msg._UNDER_ATTACK_LIST_);
			data = clan.getAttackerClans();
		}
		else
		// id = 90
		{
			// war list
			activeChar.sendPacket(Msg._WAR_LIST_);
			for(L2Clan c : clan.getEnemyClans())
				if(clan.getAttackerClans().contains(c))
					data.add(c);
		}

		for(L2Clan c : data)
		{
			String clanName = c.getName();
			int ally_id = c.getAllyId();
			if(ally_id > 0)
				sm = new SystemMessage(SystemMessage.S1_S2_ALLIANCE).addString(clanName).addString(c.getAlliance().getAllyName());
			else
				sm = new SystemMessage(SystemMessage.S1_NO_ALLIANCE_EXISTS).addString(clanName);
			activeChar.sendPacket(sm);
		}

		activeChar.sendPacket(Msg.__EQUALS__);
		return true;
	}

	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}

	public void onLoad()
	{
		UserCommandHandler.getInstance().registerUserCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}