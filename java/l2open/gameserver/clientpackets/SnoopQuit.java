package l2open.gameserver.clientpackets;

import l2open.gameserver.model.*;

public class SnoopQuit extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _snoopID;

	/**
	 * format: cd
	 */
	@Override
	public void readImpl()
	{
		_snoopID = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player player = L2ObjectsStorage.getPlayer(_snoopID);
		if(player == null)
			return;
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		player.removeSnooper(activeChar);
		activeChar.removeSnooped(player);
	}
}