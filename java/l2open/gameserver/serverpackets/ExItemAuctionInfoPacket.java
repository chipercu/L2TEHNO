package l2open.gameserver.serverpackets;

import l2open.gameserver.model.entity.ItemBroker.ItemAuction;
import l2open.gameserver.model.entity.ItemBroker.ItemAuctionBid;
import l2open.gameserver.model.entity.ItemBroker.ItemAuctionState;
import l2open.gameserver.model.items.L2ItemInstance;

public final class ExItemAuctionInfoPacket extends L2GameServerPacket
{
	private final boolean _refresh;
	private final int _timeRemaining;
	private final ItemAuction _currentAuction;
	private final ItemAuction _nextAuction;

	public ExItemAuctionInfoPacket(final boolean refresh, final ItemAuction currentAuction, final ItemAuction nextAuction)
	{
		if (currentAuction == null)
			throw new NullPointerException();

		if (currentAuction.getAuctionState() != ItemAuctionState.STARTED)
			_timeRemaining = 0;
		else
			_timeRemaining = (int) (currentAuction.getFinishingTimeRemaining() / 1000); // in seconds

		_refresh = refresh;
		_currentAuction = currentAuction;
		_nextAuction = nextAuction;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(getClient().isLindvior() ? 0x69 : 0x68);
		writeC(_refresh ? 0x00 : 0x01);
		writeD(_currentAuction.getInstanceId());

		final ItemAuctionBid highestBid = _currentAuction.getHighestBid();
		writeQ(highestBid != null ? highestBid.getLastBid() : _currentAuction.getAuctionInitBid());

		writeD(_timeRemaining);
		writeItemInfo(_currentAuction.getItem());

		if (_nextAuction != null)
		{
			writeQ(_nextAuction.getAuctionInitBid());
			writeD((int) (_nextAuction.getStartingTime() / 1000)); // unix time in seconds
			writeItemInfo(_nextAuction.getItem());
		}
	}
}
