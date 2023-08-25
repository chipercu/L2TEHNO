package l2open.gameserver.serverpackets;

/**
 * @author SYS
 */
public class ExAttributeEnchantResult extends L2GameServerPacket
{
	private int _result;

	public ExAttributeEnchantResult(int unknown)
	{
		_result = unknown;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x61);
		writeD(_result); //2 - отменено/неудачно
	}
}