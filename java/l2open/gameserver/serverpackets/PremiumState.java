package l2open.gameserver.serverpackets;

public class PremiumState extends L2GameServerPacket
{
	private int _objectId;
	private int _state;

	/** Неизвестно как работает, при получении открывается почта. */
	public PremiumState(int objectId, int state)
	{
		_objectId = objectId;
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xAA);
		writeD(_objectId);
		writeC(_state);
	}
}
