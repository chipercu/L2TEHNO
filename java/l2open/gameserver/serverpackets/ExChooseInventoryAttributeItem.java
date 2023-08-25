package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;

import java.util.ArrayList;
import java.util.List;

public class ExChooseInventoryAttributeItem extends L2GameServerPacket 
{
    private L2ItemInstance item;
    private byte element;
    private byte level;

	private List<Integer> _attributableItems;

    public ExChooseInventoryAttributeItem(L2Player player, L2ItemInstance item) 
	{
        this.item = item;
        element = item.getEnchantAttributeStoneElement(false);
        level = item.getAttributeElementLevel() > 0 ? item.getAttributeElementLevel() : 0;

		// нужно для поддержки год клиента.
		_attributableItems = new ArrayList<Integer>();
		L2ItemInstance[] items = player.getInventory().getItems();
		for(L2ItemInstance _item : items)
			if(_item.getItem().getCrystalType() != L2Item.Grade.NONE && (_item.isArmor() || _item.isWeapon()))
				_attributableItems.add(_item.getObjectId());
    }

    @Override
    protected void writeImpl() 
	{
        writeC(0xfe);
        writeH(0x62);
        writeD(item.getItemId());
        for(byte i=0; i < 6; i++)
            writeD(i == element ? 1 : 0);
        writeD(level);
    }

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0xfe);
		writeH(0x63);
		writeD(item.getItemId());
		for(byte i=0; i < 6; i++)
			writeD(i == element ? 1 : 0);
		writeD(level);
		writeD(_attributableItems.size()); // equipable items count
		for(int itemObjId : _attributableItems)
			writeD(itemObjId); // itemObjId
		return true;
	}
}