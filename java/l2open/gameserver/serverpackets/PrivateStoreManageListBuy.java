package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PrivateStoreManageListBuy extends L2GameServerPacket
{
	private ConcurrentLinkedQueue<TradeItem> buyList = new ConcurrentLinkedQueue<TradeItem>();
	private int buyer_id;
	private long buyer_adena;
	private L2TradeList _list;

	/**
	 * Окно управления личным магазином продажи
	 * @param buyer
	 */
	public PrivateStoreManageListBuy(L2Player buyer)
	{
		buyer_id = buyer.getObjectId();
		buyer_adena = buyer.getAdena();

		int _id, body_part, type2;
		long count, store_price, owner_price;
		L2Item tempItem;
        buyList = buyer.getBuyList();

		_list = new L2TradeList(0);
		for(L2ItemInstance item : buyer.getInventory().getItems())
			if(item != null && item.canBeTraded(buyer) && item.getItemId() != L2Item.ITEM_ID_ADENA)
			{
				for(TradeItem ti : buyer.getBuyList())
					if(ti.getItemId() == item.getItemId())
						continue;
				_list.addItem(item);
			}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xBD);
		//section 1
		writeD(buyer_id);
		writeQ(buyer_adena);

		//section2
		writeD(_list.getItems().size());//for potential sells
		for(L2ItemInstance temp : _list.getItems())
		{
			writeItemInfo(temp);
			writeQ(temp.getPriceToSell());
		}

		//section 3
		writeD(buyList.size());//count for any items already added for sell
        for (TradeItem item : buyList)
		{
			writeItemInfo(item);
            writeQ(item.getOwnersPrice());
            writeQ(item.getStorePrice());
            writeQ(item.getCount());
        }
	}

    @Deprecated
	static class BuyItemInfo
	{
		public int _id, body_part, type2;
		public long count, store_price, owner_price;

		public BuyItemInfo(int __id, long count2, long store_price2, int _body_part, int _type2, long owner_price2)
		{
			_id = __id;
			count = count2;
			store_price = store_price2;
			body_part = _body_part;
			type2 = _type2;
			owner_price = owner_price2;
		}
	}
}