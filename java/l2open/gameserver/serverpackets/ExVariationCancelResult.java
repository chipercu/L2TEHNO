package l2open.gameserver.serverpackets;

public class ExVariationCancelResult extends L2GameServerPacket
{
	private int _closeWindow;
	private int _unk1;

	public ExVariationCancelResult(int result)
	{
		_closeWindow = 1;
		_unk1 = result;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x58);
		writeD(_unk1);
		writeD(_closeWindow);
	}
}