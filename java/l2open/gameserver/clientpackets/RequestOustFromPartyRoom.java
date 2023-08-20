package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.PartyRoom;

/**
 * format (ch) d
 */
public class RequestOustFromPartyRoom extends L2GameClientPacket
{
	private int _id;

	@Override
	public void readImpl()
	{
		_id = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		L2Player member = L2ObjectsStorage.getPlayer(_id);
		if(activeChar == null || member == null || activeChar.is_block)
			return;

		PartyRoom room = PartyRoomManager.getInstance().getRoom(member.getPartyRoom());
		if(room != null)
			room.removeMember(member, true);
	}
}