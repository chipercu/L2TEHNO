package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestShortCutDel extends L2GameClientPacket
{
	private int _slot;
	private int _page;

	/**
	 * packet type id 0x3F
	 * format:		cd
	 */
	@Override
	public void readImpl()
	{
		int id = readD();
		_slot = id % 12;
		_page = id / 12;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		// client dont needs confirmation. this packet is just to inform the server
		activeChar.deleteShortCut(_slot, _page);
	}
}