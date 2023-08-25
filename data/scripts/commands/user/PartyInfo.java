package commands.user;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * Support for /partyinfo command
 */
public class PartyInfo implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 81 };

	public boolean useUserCommand(int id, L2Player activeChar)
	{
		if(id != COMMAND_IDS[0])
			return false;

		L2Party playerParty = activeChar.getParty();
		if(!activeChar.isInParty())
			return false;

		L2Player partyLeader = playerParty.getPartyLeader();
		if(partyLeader == null)
			return false;

		int memberCount = playerParty.getMemberCount();
		int lootDistribution = playerParty.getLootDistribution();

		activeChar.sendPacket(Msg._PARTY_INFORMATION_);

		switch(lootDistribution)
		{
			case L2Party.ITEM_LOOTER:
				activeChar.sendPacket(Msg.LOOTING_METHOD_FINDERS_KEEPERS);
				break;
			case L2Party.ITEM_ORDER:
				activeChar.sendPacket(Msg.LOOTING_METHOD_BY_TURN);
				break;
			case L2Party.ITEM_ORDER_SPOIL:
				activeChar.sendPacket(Msg.LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL);
				break;
			case L2Party.ITEM_RANDOM:
				activeChar.sendPacket(Msg.LOOTING_METHOD_RANDOM);
				break;
			case L2Party.ITEM_RANDOM_SPOIL:
				activeChar.sendPacket(Msg.LOOTING_METHOD_RANDOM_INCLUDING_SPOIL);
				break;
		}

		activeChar.sendPacket(new SystemMessage(SystemMessage.PARTY_LEADER_S1).addString(partyLeader.getName()), new SystemMessage("scripts.commands.user.PartyInfo.Members", activeChar, new Integer(memberCount)), Msg.__DASHES__);
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