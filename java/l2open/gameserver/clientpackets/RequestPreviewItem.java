package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ItemList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.SafeMath;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestPreviewItem extends L2GameClientPacket
{
	// format: cdddb
	protected static Logger _log = Logger.getLogger(RequestPreviewItem.class.getName());

	protected Future<?> _removeWearItemsTask;

	@SuppressWarnings("unused")
	private int _unknow;
	@SuppressWarnings("unused")
	private int _listId;
	private int _count;
	private short[] _items; // count*2
	protected L2Player _cha;

	class RemoveWearItemsTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			try
			{
				L2ItemInstance[] items = _cha.getInventory().getItems();
				for(L2ItemInstance i : items)
					if(i.isWear())
					{
						if(i.isEquipped())
							_cha.getInventory().unEquipItemInSlot(i.getEquipSlot());
						_cha.getInventory().destroyItem(i.getObjectId(), 1, true);
					}
				_cha.broadcastUserInfo(true);
				_cha.sendPacket(Msg.TRYING_ON_MODE_HAS_ENDED, new ItemList(_cha, false));
			}
			catch(Throwable e)
			{
				_log.log(Level.SEVERE, "", e);
			}
		}
	}

	@Override
	public void readImpl()
	{
		_cha = getClient().getActiveChar();
		_unknow = readD();
		_listId = readD();
		_count = readD();
		if(_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count <= 0)
		{
			_count = 0;
			return;
		}
		//    _items = new int[_count * 2];
		_items = new short[_count];
		for(int i = 0; i < _count; i++)
			_items[i] = (short) readD();
		//      int cnt      = readD(); _items[i * 2 + 1] = cnt;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = _cha;
		if(System.currentTimeMillis() - activeChar.getLastRequestPreviewItemPacket() < ConfigValue.RequestPreviewItemPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestPreviewItemPacket();

		if(!ConfigValue.WearTestEnabled)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPreviewItem.Disabled", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if(!ConfigValue.AltKarmaPlayerCanShop && activeChar.getKarma() > 0 && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2NpcInstance npc = activeChar.getLastNpc();

		boolean isValidMerchant = npc instanceof L2ClanHallManagerInstance || npc instanceof L2MerchantInstance || npc instanceof L2MercManagerInstance || npc instanceof L2CastleChamberlainInstance;

		if(!activeChar.isGM() && (npc == null || !isValidMerchant || !activeChar.isInRange(npc.getLoc(), npc.INTERACTION_DISTANCE+npc.BYPASS_DISTANCE_ADD)))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(_count < 1)
		{
			activeChar.sendActionFailed();
			return;
		}

		GArray<L2ItemInstance> items = new GArray<L2ItemInstance>(_count);
		for(int i = 0; i < _count; i++)
		{
			short itemId = _items[i];
			int cnt = 1;
			L2ItemInstance inst = ItemTemplates.getInstance().createItem(itemId);
			inst.setCount(cnt);
			items.add(inst);
		}

		//TODO check if valid buylist, stackable items ?

		long neededMoney = 0;
		long finalLoad = 0;
		int finalCount = activeChar.getInventory().getSize();
		int needsSpace = 2;
		int weight = 0;
		long currentMoney = activeChar.getAdena();

		for(L2ItemInstance item : items)
		{
			int itemId = item.getItemId();
			long cnt = item.getCount();
			int price;
			if(item.getItem().isStackable())
			{
				needsSpace = 1;
				if(activeChar.getInventory().getItemByItemId(itemId) != null)
					needsSpace = 0;
			}
			//L2TradeList list = TradeController.getInstance().getBuyList(_listId);
			price = 10;
			weight = item.getItem().getWeight();
			try
			{
				neededMoney = SafeMath.safeAddLong(neededMoney, SafeMath.safeMulLong(cnt, price));
				finalLoad = SafeMath.safeAddLong(finalLoad, SafeMath.safeMulLong(cnt, weight));
			}
			catch(ArithmeticException e)
			{
				for(L2ItemInstance i : items)
					i.deleteMe();
				_log.warning("Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to purchase over " + Long.MAX_VALUE + " adena worth of goods: " + e.getMessage());
				activeChar.sendActionFailed();
				return;
			}

			if(needsSpace == 2)
				finalCount += cnt;
			else if(needsSpace == 1)
				finalCount += 1;
		}

		if(neededMoney > currentMoney || neededMoney < 0 || currentMoney <= 0)
		{
			sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);

			for(L2ItemInstance i : items)
				i.deleteMe();

			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateWeight(finalLoad))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);

			for(L2ItemInstance i : items)
				i.deleteMe();

			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getInventory().validateCapacity(finalCount))
		{
			sendPacket(Msg.YOUR_INVENTORY_IS_FULL);

			for(L2ItemInstance i : items)
				i.deleteMe();

			activeChar.sendActionFailed();
			return;
		}

		activeChar.reduceAdena(neededMoney, true);

		for(L2ItemInstance item : items)
		{
			item.setWear(true);
			activeChar.getInventory().addItem(item);
			activeChar.getInventory().equipItem(item, true);
		}
		activeChar.broadcastUserInfo(true);

		sendPacket(new ItemList(activeChar, false), SystemMessage.removeItems(57, neededMoney));

		if(_removeWearItemsTask == null)
			_removeWearItemsTask = ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(), 10000, true);
	}
}