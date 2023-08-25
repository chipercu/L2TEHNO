package l2open.gameserver.serverpackets;

public class ExShowQuestMark extends L2GameServerPacket
{
	private int _questId;
	private int _cond;

	public ExShowQuestMark(int questId, int cond)
	{
		_questId = questId;
		_cond = cond;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x21);
		writeD(_questId);
		if(getClient().isLindvior())
			writeD(_cond);
	}
}