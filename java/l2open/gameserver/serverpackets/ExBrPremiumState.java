package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class ExBrPremiumState extends L2GameServerPacket
{
	private int _objectId;
	private int _state;

	/**
	 * Если параметр 1 у игрока появляется желтый квадратик вокруг уровня, если что-то другое пропадает.
	 */
	public ExBrPremiumState(L2Player activeChar, int state)
	{
		_objectId = activeChar.getObjectId();
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xD9);
		writeD(_objectId);
		writeC(_state);
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeEx(0xDA);
		writeD(_objectId);
		writeC(_state);
		return true;
	}
}
