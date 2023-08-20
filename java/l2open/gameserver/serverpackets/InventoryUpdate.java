package l2open.gameserver.serverpackets;

import l2open.gameserver.model.items.L2ItemInstance;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * sample
 *
 * 21			// packet type
 * 01 00			// item count
 *
 * 03 00			// update type   01-added?? 02-modified 03-removed
 * 04 00			// itemType1  0-weapon/ring/earring/necklace  1-armor/shield  4-item/questitem/adena
 * c6 37 50 40	// objectId
 * cd 09 00 00	// itemId
 * 05 00 00 00	// count
 * 05 00			// itemType2  0-weapon  1-shield/armor  2-ring/earring/necklace  3-questitem  4-adena  5-item
 * 00 00			// always 0 ??
 * 00 00			// equipped 1-yes
 * 00 00 00 00	// slot  0006-lr.ear  0008-neck  0030-lr.finger  0040-head  0080-??  0100-l.hand  0200-gloves  0400-chest  0800-pants  1000-feet  2000-??  4000-r.hand  8000-r.hand
 * 00 00			// enchant level
 * 00 00			// always 0 ??
 * 00 00 00 00	// augmentation id
 * ff ff ff ff	// shadow weapon time remaining
 *
 * format   h (hh dddhhhd hh dd) revision 740
 * format   h (hhdddQhhhdhhhhdddddddddd) Gracia Final
 */
public class InventoryUpdate extends L2GameServerPacket
{
	private final List<ItemInfo> _items = Collections.synchronizedList(new Vector<ItemInfo>());

	public InventoryUpdate()
	{}

	public InventoryUpdate(List<L2ItemInstance> items)
	{
		for(L2ItemInstance item : items)
			_items.add(new ItemInfo(item));
	}

	public InventoryUpdate addNewItem(L2ItemInstance item)
	{
		item.setLastChange(L2ItemInstance.ADDED);
		_items.add(new ItemInfo(item));
		return this;
	}

	public InventoryUpdate addModifiedItem(L2ItemInstance item)
	{
		item.setLastChange(L2ItemInstance.MODIFIED);
		_items.add(new ItemInfo(item));
		return this;
	}

	public InventoryUpdate addRemovedItem(L2ItemInstance item)
	{
		item.setLastChange(L2ItemInstance.REMOVED);
		_items.add(new ItemInfo(item));
		return this;
	}

	public InventoryUpdate addItem(L2ItemInstance item)
	{
		if(item == null)
			return null;

		switch(item.getLastChange())
		{
			case L2ItemInstance.ADDED:
			{
				addNewItem(item);
				break;
			}
			case L2ItemInstance.MODIFIED:
			{
				addModifiedItem(item);
				break;
			}
			case L2ItemInstance.REMOVED:
			{
				addRemovedItem(item);
			}
		}
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x21);
		writeH(_items.size());
		for(ItemInfo temp : _items)
		{
			writeH(temp.getLastChange());
			writeD(temp.getObjectId());
			writeD(temp.getItemId());
			writeD(temp.getEquipSlot()); //order
			writeQ(temp.getCount());
			writeH(temp.getType2()); // item type2
			writeH(temp.getCustomType1());
			writeH(temp.isEquipped() ? 1 : 0);
			writeD(temp.getBodyPart()); // rev 415   slot    0006-lr.ear  0008-neck  0030-lr.finger  0040-head  0080-??  0100-l.hand  0200-gloves  0400-chest  0800-pants  1000-feet  2000-??  4000-r.hand  8000-r.hand
			writeH(temp.getRealEnchantLevel()); // enchant level or pet level
			writeH(temp.getCustomType2()); // Pet name exists or not shown in control item
			writeD(temp.getAugmentationId());
			writeD(temp.getShadowLifeTime()); //interlude FF FF FF FF
            writeD(temp.getTemporalLifeTime()); // limited time item life remaining

			writeH(temp.elemAtkType);
            writeH(temp.elemAtkPower);
            for (byte i = 0; i < 6; i++) 
                writeH(temp.getElementDefAttr(i));

			writeH(temp.getEnchantOptions()[0]);
			writeH(temp.getEnchantOptions()[1]);
			writeH(temp.getEnchantOptions()[2]);
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x21);
		writeH(_items.size());
		for (ItemInfo item : _items)
		{
			writeH(item.getLastChange());
			writeD(item.getObjectId());
			writeD(item.getItemId());
			writeD(item.getEquipSlot());
			writeQ(item.getCount());
			writeH(item.getType2());
			writeH(item.getCustomType1());
			writeH(item.isEquipped() ? 1 : 0);
			writeD(item.getBodyPart());
			writeH(item.getRealEnchantLevel());
			writeH(item.getCustomType2());
			writeD(item.getAugmentationId());
			writeD(item.getShadowLifeTime());
			writeD(item.getTemporalLifeTime());
			writeH(0x01); //L2WT GOD
			writeH(item.elemAtkType);
            writeH(item.elemAtkPower);
            for (byte i = 0; i < 6; i++) 
                writeH(item.getElementDefAttr(i));
			writeH(item.getEnchantOptions()[0]);
			writeH(item.getEnchantOptions()[1]);
			writeH(item.getEnchantOptions()[2]);
			writeD(item.getVisualId()); // getVisualId
		}
		getClient().sendPacket(new ExAdenaInvenCount(getClient().getActiveChar()));
		return true;
	}
	public class ItemInfo
	{
		private final short lastChange;
		private final int objectId;
		private final int itemId;
		private final int _visual_item_id;
		private final long long_count;
		private final short type2;
		private final short customType1;
		private final boolean isEquipped;
		private final int bodyPart;
		private final short enchantLevel;
		private final short customType2;
		private final int augmentationId;
		private final int shadowLifeTime;
		private final int elemAtkType;
        private final int elemAtkPower;
		private final int[] elemDefAttr;
		private final int equipSlot;
		private final int temporalLifeTime;
		private int[] enchantOptions = new int[3];

		private ItemInfo(L2ItemInstance item)
		{
			lastChange = (short) item.getLastChange();
			objectId = item.getObjectId();
			itemId = item.getItemId();
			long_count = item.getCount();
			type2 = (short) item.getItem().getType2ForPackets();
			customType1 = (short) item.getCustomType1();
			isEquipped = item.isEquipped();
			bodyPart = item.getItem().getBodyPart();
			enchantLevel = (short) item.getEnchantLevel();
			customType2 = (short) item.getCustomType2();
			augmentationId = item.getAugmentationId();
			shadowLifeTime = item.isShadowItem() ? item.getLifeTimeRemaining() : -1;
			elemAtkType = item.getAttackElement();
			elemAtkPower = item.getAttackElementValue();
			elemDefAttr = item.getDeffAttr();
			equipSlot = item.getEquipSlot();
			temporalLifeTime = item.isTemporalItem() ? item.getLifeTimeRemaining() : 0x00;
			enchantOptions = item.getEnchantOptions();
			_visual_item_id = item._visual_item_id;
		}

		public int getVisualId()
		{
			return _visual_item_id;
		}

		public short getLastChange()
		{
			return lastChange;
		}

		public int getObjectId()
		{
			return objectId;
		}

		public int getItemId()
		{
			return itemId;
		}

		public long getCount()
		{
			return long_count;
		}

		public short getType2()
		{
			return type2;
		}

		public short getCustomType1()
		{
			return customType1;
		}

		public boolean isEquipped()
		{
			return isEquipped;
		}

		public int getBodyPart()
		{
			return bodyPart;
		}

		public short getRealEnchantLevel()
		{
			return enchantLevel;
		}

		public int getAugmentationId()
		{
			return augmentationId;
		}

		public int getShadowLifeTime()
		{
			return shadowLifeTime;
		}

		public short getCustomType2()
		{
			return customType2;
		}

		public int getEquipSlot()
		{
			return equipSlot;
		}

		public int getTemporalLifeTime()
		{
			return temporalLifeTime;
		}

		public int getElementDefAttr(byte i) 
		{
            return elemDefAttr[i];
        }

		public int[] getEnchantOptions()
		{
			return enchantOptions;
		}
	}
}