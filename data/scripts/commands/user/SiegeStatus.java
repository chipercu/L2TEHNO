package commands.user;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SystemMessage;

public class SiegeStatus implements IUserCommandHandler, ScriptFile
{
	public static final int[] COMMAND_IDS = { 99 };

	public boolean useUserCommand(int id, L2Player player)
	{
		if(COMMAND_IDS[0] != id)
			return false;
			
		if(!player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS));
			return false;
		}

		Castle castle = player.getCastle();
		if(castle == null)
			return false;

		if(castle.getSiege().isInProgress())
			if(!player.isNoble())
			{
				player.sendPacket(new SystemMessage(SystemMessage.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR));
				return false;
			}

		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setFile("data/scripts/commands/user/siege_status.htm");
		msg.replace("%name%", player.getName());
		msg.replace("%kills%", String.valueOf(0));
		msg.replace("%deaths%", String.valueOf(0));
		msg.replace("%type%", String.valueOf(0));

		player.sendPacket(msg);
		return true;
	}

	public final int[] getUserCommandList()
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
