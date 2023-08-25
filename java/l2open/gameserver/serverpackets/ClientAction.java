package l2open.gameserver.serverpackets;

public class ClientAction extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0x8F);
		//TODO d 
	}
}