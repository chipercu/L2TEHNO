package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ShowMiniMap;

public class RequestShowMiniMap extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
			sendPacket(new ShowMiniMap(activeChar, 1665));
	}
}