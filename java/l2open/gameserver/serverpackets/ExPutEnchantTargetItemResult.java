package l2open.gameserver.serverpackets;

public class ExPutEnchantTargetItemResult extends L2GameServerPacket
{
	private int _result;

	/*
	private int _crystal;
	private long _count;
	*/

	public ExPutEnchantTargetItemResult(int result, int cry, long count)
	{
		_result = result;
		/*
		_crystal = cry;
		_count = count;
		*/
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x81);
		writeD(_result);
		/* что за бред сумашедшего, нет там такого :) © Drin
		writeD(_crystal);
		writeQ(_count);
		*/
	}
}