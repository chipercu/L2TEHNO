package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class Say2 extends L2GameServerPacket
{
	private static final String _S__4A_SAY2 = "[S] 4A Say2";
	private int _objectId, _textType;
	private String _charName, _text;
	private int _msgId = -1; 

	public Say2(int objectId, int messageType, String charName, String text)
	{
		_objectId = objectId;
		_textType = messageType;
		_charName = charName;
		_text = text;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4A);
		writeD(_objectId);
		writeD(_textType);
		writeS(_charName);
		writeD(_msgId); // High Five NPCString ID 
		if(_text != null && !_text.isEmpty())
			writeS(_text);

		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		player.broadcastSnoop(_textType, _charName, _text, _objectId);
	}

	@Override
	public String getType()
	{
		return _S__4A_SAY2;
	}

	@Override
	public String toString()
	{
		return "obj_id="+_objectId+" type="+_textType+" name="+_charName+" text="+_text;
	}
}