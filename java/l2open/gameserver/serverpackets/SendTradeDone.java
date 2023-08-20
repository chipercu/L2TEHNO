package l2open.gameserver.serverpackets;

public class SendTradeDone extends L2GameServerPacket
{
	public static final SendTradeDone Success = new SendTradeDone(1), Fail = new SendTradeDone(0);

	private int _num;

	public SendTradeDone(int num)
	{
		_num = num;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x1c);
		writeD(_num);
	}
}