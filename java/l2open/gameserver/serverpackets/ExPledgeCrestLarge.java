package l2open.gameserver.serverpackets;

public class ExPledgeCrestLarge extends L2GameServerPacket
{
	private int _crestId;
	private int _i;
	private byte[] _data;

	public ExPledgeCrestLarge(int crestId, byte[] data, int i)
	{
		_crestId = crestId;
		_data = data;
		_i = i;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x1b);

		writeD(0x00);
		writeD(_crestId);

		if(getClient().isLindvior())
		{
			writeD(_i); // split number
			writeD(65664); // total size
		}

		writeD(_data.length);
		writeB(_data);
	}
}