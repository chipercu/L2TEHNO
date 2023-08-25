package l2open.gameserver.serverpackets;

import java.util.Map;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.PremiumItem;

public class ExGetPremiumItemList extends L2GameServerPacket
{
	private int _objectId;
	private Map<Integer, PremiumItem> _list;

	public ExGetPremiumItemList(L2Player activeChar)
	{
		_objectId = activeChar.getObjectId();
		_list = activeChar.getPremiumItemList();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x87 : 0x86);
		if(!_list.isEmpty())
		{
			writeD(_list.size());
			for(Map.Entry<Integer, PremiumItem> entry : _list.entrySet())
			{
				writeD(entry.getKey());
				writeD(_objectId);
				writeD(entry.getValue().getItemId());
				writeQ(entry.getValue().getCount());
				writeD(0);
				writeS(entry.getValue().getSender());
			}
		}
	}
}