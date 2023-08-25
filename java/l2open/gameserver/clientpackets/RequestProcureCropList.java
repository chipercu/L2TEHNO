package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.CastleManorManager;
import l2open.gameserver.instancemanager.CastleManorManager.CropProcure;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Manor;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2ManorManagerInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Rnd;
import l2open.util.Util;

/**
 * Format: (ch) d [dddd]
 * d: size
 * [
 * d  obj id
 * d  item id
 * d  manor id
 * d  count
 * ]
 */
public class RequestProcureCropList extends L2GameClientPacket
{
	private int _size;
	private long[] _items; // count*4

	@Override
	protected void readImpl()
	{
		_size = readD();
		if(_size * 20 > _buf.remaining() || _size > Short.MAX_VALUE || _size <= 0)
		{
			_size = 0;
			return;
		}
		_items = new long[_size * 4];
		for(int i = 0; i < _size; i++)
		{
			_items[i * 4 + 0] = readD();
			_items[i * 4 + 1] = readD();
			_items[i * 4 + 2] = readD();
			_items[i * 4 + 3] = readQ();
			if(_items[i * 4 + 0] < 1 || _items[i * 4 + 1] < 1 || _items[i * 4 + 2] < 1 || _items[i * 4 + 3] < 1)
			{
				_size = 0;
				_items = null;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(_size < 1 || _items == null)
		{
			player.sendActionFailed();
			return;
		}

		L2Object target = player.getTarget();

		if(!(target instanceof L2ManorManagerInstance))
			target = player.getLastNpc();

		if(!player.isGM() && (target == null || !(target instanceof L2ManorManagerInstance) || !player.isInRange(target, ((L2Character)target).INTERACTION_DISTANCE+((L2Character)target).BYPASS_DISTANCE_ADD)))
			return;

		L2ManorManagerInstance manorManager = (L2ManorManagerInstance) target;

		int currentManorId = manorManager.getCastle().getId();

		// Calculate summary values
		int slots = 0;
		int weight = 0;

		for(int i = 0; i < _size; i++)
		{
			int itemId = (int) _items[i * 4 + 1];
			int manorId = (int) _items[i * 4 + 2];
			long count = _items[i * 4 + 3];

			if(itemId == 0 || manorId == 0 || count == 0)
				continue;
			if(count < 1)
				continue;
			if(count > Integer.MAX_VALUE)
			{
				sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
				return;
			}

			try
			{
				CropProcure crop = CastleManager.getInstance().getCastleByIndex(manorId).getCrop(itemId, CastleManorManager.PERIOD_CURRENT);
				int rewardItemId = L2Manor.getInstance().getRewardItem(itemId, crop.getReward());
				L2Item template = ItemTemplates.getInstance().getTemplate(rewardItemId);
				weight += count * template.getWeight();

				if(!template.isStackable())
					slots += count;
				else if(player.getInventory().getItemByItemId(itemId) == null)
					slots++;
			}
			catch(NullPointerException e)
			{
				continue;
			}
		}

		if(!player.getInventory().validateWeight(weight))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}

		if(!player.getInventory().validateCapacity(slots))
		{
			sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		// Proceed the purchase
		for(int i = 0; i < _size; i++)
		{
			int objId = (int) _items[i * 4 + 0];
			int cropId = (int) _items[i * 4 + 1];
			int manorId = (int) _items[i * 4 + 2];
			long count = _items[i * 4 + 3];

			if(objId == 0 || cropId == 0 || manorId == 0 || count == 0)
				continue;

			if(count < 1)
				continue;

			CropProcure crop = null;

			try
			{
				crop = CastleManager.getInstance().getCastleByIndex(manorId).getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
			}
			catch(NullPointerException e)
			{
				continue;
			}
			if(crop == null || crop.getId() == 0 || crop.getPrice() == 0)
				continue;

			long fee = 0; // fee for selling to other manors

			int rewardItem = L2Manor.getInstance().getRewardItem(cropId, crop.getReward());

			if(count > crop.getAmount())
				continue;

			long sellPrice = count * crop.getPrice();
			long rewardPrice = ItemTemplates.getInstance().getTemplate(rewardItem).getReferencePrice();

			if(rewardPrice == 0)
				continue;

			double reward = ((double) sellPrice) / rewardPrice;
			long rewardItemCount = ((long) reward) + (Rnd.nextDouble() <= reward % 1 ? 1 : 0); // дробную часть округляем с шансом пропорционально размеру дробной части

			if(rewardItemCount < 1)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.FAILED_IN_TRADING_S2_OF_CROP_S1);
				sm.addItemName(cropId);
				sm.addNumber(count);
				player.sendPacket(sm);
				continue;
			}

			if(manorId != currentManorId)
				fee = sellPrice * 5 / 100; // 5% fee for selling to other manor

			if(player.getInventory().getAdena() < fee)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.FAILED_IN_TRADING_S2_OF_CROP_S1);
				sm.addItemName(cropId);
				sm.addNumber(count);
				player.sendPacket(sm, Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				continue;
			}

			// Add item to Inventory and adjust update packet
			L2ItemInstance itemDel = null;
			L2ItemInstance itemAdd = null;
			if(player.getInventory().getItemByObjectId(objId) == null)
				continue;

			// check if player have correct items count
			L2ItemInstance item = player.getInventory().getItemByObjectId(objId);
			if(item.getCount() < count)
				continue;

			if(crop.getId() != item.getItemId())
			{
				Util.handleIllegalPlayerAction(player, "RequestProcureCropList", "packet cheat 1", 2);
				break;
			}

			itemDel = player.getInventory().destroyItem(objId, count, true);
			if(itemDel == null)
				continue;

			if(fee > 0)
				player.getInventory().reduceAdena(fee);
			crop.setAmount(crop.getAmount() - count);
			CastleManager.getInstance().getCastleByIndex(manorId).updateCrop(crop.getId(), crop.getAmount(), CastleManorManager.PERIOD_CURRENT);

			itemAdd = player.getInventory().addItem(rewardItem, rewardItemCount);
			if(itemAdd == null)
				continue;

			// Send System Messages
			player.sendPacket(new SystemMessage(SystemMessage.TRADED_S2_OF_CROP_S1).addItemName(cropId).addNumber(count), SystemMessage.removeItems(cropId, count), SystemMessage.obtainItems(rewardItem, rewardItemCount, 0));

			if(fee > 0)
				player.sendPacket(new SystemMessage(SystemMessage.S1_ADENA_HAS_BEEN_PAID_FOR_PURCHASING_FEES).addNumber(fee), SystemMessage.removeItems(57, fee));
		}

		player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
	}
}