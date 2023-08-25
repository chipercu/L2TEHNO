package l2open.gameserver.serverpackets;

import l2open.gameserver.itemmall.ItemMall;

public class ExBR_ProductInfo extends L2GameServerPacket
{
    private ItemMall.ItemMallItem item;

    public ExBR_ProductInfo(ItemMall.ItemMallItem item) {
        this.item = item;
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeH(getClient().isLindvior() ? 0xD8 : 0xD7);

        writeD(item.template.brId);
        writeD(item.price);
        writeD(1);// по идее тут начало повторяющегося блока for()
        writeD(item.template.itemId);
        writeD(item.count);  //quantity // количество
        writeD(item.iWeight); //weight
        writeD(item.iDropable ? 1 : 0); //0 - dont drop/trade
    }
}