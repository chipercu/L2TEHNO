package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ItemList;

public class RequestItemList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || !activeChar.getPlayerAccess().UseInventory || activeChar.isInventoryDisabled() || activeChar.is_block)
			return;
		sendPacket(new ItemList(activeChar, true));
	}
}