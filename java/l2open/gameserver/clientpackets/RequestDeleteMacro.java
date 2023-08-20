package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestDeleteMacro extends L2GameClientPacket
{
	private int _id;

	/**
	 * format:		cd
	 */
	@Override
	public void readImpl()
	{
		_id = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;
		activeChar.deleteMacro(_id);
	}
}