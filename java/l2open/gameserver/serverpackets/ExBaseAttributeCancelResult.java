package l2open.gameserver.serverpackets;

import l2open.gameserver.model.items.L2ItemInstance;

public class ExBaseAttributeCancelResult extends L2GameServerPacket
{
	private boolean _result;
	private int _objectId;
	private int _element;

	public ExBaseAttributeCancelResult(boolean result, L2ItemInstance item, int element)
	{
		_result = result;
		_objectId = item.getObjectId();
		_element = element;
	}
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x75);
		writeD(_result ? 0x01 : 0x00);
		writeD(_objectId);
		writeD(_element);
	}
}