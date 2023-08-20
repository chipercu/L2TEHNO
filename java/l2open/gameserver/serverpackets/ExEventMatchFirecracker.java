package l2open.gameserver.serverpackets;

public class ExEventMatchFirecracker extends L2GameServerPacket
{
	private int object_id;

	public ExEventMatchFirecracker(int _object_id)
	{
		object_id = _object_id;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x05);

		writeD(object_id);
	}
}