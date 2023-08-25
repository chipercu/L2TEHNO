package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.ItemAuctionManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.ItemBroker.ItemAuction;
import l2open.gameserver.model.entity.ItemBroker.ItemAuctionInstance;

public class RequestBidItemAuction extends L2GameClientPacket
{
	private int _instanceId;
	private long _bid;

	protected void runImpl()
	{
		L2Player activeChar = (getClient()).getActiveChar();

		if(activeChar == null)
		{
			return;
		}
		if(_bid < 0 || (_bid > Long.MAX_VALUE))
		{
			return;
		}
		ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
		if(instance == null)
			return;

		ItemAuction auction = instance.getCurrentAuction();
		if(auction != null)
			auction.registerBid(activeChar, _bid);
	}

	protected void readImpl()
	{
		_instanceId = super.readD();
		_bid = super.readQ();
	}
}