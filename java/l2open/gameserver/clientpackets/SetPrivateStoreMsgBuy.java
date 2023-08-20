package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.serverpackets.PrivateStoreMsgBuy;

public class SetPrivateStoreMsgBuy extends L2GameClientPacket
{
	private String _storename;

	@Override
	public void readImpl()
	{
		_storename = readS(32);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2TradeList tradeList = activeChar.getTradeList();
		if(tradeList != null)
		{
			tradeList.setBuyStoreName(_storename);
			sendPacket(new PrivateStoreMsgBuy(activeChar));
		}
	}
}