package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.items.L2ItemInstance;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * format: cddb, b - array of (ddhhdd)
 * Список продаваемого в приватный магазин покупки
 * см. также l2open.gameserver.clientpackets.RequestPrivateStoreBuy
 */
public class RequestPrivateStoreBuySellList extends L2GameClientPacket
{
	private int _buyerID, _count;
	private L2Player _buyer, _seller;
	private ConcurrentLinkedQueue<TradeItem> _sellerlist = new ConcurrentLinkedQueue<TradeItem>(), _buyerlist = null;

	private int _fail = 0; // 1 — некритичный сбой, просто прерывать обмен, 2 — снимать продавца с трейда
	private boolean seller_fail = false;

	@Override
	public void readImpl()
	{
		_seller = getClient().getActiveChar();

		_buyerID = readD();
		_buyer = (L2Player) _seller.getVisibleObject(_buyerID);
		_count = readD();

		if(_count * 28 > _buf.remaining() || _count > Short.MAX_VALUE || _count <= 0)
		{
			seller_fail = true;
			return;
		}
		else if(_seller == null || _buyer == null || _seller.getDistance3D(_buyer) > _seller.INTERACTION_DISTANCE+_seller.BYPASS_DISTANCE_ADD)
		{
			_fail = 1;
			return;
		}
		else if(_buyer.getTradeList() == null)
		{
			_fail = 2;
			return;
		}
		else if(!_seller.getPlayerAccess().UseTrade)
		{
			_seller.sendPacket(Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
			_fail = 1;
			return;
		}

		_buyerlist = _buyer.getBuyList();

		TradeItem temp;
		long sum = 0;
		for(int i = 0; i < _count; i++)
		{
			temp = new TradeItem();

			readD(); // ObjectId, не работает, поскольку используется id вещи-образца скупщика
			temp.setItemId(readD());
			readH();
			readH();
			temp.setCount(readQ());
			temp.setOwnersPrice(readQ());

			if(temp.getItemId() < 1 || temp.getCount() < 1 || temp.getOwnersPrice() < 1)
			{
				_seller.sendPacket(Msg.ActionFail, Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
				_fail = 1;
				return;
			}

			sum += temp.getCount() * temp.getOwnersPrice();

			L2ItemInstance SIItem = null;
			try
			{
				L2ItemInstance[] SIItemList = _seller.getInventory().getAllItemsById(temp.getItemId());

				if(SIItemList == null || SIItemList.length <= 0)
				{
					_seller.sendActionFailed();
					_log.warning("Player " + _seller.getName() + " tries to sell to PSB:" + _buyer.getName() + " item not in inventory");
					_fail = 1;
					return;
				}

				for(int i1=0;i1<SIItemList.length;i1++)
					if(SIItemList[i1].canBeTraded(_seller) && canBeCorrectItem(SIItemList[i1], temp))
					{
						SIItem = SIItemList[i1];
						break;
					}

				if(SIItem == null || SIItem.isEquipped())
				{
					_seller.sendPacket(Msg.ActionFail, Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
					_fail = 1;
					return;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			temp.setObjectId(SIItem.getObjectId());

			if(temp.getCount() > SIItem.getCount())
				temp.setCount(SIItem.getCount());

			temp.setEnchantLevel(SIItem.getRealEnchantLevel());
			temp.setAttackElement(SIItem.getAttackElementAndValue());
			temp.setDefenceFire(SIItem.getDefenceFire());
			temp.setDefenceWater(SIItem.getDefenceWater());
			temp.setDefenceWind(SIItem.getDefenceWind());
			temp.setDefenceEarth(SIItem.getDefenceEarth());
			temp.setDefenceHoly(SIItem.getDefenceHoly());
			temp.setDefenceUnholy(SIItem.getDefenceUnholy());
			temp.setAugmentationId(SIItem.getAugmentationId());
			temp.setEnchantOptions(SIItem.getEnchantOptions());
			temp.setVisualId(SIItem._visual_item_id);

			_sellerlist.add(temp);
		}

		L2ItemInstance _cost = _buyer.getInventory().getItemByItemId(ConfigValue.TradeItemId);
		if(_cost == null || _cost.getCount() < sum) // если у продавца не хватает денег - снимать с трейда, ибо нефиг
		{
			_buyer.sendPacket(Msg.ActionFail, Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
			_fail = 2;
			return;
		}
		_fail = 0;
	}

	public boolean canBeCorrectItem(L2ItemInstance item, TradeItem need_item)
	{
		if(item.getRealEnchantLevel() == need_item.getEnchantLevel() && item.getAttackElementAndValue()[0] == need_item.getAttackElement()[0] && item.getAttackElementAndValue()[1] == need_item.getAttackElement()[1] && item.getDefenceFire() == need_item.getDefenceFire() && item.getDefenceWater() == need_item.getDefenceWater() && item.getDefenceWind() == need_item.getDefenceWind() && item.getDefenceEarth() == need_item.getDefenceEarth() && item.getDefenceHoly() == need_item.getDefenceHoly() && item.getDefenceUnholy() == need_item.getDefenceUnholy())
			return true;
		return false;
	}

	@Override
	public void runImpl()
	{
		if(seller_fail || _buyer == null)
		{
			if(_seller != null)
				_seller.sendActionFailed();
			return;
		}

		if(_fail == 2)
		{
			L2TradeList.cancelStore(_buyer);
			return;
		}

		_buyer.getTradeList();
		if(_fail == 1 || _buyer.getPrivateStoreType() != L2Player.STORE_PRIVATE_BUY || !_buyer.getTradeList().buySellItems(_buyer, _buyerlist, _seller, _sellerlist))
		{
			_seller.sendActionFailed();
			return;
		}

		_buyer.saveTradeList();

		// на всякий случай немедленно сохраняем все изменения
		for(L2ItemInstance i : _buyer.getInventory().getItemsList())
			i.updateDatabase(true, true);

		for(L2ItemInstance i : _seller.getInventory().getItemsList())
			i.updateDatabase(true, true);

		if(_buyer.getBuyList().isEmpty())
			L2TradeList.cancelStore(_buyer);

		_buyer.updateStats();
		_seller.sendActionFailed();
	}
}