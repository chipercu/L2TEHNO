package l2open.gameserver.serverpackets;

public class PledgeExtendedInfo extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x8A);
		//TODO SddSddddddddSd 
	}
}