package l2open.gameserver.serverpackets;

// TODO: 
public class AttackOutOfRange extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		// just trigger - без аргументов
		writeC(0x02);
	}
}