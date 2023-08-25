package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExListPartyMatchingWaitingRoom;

import java.util.logging.Logger;

/** 
 * Format: dddd
 */
public class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket
{
	protected static final Logger _log = Logger.getLogger(RequestListPartyMatchingWaitingRoom.class.getName());

	@SuppressWarnings("unused")
	private int _minLevel, _maxLevel, _page, _unk;

	@Override
	protected void readImpl()
	{
		_page = readD();
		_minLevel = readD();
		_maxLevel = readD();
		_unk = readD(); // всегда 1?
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _minLevel, _maxLevel, _page));
	}
}