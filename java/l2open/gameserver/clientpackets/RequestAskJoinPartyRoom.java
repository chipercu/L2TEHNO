package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.PartyRoom;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.serverpackets.ExAskJoinPartyRoom;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * format: (ch)S
 */
public class RequestAskJoinPartyRoom extends L2GameClientPacket
{
	private String _name; // not tested, just guessed

	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		L2Player targetChar = L2World.getPlayer(_name);

		if(targetChar == null || targetChar == activeChar)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getPartyRoom() <= 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInTransaction())
		{
			activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		if(targetChar.isInTransaction())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(targetChar.getName()));
			return;
		}

		if(targetChar.getPartyRoom() > 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addString(targetChar.getName()));
			return;
		}

		PartyRoom room = PartyRoomManager.getInstance().getRooms().get(activeChar.getPartyRoom());
		if(room == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(room.getMembersSize() >= room.getMaxMembers())
		{
			activeChar.sendPacket(Msg.PARTY_IS_FULL);
			return;
		}

		if(!PartyRoomManager.getInstance().isLeader(activeChar))
		{
			activeChar.sendPacket(Msg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}

		new Transaction(TransactionType.PARTY_ROOM, activeChar, targetChar, 10000);

		targetChar.sendPacket(new ExAskJoinPartyRoom(activeChar.getName()));
		activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HAS_INVITED_YOU_TO_ENTER_THE_PARTY_ROOM).addString(targetChar.getName()));
	}
}