package l2open.gameserver.serverpackets;

public class ExResponseShowStepTwo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xAF);
		// TODO dS x[cS]
	}
}