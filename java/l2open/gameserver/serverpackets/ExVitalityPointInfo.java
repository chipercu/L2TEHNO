package l2open.gameserver.serverpackets;

public class ExVitalityPointInfo extends L2GameServerPacket
{
	private final int _VitalityPoint;

	public ExVitalityPointInfo(int VitalityPoint)
	{
		_VitalityPoint = VitalityPoint;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xA0);
		writeD(_VitalityPoint);
		if(getClient().isLindvior())
		{
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
		}
	}
}