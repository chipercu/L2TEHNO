package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.ItemAuctionManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.ItemBroker.ItemAuction;
import l2open.gameserver.model.entity.ItemBroker.ItemAuctionInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExItemAuctionInfoPacket;

public class RequestInfoItemAuction extends L2GameClientPacket
{
	private int _instanceId;

	@Override
	protected final void readImpl()
	{
		_instanceId = readD();
	}

	@Override
	protected final void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.getAndSetLastItemAuctionRequest();

		final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
		if(instance == null)
			return;

		final ItemAuction auction = instance.getCurrentAuction();
		L2NpcInstance broker = activeChar.getLastNpc();
		if(auction == null || broker == null || broker.getNpcId() != _instanceId || activeChar.getDistance(broker.getX(), broker.getY()) > broker.INTERACTION_DISTANCE+broker.BYPASS_DISTANCE_ADD)
			return;

		activeChar.sendPacket(new ExItemAuctionInfoPacket(true, auction, instance.getNextAuction()));
	}
}