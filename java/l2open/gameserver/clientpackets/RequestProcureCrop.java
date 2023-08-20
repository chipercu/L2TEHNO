package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManorManager;
import l2open.gameserver.instancemanager.CastleManorManager.CropProcure;
import l2open.gameserver.model.L2Manor;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2ManorManagerInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.SafeMath;

@SuppressWarnings("unused")
// TODO
public class RequestProcureCrop extends L2GameClientPacket
{
	// format: cddb
	private int _listId;
	private int _count;
	private long[] _items;
	private GArray<CropProcure> _procureList = new GArray<CropProcure>();

	@Override
	protected void readImpl()
	{
		_listId = readD();
		_count = readD();
		if(_count * 16 > _buf.remaining() || _count > Short.MAX_VALUE || _count <= 0)
		{
			_count = 0;
			return;
		}
		_items = new long[_count * 2];
		for(int i = 0; i < _count; i++)
		{
			readD(); // service
			_items[i * 2 + 0] = readD();

			long count = readQ();
			if(count < 1)
			{
				_count = 0;
				_items = null;
				return;
			}

			_items[i * 2 + 1] = count;
		}
	}

	@Override
	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(_count < 1 || _items == null)
		{
			player.sendActionFailed();
			return;
		}

		// Alt game - Karma punishment
		if(!ConfigValue.AltKarmaPlayerCanShop && player.getKarma() > 0)
			return;

		L2Object target = player.getTarget();

		long subTotal = 0;
		int tax = 0;

		// Check for buylist validity and calculates summary values
		int slots = 0;
		int weight = 0;
		L2ManorManagerInstance manor = target != null && target instanceof L2ManorManagerInstance ? (L2ManorManagerInstance) target : null;

		for(int i = 0; i < _count; i++)
		{
			int itemId = (int) _items[i * 2 + 0];
			long count = _items[i * 2 + 1];
			int price = 0;
			if(count < 0)
			{
				sendPacket(Msg.INCORRECT_ITEM_COUNT);
				return;
			}

			Castle castle = manor.getCastle();
			if(castle == null)
				return;

			CropProcure crop = castle.getCrop(itemId, CastleManorManager.PERIOD_CURRENT);
			if(crop == null)
				return;

			int rewardId = L2Manor.getInstance().getRewardItem(itemId, crop.getReward());

			L2Item template = ItemTemplates.getInstance().getTemplate(rewardId);
			weight += count * template.getWeight();

			long add_slot = 0;
			if(!template.isStackable())
				add_slot = count;
			else if(player.getInventory().getItemByItemId(itemId) == null)
				add_slot = 1;

			if(add_slot > 0)
			{
				try
				{
					slots = SafeMath.safeAddInt(slots, add_slot);
				}
				catch(ArithmeticException e)
				{
					sendPacket(Msg.INCORRECT_ITEM_COUNT);
					return;
				}
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
		_procureList = manor.getCastle().getCropProcure(CastleManorManager.PERIOD_CURRENT);

		for(int i = 0; i < _count; i++)
		{
			int itemId = (int) _items[i * 2 + 0];
			long count = _items[i * 2 + 1];
			if(count < 0)
				count = 0;

			int rewradItemId = L2Manor.getInstance().getRewardItem(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());
			long rewradItemCount = L2Manor.getInstance().getRewardAmountPerCrop(manor.getCastle().getId(), itemId, manor.getCastle().getCropRewardType(itemId));

			rewradItemCount = count * rewradItemCount;

			// Add item to Inventory and adjust update packet
			L2ItemInstance item = player.getInventory().addItem(rewradItemId, rewradItemCount);
			L2ItemInstance iteme = player.getInventory().destroyItemByItemId(itemId, count, true);

			if(item == null || iteme == null)
				continue;

			// Send Char Buy Messages
			player.sendPacket(SystemMessage.obtainItems(rewradItemId, rewradItemCount, 0));

			//manor.getCastle().setCropAmount(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getAmount() - count);
		}

		player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
	}
}