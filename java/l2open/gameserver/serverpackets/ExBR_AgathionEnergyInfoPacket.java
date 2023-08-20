package l2open.gameserver.serverpackets;

import l2open.gameserver.model.items.L2ItemInstance;

public class ExBR_AgathionEnergyInfoPacket extends L2GameServerPacket
{
	private int _size;
	private L2ItemInstance[] _itemList = null;

	public ExBR_AgathionEnergyInfoPacket(int size, L2ItemInstance... item)
	{
		_itemList = item;
		_size = size;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0xDF : 0xDE);

		writeD(_size);
		if(_itemList != null)
			for(L2ItemInstance item : _itemList)
			{
				if(item == null || item.getItem().getAgathionEnergy() == 0)
					continue;
				writeD(item.getObjectId());
				writeD(item.getItemId());
				writeD(0x200000);
				writeD(item.getAgathionEnergy());//current energy
				writeD(item.getItem().getAgathionEnergy()); //max energy
			}
	}
}