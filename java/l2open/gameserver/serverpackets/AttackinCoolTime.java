package l2open.gameserver.serverpackets;

// TODO: 
public class AttackinCoolTime extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		// just trigger - без аргументов
		writeC(0x03);
	}
}