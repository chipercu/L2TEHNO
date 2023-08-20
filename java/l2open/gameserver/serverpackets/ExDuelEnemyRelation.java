package l2open.gameserver.serverpackets;

public class ExDuelEnemyRelation extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x5A : 0x59);
		// just trigger
	}
}