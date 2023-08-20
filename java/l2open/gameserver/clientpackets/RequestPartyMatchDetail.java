package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.PartyRoom;

public class RequestPartyMatchDetail extends L2GameClientPacket
{
	private int _roomId;
	private int _mode;
	private int _level;
	@SuppressWarnings("unused")
	private int _unk;

	/**
	 * Format: dddd
	 */
	@Override
	public void readImpl()
	{
		_roomId = readD(); // room id, если 0 то autojoin
		_mode = readD(); // location
		_level = readD(); // 1 - all, 0 - my level (только при autojoin)
		_unk = readD(); //Unknown всегда 0 ??

		/*	Near Me - (-2)
		All - (-1)
		Talking Island - 1
		Gludio - 2
		Dark Elven Ter. - 3
		Elven Territory - 4
		Dion - 5
		Giran - 6
		Neutral Zone - 7
		Schuttgart - 9
		Oren - 10
		Hunters Village - 11
		Innadril - 12
		Aden - 13
		Rune - 14
		Goddard - 15
		Change - 100 ???? (возможно room id)*/
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_roomId > 0)
			PartyRoomManager.getInstance().joinPartyRoom(activeChar, _roomId);
		else
			for(PartyRoom room : PartyRoomManager.getInstance().getRooms(_mode, _level, activeChar))
				if(room.getMembersSize() < room.getMaxMembers())
					PartyRoomManager.getInstance().joinPartyRoom(activeChar, room.getId());
	}
}