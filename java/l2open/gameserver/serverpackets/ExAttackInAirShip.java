package l2open.gameserver.serverpackets;

public class ExAttackInAirShip extends L2GameServerPacket
{
	/*
	 * заготовка!!!
	 * Format: dddcddddh[ddc]
	 * ExAttackInAirShip AttackerID:%d DefenderID:%d Damage:%d bMiss:%d bCritical:%d AirShipID:%d
	 */

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x72);
	}
}