package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PrivateStoreListSell extends L2GameServerPacket
{
	private int seller_id;
	private long buyer_adena;
	private final boolean _package;
	private ConcurrentLinkedQueue<TradeItem> _sellList;

	/**
	 * Список вещей в личном магазине продажи, показываемый покупателю
	 * @param buyer
	 * @param seller
	 */
	public PrivateStoreListSell(L2Player buyer, L2Player seller)
	{
		seller_id = seller.getObjectId();
		buyer_adena = buyer.getAdena();
		_package = seller.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE;
		_sellList = _package ? seller.getSellPkgList() : seller.getSellList();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xA1);
		writeD(seller_id);
		writeD(_package ? 1 : 0);
		writeQ(buyer_adena);

		if(getClient().isLindvior())
			writeD(0x00); //L2WT GOD
		writeD(_sellList.size());
		for(TradeItem ti : _sellList)
		{
			writeItemInfo(ti);
            writeQ(ti.getOwnersPrice());
			writeQ(ti.getItem().getReferencePrice() * 2);
		}
	}
}