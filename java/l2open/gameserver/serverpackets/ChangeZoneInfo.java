package l2open.gameserver.serverpackets;

/**
 * Этот пакет нужен для Фреи, он меняет внешний вид зала на третей стадии фарма...
 *******************
 * @author Diagod  *
 *   20.05.2011    *
 *******************
 */
public class ChangeZoneInfo extends L2GameServerPacket
{
	private int _stage = 0x00;

	public ChangeZoneInfo(int stage)
	{
		_stage = stage;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeHG(0xC2);

		writeD(0x00); // мб делл?
		writeD(0x00);
		writeD(_stage);
	}
}