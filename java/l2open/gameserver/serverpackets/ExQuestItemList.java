package l2open.gameserver.serverpackets;

import javolution.util.FastList;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
/**
 * @author : Drizzy
 */
public class ExQuestItemList extends L2GameServerPacket 
{
	private FastList<L2ItemInstance> _items;
	private L2Player player;

	public ExQuestItemList(FastList<L2ItemInstance> items, L2Player activeChar)
	{
		player = activeChar;
        _items = items;
    }

    @Override
    protected void writeImpl()
	{
        writeC(0xFE);
        writeHG(0xC6);
        writeH(_items.size());
		for (L2ItemInstance item : _items)
           writeItemInfo(item);

        writeH(0x00);//TODO: Block Items length
        writeC(0x00);//TODO: Block Mode
        // for(;;) {
        //    writeD(0x00);//Id to block
        //}
		//_log.info("list = " + list);
		FastList.recycle(_items);
    }

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0xFE);
        writeH(0xC7);
        writeH(_items.size());
		for (L2ItemInstance item : _items)
		{
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeD(item.getEquipSlot());
            writeQ(item.getCount());
            writeD(item.getItem().getType2());
            writeH(item.getCustomType1());
            writeD(item.getItem().getBodyPart());
            writeH(item.getEnchantLevel());
            writeH(item.getCustomType2());
			if(item.isAugmented())
				writeD(item.getAugmentation().getAugmentationId());
			else
				writeD(0x00);
            writeD(item.isShadowItem() ? item.getLifeTimeRemaining() : -1);
            writeD(item.isTemporalItem() ? item.getLifeTimeRemaining() : 0x00);
			writeH(0x01); // L2WT GOD
            writeItemElements(item);
            writeH(item.getEnchantOptions()[0]);
			writeH(item.getEnchantOptions()[1]);
			writeH(item.getEnchantOptions()[2]);
			writeD(item._visual_item_id); // getVisualId
        }

        writeH(0x00);//TODO: Block Items length
        writeC(0x00);//TODO: Block Mode
        // for(;;) {
        //    writeD(0x00);//Id to block
        //}
		//_log.info("list = " + list);
		FastList.recycle(_items);
		return true;
	}
}
