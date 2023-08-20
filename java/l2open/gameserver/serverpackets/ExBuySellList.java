package l2open.gameserver.serverpackets;

import l2open.gameserver.TradeController.NpcTradeList;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.GArray;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExBuySellList extends L2GameServerPacket
{
	private int _listId;
    private byte done;
	private final GArray<TradeItem> _Buylist;
	private final List<L2ItemInstance> _SellList;
	private final ConcurrentLinkedQueue<L2ItemInstance> _RefundList;
	private long _money;
	private double _TaxRate = 0;

	public ExBuySellList(NpcTradeList Buylist, L2Player activeChar, double taxRate)
	{
		if(Buylist != null)
		{
			_listId = Buylist.getListId();
			_Buylist = cloneAndFilter(Buylist.getItems());
			activeChar.setBuyListId(_listId);
		}
        else
            _Buylist = null;

		_money = activeChar.getAdena();
		_TaxRate = taxRate;
		_RefundList = activeChar.getInventory().getRefundItemsList();

		_SellList = new ArrayList<L2ItemInstance>(activeChar.getInventory().getItemsList().size());
		for(L2ItemInstance item : activeChar.getInventory().getItemsList())
			if(item.getItemId() != 57 && item.getItem().isSellable() && item.canBeTraded(activeChar) && item.getReferencePrice() > 0)
				_SellList.add(item);
		Collections.sort(_SellList, Inventory.OrderComparator);
	}

	public ExBuySellList done()
	{
		done = 1;
		return this;
	}

	protected static GArray<TradeItem> cloneAndFilter(GArray<TradeItem> list)
	{
		if(list == null)
			return null;

		GArray<TradeItem> ret = new GArray<TradeItem>(list.size());

		for(TradeItem item : list)
		{
			// А не пора ли обновить количество лимитированных предметов в трейд листе?
			if(item.getCurrentValue() < item.getCount() && item.getLastRechargeTime() + item.getRechargeTime() <= System.currentTimeMillis() / 60000)
			{
				item.setLastRechargeTime(item.getLastRechargeTime() + item.getRechargeTime());
				item.setCurrentValue(item.getCount());
			}

			if(item.getCurrentValue() == 0 && item.getCount() != 0)
				continue;

			ret.add(item);
		}

		return ret;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xB7);

        writeD(0x00);
		writeQ(_money); // current money
        writeD(_listId);

        if(_Buylist == null)
			writeH(0);
		else
		{
			writeH(_Buylist.size());
			for(TradeItem item : _Buylist)
			{
				writeD(item.getObjectId());
				writeD(item.getItemId());
				writeD(0);
				writeQ(item.getCurrentValue()); // max amount of items that a player can buy at a time (with this itemid)
                writeH(item.getItem().getType2ForPackets()); // item type2
				writeH(0x00);
				writeH(item.getCustomType1()); // getCustomType1?
				writeD(item.getItem().getBodyPart()); // rev 415  slot    0006-lr.ear  0008-neck  0030-lr.finger  0040-head  0080-??  0100-l.hand  0200-gloves  0400-chest  0800-pants  1000-feet  2000-??  4000-r.hand  8000-r.hand
				writeH(item.getEnchantLevel()); // enchant level
				writeH(item.getCustomType2());

                writeD(item.getAugmentationId());// Augmentation Id
                writeD(item.getTemporalLifeTime());// Mana
                writeD(item.getShadowLifeTime());// Shadow Life Time
                writeItemElements(item);

				writeH(item.getEnchantOptions()[0]);
				writeH(item.getEnchantOptions()[1]);
				writeH(item.getEnchantOptions()[2]);

                writeQ((long) (item.getOwnersPrice() * (1 + _TaxRate)));
			}
		}

        getClient().sendPacket(new SellList(done, _SellList, _RefundList));
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xB8);

        writeD(0x00);

		writeQ(_money); // current money
		writeD(_listId);
		writeD(0x00); // L2WT GOD Количество занятых слотов
		if(_Buylist == null)
			writeH(0);
		else
		{
			writeH(_Buylist.size());
			for (TradeItem item : _Buylist)
			{
				writeItemInfo(item, item.getCurrentValue());
				writeQ((long) (item.getOwnersPrice() * (1. + _TaxRate)));
			}
		}
        getClient().sendPacket(new SellList(done, _SellList, _RefundList));
		return true;
	}
}