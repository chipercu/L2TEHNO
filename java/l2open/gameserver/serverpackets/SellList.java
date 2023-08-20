package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.items.L2ItemInstance;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SellList extends L2GameServerPacket
{
    private byte done;
    private List<L2ItemInstance> sellList;
    private ConcurrentLinkedQueue<L2ItemInstance> refundList;

    public SellList(byte d, List<L2ItemInstance> s, ConcurrentLinkedQueue<L2ItemInstance> r)
	{
        done = d;
        sellList = s;
        refundList = r;
    }

    @Override
    protected final void writeImpl() 
	{
        writeC(0xFE);
		writeH(0xB7);
        writeD(0x01);

        writeH(sellList.size());

		int count=0;
        for(L2ItemInstance item : sellList) 
		{
			//_log.info("SellList["+sellList.size()+"]["+(++count)+"]: ["+item.getName()+"]["+item.getItemId()+"]["+item.getObjectId()+"]");
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeD(item.getEquipSlot());
            writeQ(item.getCount());
            writeH(item.getItem().getType2ForPackets());
            writeH(item.getCustomType1());
            writeH(item.isEquipped() ? 1 : 0);
            writeD(item.getBodyPart());
            writeH(item.getRealEnchantLevel());
            writeH(item.getCustomType2());
            writeD(item.getAugmentationId());
            writeD(item.isShadowItem() ? item.getLifeTimeRemaining() : -1);
            writeD(item.isTemporalItem() ? item.getLifeTimeRemaining() : 0x00);
            writeItemElements(item);
            //три значения энчант эффекта
            writeH(item.getEnchantOptions()[0]);
			writeH(item.getEnchantOptions()[1]);
			writeH(item.getEnchantOptions()[2]);
			if(ConfigValue.SellItemOneAdena)
				writeQ(1L);
			else
			{
				long price = item.getReferencePrice() / ConfigValue.SellItemDiv;
				price -= price*ConfigValue.SellITaxPer/100;

				writeQ(price);
			}
        }

        writeH(refundList.size());

        for (L2ItemInstance item : refundList) 
		{
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeD(item.getEquipSlot());
            writeQ(item.getCount());
            writeH(item.getItem().getType2());
            writeH(item.getCustomType1());
            writeH(item.isEquipped() ? 1 : 0);
            writeD(item.getBodyPart());
            writeH(item.getRealEnchantLevel());
            writeH(item.getCustomType2());
            writeD(item.getAugmentationId());
            writeD(item.isShadowItem() ? item.getLifeTimeRemaining() : -1);
            writeD(item.isTemporalItem() ? item.getLifeTimeRemaining() : 0x00);

            writeItemElements(item);
            writeH(item.getEnchantOptions()[0]);
			writeH(item.getEnchantOptions()[1]);
			writeH(item.getEnchantOptions()[2]);
            writeD(item.getObjectId());
			if(ConfigValue.SellItemOneAdena)
				writeQ(item.getCount());
			else
			{
				long price = item.getCount() * item.getReferencePrice() / ConfigValue.SellItemDiv;
				price -= price*ConfigValue.SellITaxPer/100;

				writeQ(price);
			}
        }
        writeC(done);
    }

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0xFE);
		writeH(0xB8);
        writeD(0x01);

		writeD(0x00); // L2WT GOD Количество занятых слотов
		writeH(sellList.size());
		for (L2ItemInstance item : sellList)
		{
			writeItemInfo(item);
			if(ConfigValue.SellItemOneAdena)
				writeQ(1L);
			else
			{
				long price = item.getReferencePrice() / ConfigValue.SellItemDiv;
				price -= price*ConfigValue.SellITaxPer/100;

				writeQ(price);
			}
		}
		writeH(refundList.size());
		for (L2ItemInstance item : refundList)
		{
			writeItemInfo(item);
			writeD(item.getObjectId());
			if(ConfigValue.SellItemOneAdena)
				writeQ(item.getCount());
			else
			{
				long price = item.getCount() * item.getReferencePrice() / ConfigValue.SellItemDiv;
				price -= price*ConfigValue.SellITaxPer/100;

				writeQ(price);
			}
		}
		writeC(done);
		return true;
	}
}