package l2open.gameserver.serverpackets;

import l2open.gameserver.clientpackets.RequestExPostItemList;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;

import java.util.*;

/**
 * Ответ на запрос создания нового письма.
 * Отсылается при получении {@link RequestExPostItemList}
 * Содержит список вещей, которые можно приложить к письму.
 */
public class ExReplyPostItemList extends L2GameServerPacket
{
	private List<L2ItemInstance> _itemslist = new ArrayList<L2ItemInstance>();

	public ExReplyPostItemList(L2Player cha)
	{
		if(!cha.getPlayerAccess().UseTrade) // если не разрешен трейд передавать предметы нельзя
			return;

		String tradeBan = cha.getVar("tradeBan"); // если трейд забанен тоже
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
			return;

		for(L2ItemInstance item : cha.getInventory().getItems())
			if(item != null && item.canBeTraded(cha))
				_itemslist.add(item);
		Collections.sort(_itemslist, Inventory.OrderComparator);
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xB2);

		writeD(_itemslist.size());
		for(L2ItemInstance temp : _itemslist)
			writeItemInfo(temp);
	}
}