package l2open.gameserver.serverpackets;

import java.util.logging.Logger;

/**
 * Format: ch S
 */
public class ExAskJoinPartyRoom extends L2GameServerPacket
{
	protected static final Logger _log = Logger.getLogger(ExAskJoinPartyRoom.class.getName());
	private String _charName;

	public ExAskJoinPartyRoom(String charName)
	{
		_charName = charName;
		//_log.info("ExAskJoinPartyRoom _charName: " + _charName);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x35);
		writeS(_charName);
	}
}