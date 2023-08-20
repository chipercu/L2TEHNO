package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestResetNickname extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// nothing (trigger)
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		if(activeChar.getTitleColor() != 0xFFFF77 && activeChar.getVar("TitleColor") != null)
		{
			activeChar.setTitleColor(Integer.decode("0xFFFF77"));
			activeChar.setVar("TitleColor", "0xFFFF77");
			activeChar.broadcastUserInfo(true);
		}
	}
}