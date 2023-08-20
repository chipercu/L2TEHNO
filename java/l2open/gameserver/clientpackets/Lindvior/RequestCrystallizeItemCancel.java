package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestCrystallizeItemCancel extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//TODO
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		activeChar.sendActionFailed();
	}
}
