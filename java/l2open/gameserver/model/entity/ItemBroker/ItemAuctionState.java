package l2open.gameserver.model.entity.ItemBroker;

public enum ItemAuctionState
{
	CREATED((byte)0),
	STARTED((byte)1),
	FINISHED((byte)2);

	private final byte _stateId;

	private ItemAuctionState(byte stateId)
	{
		_stateId = stateId;
	}

	public byte getStateId()
	{
		return _stateId;
	}

	public static ItemAuctionState stateForStateId(byte stateId)
	{
		for (ItemAuctionState state : values())
		{
			if (state._stateId == stateId)
				return state;
		}
		return null;
	}
}
