package l2open.gameserver.serverpackets;

public class ExPlayAnimation extends L2GameServerPacket
{
	int _objectId;
	int _type;
	int _time;
	String _unk;

	public ExPlayAnimation(int objectId, int type, int time, String unk)
	{
		_objectId = objectId;
		_type = type;
		_time = time;
		_unk = unk;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x5A);

		writeD(_objectId);
		writeC(_type);
		writeD(_time);
		writeS(_unk);
	}
}