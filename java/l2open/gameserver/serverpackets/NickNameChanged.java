package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Character;

public class NickNameChanged extends L2GameServerPacket
{
	private final int objectId;
	private final String title;

	public NickNameChanged(L2Character cha)
	{
		objectId = cha.getObjectId();
		title = cha.getTitle();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xCC);
		writeD(objectId);
		writeS(title);
	}
}