package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;

public class ExGMViewQuestItemList extends L2GameServerPacket
{
	private int _size;
	private L2ItemInstance[] _items;

	private int _limit;
	private String _name;

	public ExGMViewQuestItemList(L2Player player, L2ItemInstance[] items, int size)
	{
		_items = items;
		_size = size;
		_name = player.getName();
		//_limit = Config.QUEST_INVENTORY_MAXIMUM;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeD(0xC7);
		/*writeS(_name);
		writeD(_limit);
		writeH(_size);
		for(L2ItemInstance temp : _items)
			if(temp.getTemplate().isQuest())
				writeItemInfo(temp);*/
	}
}
