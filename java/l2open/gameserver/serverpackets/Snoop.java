package l2open.gameserver.serverpackets;

public class Snoop extends L2GameServerPacket
{
	private int _convoID;
	private int _speakerId;
	private String _name;
	private int _type;
	private String _speaker;
	private String _msg;

	public Snoop(int id, String name, int type, int speakerId, String speaker, String msg)
	{
		_convoID = id;
		_name = name;
		_type = type;
		_speakerId = speakerId;
		_speaker = speaker;
		_msg = msg;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xdb);

		writeD(_convoID);
		writeS(_name);
		writeD(_speakerId);
		writeD(_type);
		writeS(_speaker);
		writeS(_msg);
	}
}