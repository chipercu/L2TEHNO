package l2open.gameserver.serverpackets;

import l2open.gameserver.model.items.L2ItemInstance;

public class ExPutItemResultForVariationCancel extends L2GameServerPacket
{
	private final int _itemObjId;
	private final int _itemId;
	private final int _aug1;
	private final int _aug2;
	private final long _price;

	public ExPutItemResultForVariationCancel(L2ItemInstance item, int price)
	{
		_itemObjId = item.getObjectId();
		_itemId = item.getItemId();
		_aug1 = 0x0000FFFF & item.getAugmentationId();
		_aug2 = item.getAugmentationId() >> 16;
		_price = price;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x57);
		writeD(_itemObjId);
		writeD(_itemId);
		writeD(_aug1);
		writeD(_aug2);
		writeQ(_price);
		writeD(0x01);
	}
}