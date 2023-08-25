package l2open.gameserver.serverpackets;

/**
 * Format: ch dddd
 * @author SYS
 * Этот пакет показывает откат у элексиров.
 * Вот живой пример с оффа:
 * FE 49 00 AE 21 00 00 01 00 00 00 2C 01 00 00 2C 01 00 00
 * Где:
 * AE 21 00 00 - id предмета (8622 Elixir of Life (No Grade))
 * 2C 01 00 00 - время отката 300 сек
 */
public class ExUseSharedGroupItem extends L2GameServerPacket
{
	private int _itemId, _grpId, _remainedTime, _totalTime;

	public ExUseSharedGroupItem(int itemId, int grpId, int remainedTime, int totalTime)
	{
		_itemId = itemId;
		_grpId = grpId;
		_remainedTime = remainedTime / 1000;
		_totalTime = totalTime / 1000;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x4a);

		writeD(_itemId);
		writeD(_grpId);
		writeD(_remainedTime);
		writeD(_totalTime);
	}
}