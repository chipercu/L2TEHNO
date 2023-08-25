package l2open.gameserver.serverpackets;

public class WithdrawAlliance extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xAB);
		//TODO d
	}
}