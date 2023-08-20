package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.Warehouse.WarehouseType;

import java.util.*;

public class WareHouseDepositList extends L2GameServerPacket
{
	private int _whtype;
	private long char_adena;
	private List<L2ItemInstance> _itemslist = new ArrayList<L2ItemInstance>();
	private int _depositedItemsCount;

	public WareHouseDepositList(L2Player cha, WarehouseType whtype)
	{
		cha.setUsingWarehouseType(whtype);
		_whtype = whtype.getPacketValue();
		char_adena = cha.getAdena();
		for(L2ItemInstance item : cha.getInventory().getItems())
			if(item != null && item.canBeStored(cha, _whtype == 1))
				_itemslist.add(item);
		Collections.sort(_itemslist, Inventory.OrderComparator);
		switch (_whtype)
		{
			/*case 1:
				_depositedItemsCount = cha.getWarehouse().getSize();
				break;
			case 2:
				_depositedItemsCount = cha.getFreight().getSize();
				break;
			case 3:
			case 4:
				_depositedItemsCount = cha.getClan().getWarehouse().getSize();
				break;*/
			default:
				_depositedItemsCount = 0;
				return;
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x41);
		writeH(_whtype);
		writeQ(char_adena);
		if(getClient().isLindvior())
		{
			writeH(_depositedItemsCount);
			writeD(0x00);
		}
		writeH(_itemslist.size());
		for(L2ItemInstance temp : _itemslist)
		{
			writeItemInfo(temp);
			writeD(temp.getObjectId());
		}
	}
}