package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.CastleManorManager;
import l2open.gameserver.instancemanager.CastleManorManager.SeedProduction;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2ManorManagerInstance;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Log;
import l2open.util.SafeMath;
import l2open.util.Util;

/**
 * Format: cdd[dd]
 * c    // id (0xC5)
 *
 * d    // manor id
 * d    // seeds to buy
 * [
 * d    // seed id
 * d    // count
 * ]
 */
public class RequestBuySeed extends L2GameClientPacket
{
	private int _count, _manorId;

	private long[] _items; // size _count * 2

	@Override
	protected void readImpl()
	{
		_manorId = readD();
		_count = readD();

		if(_count > Short.MAX_VALUE || _count <= 0 || _count * 12 > _buf.remaining())
		{
			_count = 0;
			return;
		}

		_items = new long[_count * 2];

		for(int i = 0; i < _count; i++)
		{
			_items[i * 2 + 0] = readD();
			_items[i * 2 + 1] = readQ();
			if(_items[i * 2 + 0] < 1 || _items[i * 2 + 1] < 1)
			{
				_count = 0;
				_items = null;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		long totalPrice = 0;
		int slots = 0;
		int totalWeight = 0;

		L2Player player = getClient().getActiveChar();
		if(player == null || player.is_block)
			return;

		if(_count < 1 || _items == null)
		{
			player.sendActionFailed();
			return;
		}

		L2Object target = player.getTarget();

		if(!(target instanceof L2ManorManagerInstance))
			target = player.getLastNpc();

		if(!(target instanceof L2ManorManagerInstance))
			return;

		Castle castle = CastleManager.getInstance().getCastleByIndex(_manorId);

		for(int i = 0; i < _count; i++)
		{
			int seedId = (int) _items[i * 2 + 0];
			long count = _items[i * 2 + 1];
			long price = 0;
			long residual = 0;

			SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			price = seed.getPrice();
			residual = seed.getCanProduce();

			if(price <= 0)
				return;

			if(residual < count)
				return;

			try
			{
				totalPrice = SafeMath.safeAddLong(totalPrice, SafeMath.safeMulLong(count, price));
			}
			catch(ArithmeticException e)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Long.MAX_VALUE + " adena worth of goods.\r\n" + e.getMessage(), "", ConfigValue.IllegalActionPunishment);
				return;
			}

			L2Item template = ItemTemplates.getInstance().getTemplate(seedId);
			totalWeight += count * template.getWeight();
			if(!template.isStackable())
				slots += count;
			else if(player.getInventory().getItemByItemId(seedId) == null)
				slots++;
		}

		if(!player.getInventory().validateWeight(totalWeight))
		{
			sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}

		if(!player.getInventory().validateCapacity(slots))
		{
			sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		// Charge buyer
		if(totalPrice < 0 || player.getAdena() < totalPrice)
		{
			sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(totalPrice, true);

		// Adding to treasury for Manor Castle
		castle.addToTreasuryNoTax((int) totalPrice, false, true);
		Log.add(castle.getName() + "|" + (int) totalPrice + "|BuySeed", "treasury");

		// Proceed the purchase
		for(int i = 0; i < _count; i++)
		{
			int seedId = (int) _items[i * 2 + 0];
			long count = _items[i * 2 + 1];
			if(count < 0)
				count = 0;

			// Update Castle Seeds Amount
			SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			seed.setCanProduce(seed.getCanProduce() - count);
			CastleManager.getInstance().getCastleByIndex(_manorId).updateSeed(seed.getId(), seed.getCanProduce(), CastleManorManager.PERIOD_CURRENT);

			// Add item to Inventory and adjust update packet
			player.getInventory().addItem(seedId, count);
			player.sendPacket(SystemMessage.obtainItems(seedId, count, 0));
		}

		player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
	}
}