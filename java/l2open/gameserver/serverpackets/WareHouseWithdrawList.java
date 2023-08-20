package l2open.gameserver.serverpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.Warehouse.WarehouseType;

import java.util.NoSuchElementException;

public class WareHouseWithdrawList extends L2GameServerPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2; // final - 4?
	public static final int CASTLE = 3;
	public static final int FREIGHT = 4; // final - 1?

	private long _money;
	private L2ItemInstance[] _items;
	private int _type;
	private boolean can_writeImpl = false;
	private int _inventoryUsedSlots;

	public WareHouseWithdrawList(L2Player cha, WarehouseType type, ItemClass clss)
	{
		if(cha == null)
			return;

		_money = cha.getAdena();
		_type = type.getPacketValue();
		cha.setUsingWarehouseType(type);
		_inventoryUsedSlots = cha.getInventory().getSize();
		switch(type)
		{
			case PRIVATE:
				_items = cha.getWarehouse().listItems(clss);
				break;
			case CLAN:
			case CASTLE:
				_items = cha.getClan().getWarehouse().listItems(clss);
				break;
			/*
			 case CASTLE:
			 items = _cha.getClan().getCastleWarehouse().listItems();
			 break;
			 */
			case FREIGHT:
				_items = cha.getFreight().listItems(clss);
				break;
			default:
				throw new NoSuchElementException("Invalid value of 'type' argument");
		}

		if(_items.length == 0)
		{
			cha.sendPacket(Msg.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
			return;
		}

		can_writeImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		writeC(0x42);
		writeH(_type);
		writeQ(_money);
		writeH(_items.length);
		if(getClient().isLindvior())
		{
			writeH(1);
			writeD(0x00);
			writeD(_inventoryUsedSlots);
		}
		for(L2ItemInstance temp : _items)
		{
			writeItemInfo(temp);
			writeD(temp.getObjectId());//здесь что то другое..
			if(getClient().isLindvior())
			{
				writeD(0x00);
				writeD(0x00);
			}
		}
	}
}