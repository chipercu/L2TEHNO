package l2open.gameserver.serverpackets;

public class ExEnchantSkillResult extends L2GameServerPacket
{
	private final int _result;

	public ExEnchantSkillResult(int result)
	{
		_result = result;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0xA8 : 0xA7);
		writeD(_result);
	}
}