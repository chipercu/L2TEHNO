package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * Format: ch ddcdc
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private int _mAddPoint, _mPeriodType, _pointType, _pcBangPoints, _remainTime;

	public ExPCCafePointInfo(int pcBangPoints, int mAddPoint, int mPeriodType, int pointType, int remainTime)
	{
		_pcBangPoints = pcBangPoints;
		_mAddPoint = mAddPoint;
		_mPeriodType = mPeriodType;
		_pointType = pointType;
		_remainTime = remainTime;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x32);

		writeD(_pcBangPoints); // количество очков ПК клуба...
		writeD(_mAddPoint); // Указатель изменения счета.
		writeC(_mPeriodType); // period(0=don't show window,1=acquisition,2=use points) 
		writeD(_remainTime); // period hours left 
		writeC(_pointType); // Разный цвет указателя изменения счета. 0-желтый, 1-голубой, 2-красный, 3-черный
	}
}