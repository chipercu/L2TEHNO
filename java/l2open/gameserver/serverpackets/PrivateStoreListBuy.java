package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PrivateStoreListBuy extends L2GameServerPacket
{
	private int buyer_id;
	private long seller_adena;
	private ConcurrentLinkedQueue<TradeItem> _buyerslist;

	/**
	 * Список вещей в личном магазине покупки, показываемый продающему
	 * @param seller
	 * @param storePlayer
	 */
	public PrivateStoreListBuy(L2Player seller, L2Player storePlayer)
	{
		seller_adena = seller.getAdena();
		buyer_id = storePlayer.getObjectId();

		ConcurrentLinkedQueue<L2ItemInstance> sellerItems = seller.getInventory().getItemsList();
		_buyerslist = new ConcurrentLinkedQueue<TradeItem>();
		_buyerslist.addAll(storePlayer.getBuyList());

		for(TradeItem buyListItem : _buyerslist)
			buyListItem.setCurrentValue(0);

		for(L2ItemInstance sellerItem : sellerItems)
			for(TradeItem buyListItem : _buyerslist)
				if(sellerItem.getItemId() == buyListItem.getItemId() && sellerItem.canBeTraded(seller))
				{
					buyListItem.setCurrentValue(Math.min(buyListItem.getCount(), sellerItem.getCount()));
					continue;
				}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xBE);

		writeD(buyer_id);
		writeQ(seller_adena);
		if(getClient().isLindvior())
			writeD(0x00);
		writeD(_buyerslist.size());
		for(TradeItem buyersitem : _buyerslist)
		{
			L2Item tmp = ItemTemplates.getInstance().getTemplate(buyersitem.getItemId());

			writeD(buyersitem.getObjectId());
			writeD(buyersitem.getItemId());
			writeD(buyersitem.getEquipSlot());
			writeQ(buyersitem.getCurrentValue());
			writeH(buyersitem.getItem().getType2ForPackets());
			writeH(buyersitem.getCustomType1());
			writeH(buyersitem.isEquipped() ? 1 : 0);
			writeD(buyersitem.getItem().getBodyPart());
			writeH(buyersitem.getEnchantLevel());
			writeH(buyersitem.getCustomType2());
			writeD(buyersitem.getAugmentationId());
			writeD(buyersitem.getShadowLifeTime());
			writeD(buyersitem.getTemporalLifeTime());
			if(getClient().isLindvior())
				writeH(0x01); //L2WT GOD
			writeH(buyersitem.getAttackElement()[0]);
			writeH(buyersitem.getAttackElement()[1]);
			writeH(buyersitem.getDefenceFire());
			writeH(buyersitem.getDefenceWater());
			writeH(buyersitem.getDefenceWind());
			writeH(buyersitem.getDefenceEarth());
			writeH(buyersitem.getDefenceHoly());
			writeH(buyersitem.getDefenceUnholy());
			writeH(buyersitem.getEnchantOptions()[0]);
			writeH(buyersitem.getEnchantOptions()[1]);
			writeH(buyersitem.getEnchantOptions()[2]);
			if(getClient().isLindvior())
				writeD(buyersitem.getVisualId()); // getVisualId

            writeD(buyersitem.getObjectId());
            writeQ(buyersitem.getOwnersPrice());
			writeQ(tmp.getReferencePrice());
			writeQ(buyersitem.getCount());
		}
	}
}