package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ChangeWaitType;
import l2open.gameserver.serverpackets.PrivateStoreMsgBuy;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SetPrivateStoreBuyList extends L2GameClientPacket
{
	// format: cdb, b - array of (dhhdd)
	private int _count;
	private long[] _items; // count * 3

	@Override
	public void readImpl()
	{
		_count = readD();
		if(_count * 40 > _buf.remaining() || _count > Short.MAX_VALUE || _count <= 0)
		{
			_items = null;
			return;
		}
		_items = new long[_count * 12];
		for(int i = 0; i < _count; i++)
		{
			_items[i * 12 + 0] = readD(); // item id

			_items[i * 12 + 3] = readH(); // Заточка
								 readH(); // Не понятно, что это...

			_items[i * 12 + 1] = readQ(); // count
			_items[i * 12 + 2] = readQ(); // price

			if(_items[i * 12 + 0] < 1 || _items[i * 12 + 1] < 1 || _items[i * 12 + 0] < 0)
			{
				_items = null;
				break;
			}

			_items[i * 12 + 4] = readH(); // AttackAttValue
			_items[i * 12 + 5] = readH(); // AttackAttType

			_items[i * 12 + 6] = readH(); // DefAttFire
			_items[i * 12 + 7] = readH(); // DefAttWater
			_items[i * 12 + 8] = readH(); // DefAttWind
			_items[i * 12 + 9] = readH(); // DefAttEarth
			_items[i * 12 + 10] = readH(); // DefAttHoly
			_items[i * 12 + 11] = readH(); // DefAttUnHoly
			if(getClient().isLindvior())
			{
				readH();
				readH();
			}
		}
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(System.currentTimeMillis() - activeChar.getLastSetPrivateStoreBuyListPacket() < ConfigValue.SetPrivateStoreBuyListPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastSetPrivateStoreBuyListPacket();

		if(_items == null || !activeChar.checksForShop(false))
		{
			L2TradeList.cancelStore(activeChar);
			return;
		}
		if(!activeChar.canItemAction())
		{
			L2TradeList.cancelStore(activeChar);
			return;
		}

		int maxSlots = activeChar.getTradeLimit();

		if(_count > maxSlots)
		{
			activeChar.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			L2TradeList.cancelStore(activeChar);
			return;
		}

		TradeItem temp;
		ConcurrentLinkedQueue<TradeItem> listbuy = new ConcurrentLinkedQueue<TradeItem>();
		long totalCost = 0;
		int count = 0;

		outer: for(int x = 0; x < _count; x++)
		{
			int itemId = (int) _items[x * 12 + 0];
			long itemCount = _items[x * 12 + 1];
			long itemPrice = _items[x * 12 + 2];
			int enchant = (int) _items[x * 12 + 3];
			int att_type = (int) _items[x * 12 + 4];
			int att_value = (int) _items[x * 12 + 5];
			int att_type_fa = (int) _items[x * 12 + 6];
			int att_type_wa = (int) _items[x * 12 + 7];
			int att_type_wi = (int) _items[x * 12 + 8];
			int att_type_ea = (int) _items[x * 12 + 9];
			int att_type_ho = (int) _items[x * 12 + 10];
			int att_type_un = (int) _items[x * 12 + 11];

			if(ItemTemplates.getInstance().getTemplate(itemId) == null || itemCount < 1 || itemPrice < 1 || itemId == L2Item.ITEM_ID_ADENA || itemId == ConfigValue.TradeItemId)
				continue;
			L2ItemInstance itemToBay = activeChar.getInventory().getItemByItemInfo(itemId, enchant, att_type == 65534 ? -2 : att_type, att_type == 65534 ? 0 : att_value, att_type_fa, att_type_wa, att_type_wi, att_type_ea, att_type_ho, att_type_un);
			if(itemToBay == null)
				continue;
			L2Item item = ItemTemplates.getInstance().getTemplate(itemId);

			if(item.isStackable())
				for(TradeItem ti : listbuy)
					if(ti.getItemId() == itemId)
					{
						if(ti.getOwnersPrice() == itemPrice)
							ti.setCount(ti.getCount() + itemCount);
						continue outer;
					}
			if(att_type == 65534)
			{
				att_type = -2;
				att_value = 0;
			}
			temp = new TradeItem();
			temp.setItemId(itemId);
			temp.setCount(itemCount);
			temp.setOwnersPrice(itemPrice);

			temp.setEnchantLevel(enchant);
			temp.setAttackElement(new int[]{att_type, att_value});
			temp.setDefenceFire(att_type_fa);
			temp.setDefenceWater(att_type_wa);
			temp.setDefenceWind(att_type_wi);
			temp.setDefenceEarth(att_type_ea);
			temp.setDefenceHoly(att_type_ho);
			temp.setDefenceUnholy(att_type_un);
			temp.setAugmentationId(itemToBay.getAugmentationId());
			temp.setEnchantOptions(itemToBay.getEnchantOptions());
			temp.setVisualId(itemToBay._visual_item_id);
			totalCost += temp.getOwnersPrice() * temp.getCount();
			listbuy.add(temp);
			count++;
		}

		L2ItemInstance _cost = activeChar.getInventory().getItemByItemId(ConfigValue.TradeItemId);
		if(_cost == null || _cost.getCount() < totalCost)
		{
			activeChar.sendPacket(Msg.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
			L2TradeList.cancelStore(activeChar);
			return;
		}

		if(count > 0)
		{
			activeChar.setBuyList(listbuy);
			activeChar.setPrivateStoreType(L2Player.STORE_PRIVATE_BUY);
			activeChar.broadcastPacket(new ChangeWaitType(activeChar, ChangeWaitType.WT_SITTING));
			activeChar.broadcastUserInfo(true);
			activeChar.broadcastPacket(new PrivateStoreMsgBuy(activeChar));
			activeChar.sitDown(false);
			return;
		}

		L2TradeList.cancelStore(activeChar);
	}
}