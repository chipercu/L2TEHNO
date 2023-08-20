package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.util.Util;

/**
 * Пример:
 * 08
 * a5 04 31 48 ObjectId
 * 00 00 00 7c unk
 *
 * format  d
 */
public class DeleteObject extends L2GameServerPacket
{
	private int _objectId;

	public DeleteObject(L2Object obj)
	{
		_objectId = obj.getObjectId();
		//if(obj.isPlayer())
		//	Util.test();
	}

	@Override
	protected final void writeImpl()
	{
		if(_objectId == 0)
			return;
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getObjectId() == _objectId)
			return;

		writeC(0x08);
		writeD(_objectId);
		writeD(0x01); // Что-то странное. Если объект сидит верхом то при 0 он сперва будет ссажен, при 1 просто пропадет.
	}

	@Override
	public String getType()
	{
		return super.getType() + " " + L2ObjectsStorage.getCharacter(_objectId) + " (" + _objectId + ")";
	}
}