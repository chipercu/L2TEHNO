package l2open.gameserver.serverpackets;

public class ExEventMatchMessage extends L2GameServerPacket
{
	private int _type;
	private String _msg;

	public ExEventMatchMessage(int type, String msg)
	{
		_type = type;
		_msg = msg;
	}

	// TODO cS
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x0F);
		
		writeC(_type);
		writeS(_msg);
	}
}