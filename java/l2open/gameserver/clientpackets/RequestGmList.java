package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.GmListTable;
import l2open.util.Log;

public class RequestGmList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
		{
			GmListTable.sendListToPlayer(activeChar);
			Log.LogCommand(activeChar, 2, getType(), 1);
		}
	}
}