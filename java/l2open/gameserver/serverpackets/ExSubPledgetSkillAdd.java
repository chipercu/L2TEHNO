package l2open.gameserver.serverpackets;

public class ExSubPledgetSkillAdd extends L2GameServerPacket
{
	private int _id;
	private int _level;
	private int _pId;

	public ExSubPledgetSkillAdd(int id, int level, int pId)
	{
		_id = id;
		_level = level;
		_pId = pId;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x76);
		writeD(_pId);
		writeD(_id);
		writeD(_level);
	}

	public final String getType()
	{
		return "[S] ExSubPledgetSkillAdd";
	}
}