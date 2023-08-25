package l2open.gameserver.model.items;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;

public final class ClanWarehouse extends Warehouse
{
	private L2Clan _clan;

	public ClanWarehouse(L2Clan clan)
	{
		_clan = clan;
	}

	@Override
	public int getOwnerId()
	{
		return _clan.getClanId();
	}

	@Override
	public ItemLocation getLocationType()
	{
		return ItemLocation.CLANWH;
	}
}