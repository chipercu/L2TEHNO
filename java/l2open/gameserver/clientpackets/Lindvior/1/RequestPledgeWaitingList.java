package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.*;

public class RequestPledgeWaitingList extends L2GameClientPacket
{
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_clanId = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (activeChar.getClanId() != _clanId))
		{
			return;
		}
		
		sendPacket(new ExPledgeWaitingList(_clanId));
	}
}