package l2open.gameserver.serverpackets;

import l2open.gameserver.model.items.L2ItemInstance;

/**
 * ddQhdhhhhhdhhhhhhhh - Gracia Final
 */
public class ExRpItemLink extends L2GameServerPacket
{
	private ItemInfo _item = null;

	public ExRpItemLink(ItemInfo item)
	{
		_item = item;
	}

	@Override
	protected final void writeImpl()
	{
		if(_item == null || _item.getObjectId() == 0)
			return;
		writeC(EXTENDED_PACKET);
		writeHG(0x6c);

		writeD(_item.getObjectId());
		writeD(_item.getItemId());
        writeD(0x00);
		writeQ(_item.getCount());
		writeH(_item.getType2());
        writeH(_item.getCustomType1());
        writeH(_item.isEquipped() ? 1 : 0);
		writeD(_item.getBodyPart());
		writeH(_item.getRealEnchantLevel());
		writeH(_item.getCustomType2());
		writeD(_item.getAugmentationId());
		writeD(_item.getShadowLifeTime());
        writeD(_item.getTemporalLifeTime());

		if(getClient().isLindvior())
			writeH(0x01); //L2WT GOD

		writeH(_item.getAttackElement()[0]);
		writeH(_item.getAttackElement()[1]);
		writeH(_item.getDefenceFire());
		writeH(_item.getDefenceWater());
		writeH(_item.getDefenceWind());
		writeH(_item.getDefenceEarth());
		writeH(_item.getDefenceHoly());
		writeH(_item.getDefenceUnholy());
       	writeH(_item.getEnchantOptions()[0]);
		writeH(_item.getEnchantOptions()[1]);
		writeH(_item.getEnchantOptions()[2]);

		if(getClient().isLindvior())
			writeD(_item.getVisualId()); // getVisualId
	}

	// TODO: сделать общий макет для итемов.
    public static class ItemInfo
	{
		private int ownerId;
		private int objectId;
		private int itemId;
		private int _visual_item_id;
		private long count;
		private short type2;
		private int customType1;
		private boolean equipped;
		private int bodyPart;
		private short enchantLevel;
		private int customType2;
		private int augmentationId;
		private int shadowLifeTime;
		private int[] attackElement;
		private int defenceFire;
		private int defenceWater;
		private int defenceWind;
		private int defenceEarth;
		private int defenceHoly;
		private int defenceUnholy;
		private int temporalLifeTime;
		private int[] enchantOptions = new int[3];

        public ItemInfo(L2ItemInstance item)
		{
			if(item == null)
			{
				objectId = 0;
				return;
			}
			ownerId = item.getOwnerId();
			objectId = item.getObjectId();
			itemId = item.getItemId();
			type2 = (short) item.getItem().getType2ForPackets();
			bodyPart = item.getItem().getBodyPart();
            customType1 = item.getCustomType1();
            equipped = item.isEquipped();
            customType2 = item.getCustomType2();
            temporalLifeTime = item.isTemporalItem() ? item.getLifeTimeRemaining() : 0x00;
			count = item.getCount();
			enchantLevel = (short) item.getRealEnchantLevel();
			augmentationId = item.getAugmentationId();
			shadowLifeTime = item.isShadowItem() ? item.getLifeTimeRemaining() : -1;
			attackElement = item.getAttackElementAndValue();
			defenceFire = item.getDefenceFire();
			defenceWater = item.getDefenceWater();
			defenceWind = item.getDefenceWind();
			defenceEarth = item.getDefenceEarth();
			defenceHoly = item.getDefenceHoly();
			defenceUnholy = item.getDefenceUnholy();
			enchantOptions = item.getEnchantOptions();
			_visual_item_id = item._visual_item_id;
		}

		public int getVisualId()
		{
			return _visual_item_id;
		}

		public int[] getEnchantOptions()
		{
			return enchantOptions;
		}

		public int getOwnerId()
		{
			return ownerId;
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
			return count;
		}

		public short getType2()
		{
			return type2;
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

		public int[] getAttackElement()
		{
			return attackElement;
		}

		public int getDefenceFire()
		{
			return defenceFire;
		}

		public int getDefenceWater()
		{
			return defenceWater;
		}

		public int getDefenceWind()
		{
			return defenceWind;
		}

		public int getDefenceEarth()
		{
			return defenceEarth;
		}

		public int getDefenceHoly()
		{
			return defenceHoly;
		}

		public int getDefenceUnholy()
		{
			return defenceUnholy;
		}

        public int getCustomType1()
		{
            return customType1;
        }

        public boolean isEquipped()
		{
            return equipped;
        }

        public int getCustomType2()
		{
            return customType2;
        }

        public int getTemporalLifeTime()
		{
            return temporalLifeTime;
        }
    }
}