package l2open.gameserver.clientpackets;

import l2open.gameserver.serverpackets.ExSendManorList;

/**
 * Format: ch
 * c (id) 0xD0
 * h (subid) 0x01
 *
 */
public class RequestManorList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(new ExSendManorList());
	}
}