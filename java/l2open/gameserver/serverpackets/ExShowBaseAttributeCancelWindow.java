package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;

import java.util.Vector;

/**
 * @author SYS
 */
public class ExShowBaseAttributeCancelWindow extends L2GameServerPacket
{
	private final Vector<L2ItemInstance> _items = new Vector<L2ItemInstance>();

	public ExShowBaseAttributeCancelWindow(L2Player activeChar)
	{
		for(L2ItemInstance i : activeChar.getInventory().getItemsList())
		{
			if(!i.hasAttribute() || !i.getItem().canBeEnchanted() || getAttributeRemovePrice(i) == 0 || i.getItem().isRaidAccessory() || i.getItemId() == 13752 || i.getItemId() == 13753 || i.getItemId() == 13754)
				continue;
			_items.add(i);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x74);
		writeD(_items.size());
		for(L2ItemInstance i : _items)
		{
			writeD(i.getObjectId());
			writeQ(getAttributeRemovePrice(i));
		}
	}
	
	public static long getAttributeRemovePrice(L2ItemInstance item)
	{
		switch(item.getCrystalType())
		{
			case S:
				return item.getItem().getType2() == L2Item.TYPE2_WEAPON ? 50000 : 40000;
			case S80:
				return item.getItem().getType2() == L2Item.TYPE2_WEAPON ? 100000 : 80000;
			case S84:
				return item.getItem().getType2() == L2Item.TYPE2_WEAPON ? 200000 : 160000;
		}
		return 0;
	}
}