package l2open.gameserver.serverpackets;

import l2open.gameserver.itemmall.ItemMall;

import java.util.List;

public class ExBR_RecentProductListPacket extends L2GameServerPacket
{
    List<ItemMall.ItemMallItem> list;

    public ExBR_RecentProductListPacket(int objId) {
        list = ItemMall.getInstance().getRecentListByOID(objId);
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeH(getClient().isLindvior() ? 0xDD : 0xDC);
        writeD(list.size());
        for (ItemMall.ItemMallItem item : list) {
            writeD(item.template.brId);
            writeH(item.template.category);
            writeD(item.price);
            int cat = 0;
            if (item.iSale >= 2) {
                switch (item.iCategory2) {
                    case 0:
                    case 2:
                        cat = 2;
                        break;
                    case 1:
                        cat = 3;
                        break;
                }
            }
            writeD(cat);
            if (item.iStartSale > 0 && item.iEndSale > 0) {
                writeD(item.iStartSale);
                writeD(item.iEndSale);
            } else {
                writeD(0x12CEDE40);
                writeD(0x7ECE3CD0);
            }
            writeC(0x7F);
            writeC(item.iStartHour);
            writeC(item.iStartMin);
            writeC(item.iEndHour);
            writeC(item.iEndMin);
            writeD(item.iStock);
            writeD(item.iMaxStock);
        }
    }
}