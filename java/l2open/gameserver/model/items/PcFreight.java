package l2open.gameserver.model.items;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;

public class PcFreight extends Warehouse
{
	private final L2Player _owner;
	private final int ownerObjectId;

	public PcFreight(L2Player owner)
	{
		_owner = owner;
		ownerObjectId = owner.getObjectId();
	}

	public PcFreight(int id)
	{
		_owner = null;
		ownerObjectId = id;
	}

	public L2Player getOwner()
	{
		return _owner;
	}

	@Override
	public int getOwnerId()
	{
		return ownerObjectId;
	}

	@Override
	public ItemLocation getLocationType()
	{
		return ItemLocation.FREIGHT;
	}
}