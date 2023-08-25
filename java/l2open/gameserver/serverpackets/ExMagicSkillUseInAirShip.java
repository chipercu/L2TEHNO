package l2open.gameserver.serverpackets;

public class ExMagicSkillUseInAirShip extends L2GameServerPacket
{
	/**
	 * заготовка!!!
	 * Format: ddddddddddh[h]h[ddd]
	 */

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x74 : 0x73);
	}
}