package l2open.gameserver.serverpackets;

// TODO: 
public class AttackDeadTarget extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		// just trigger - без аргументов
		writeC(0x04);
	}
}