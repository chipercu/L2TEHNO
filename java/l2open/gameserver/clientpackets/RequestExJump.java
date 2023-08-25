package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExJumpToLocation;

public class RequestExJump extends L2GameClientPacket
{
	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		sendPacket(new ExJumpToLocation(activeChar.getObjectId(), activeChar.getLoc(), activeChar.getLoc()));
		_log.info(getType());
	}

	@Override
	public void readImpl()
	{}
}