package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SendTradeDone;

public class RequestPrivateStoreQuitSell extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(System.currentTimeMillis() - activeChar.getLastRequestPrivateStoreQuitSellPacket() < ConfigValue.RequestPrivateStoreQuitSellPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestPrivateStoreQuitSellPacket();
		if(activeChar.getTradeList() != null)
		{
			activeChar.getTradeList().removeAll();
			activeChar.sendPacket(new SendTradeDone(0));
			activeChar.setTradeList(null);
			if(activeChar.isInTransaction())
				activeChar.getTransaction().cancel();
		}
		activeChar.setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);
		activeChar.standUp();
		activeChar.broadcastUserInfo(true);
	}
}