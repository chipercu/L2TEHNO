package l2open.gameserver.model.items;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.util.GArray;
import l2open.util.Log;

import java.sql.ResultSet;
import java.util.logging.Level;

public class PcInventory extends Inventory
{
	protected final L2Player _owner;

	public PcInventory(L2Player owner)
	{
		_owner = owner;
	}

	@Override
	public L2Player getOwner()
	{
		return _owner;
	}

	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.INVENTORY;
	}

	@Override
	public ItemLocation getEquipLocation()
	{
		return ItemLocation.PAPERDOLL;
	}

	public int getSize(boolean quest)
	{
		if (quest)
			return _questSlots;
		else
			return getSize();
	}

	public long getAdena()
	{
		L2ItemInstance _adena = getItemByItemId(57);
		if(_adena == null)
			return 0;
		return _adena.getCount();
	}

	/**
	 * Get all augmented items
	 */
	public GArray<L2ItemInstance> getAugmentedItems()
	{
		GArray<L2ItemInstance> list = new GArray<L2ItemInstance>();
		for(L2ItemInstance item : getItems())
			if(item != null && item.isAugmented())
				list.add(item);
		return list;
	}

	/**
	 * Добавляет адену игроку.<BR><BR>
	 * @param amount - сколько адены дать
	 * @return L2ItemInstance - новое количество адены
	 */
	public L2ItemInstance addAdena(long amount)
	{
		L2ItemInstance _adena = addItem(57, amount);
		Log.LogItem(getOwner(), Log.Sys_GetItem, _adena);
		return _adena;
	}

	public L2ItemInstance reduceAdena(long adena)
	{
		return destroyItemByItemId(57, adena, true);
	}

	public static int[][] restoreVisibleInventory(int objectId)
	{
		int[][] paperdoll = new int[Inventory.PAPERDOLL_MAX][3];
		ThreadConnection con = null;
		FiltredPreparedStatement statement2 = null;
		ResultSet invdata = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement2 = con.prepareStatement("SELECT object_id,item_id,loc_data,enchant_level FROM items WHERE owner_id=? AND loc='PAPERDOLL'");
			statement2.setInt(1, objectId);
			invdata = statement2.executeQuery();

			while(invdata.next())
			{
				int slot = invdata.getInt("loc_data");
				paperdoll[slot][0] = invdata.getInt("object_id");
				paperdoll[slot][1] = invdata.getInt("item_id");
				paperdoll[slot][2] = invdata.getInt("enchant_level");
			}
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not restore inventory:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement2, invdata);
		}
		return paperdoll;
	}

	public boolean validateCapacity(L2ItemInstance item)
	{
		int slots = getSize();

		if(!(item.isStackable() && getItemByItemId(item.getItemId()) != null))
			slots++;

		return validateCapacity(slots);
	}

	public boolean validateCapacity(GArray<L2ItemInstance> items)
	{
		int slots = getSize();

		for(L2ItemInstance item : items)
			if(!(item.isStackable() && getItemByItemId(item.getItemId()) != null))
				slots++;

		return validateCapacity(slots);
	}

	public boolean validateCapacity(int slots)
	{
		return validateCapacity(slots, false);
	}

	public boolean validateCapacity(int slots, boolean questItem)
	{
		if (!questItem)
			return slots <= _owner.getInventoryLimit();
		else
			return _questSlots <= _owner.getQuestInventoryLimit();
	}

	public int slotsLeft()
	{
		L2Player owner = getOwner();
		if(owner == null)
			return 0;
		int slots = owner.getInventoryLimit() - getSize();
		return slots > 0 ? slots : 0;
	}

	public boolean validateWeight(GArray<L2ItemInstance> items)
	{
		long weight = 0;
		for(L2ItemInstance item : items)
			weight += item.getItem().getWeight() * item.getCount();
		return validateWeight(weight);
	}

	public boolean validateWeight(L2ItemInstance item)
	{
		long weight = item.getItem().getWeight() * item.getCount();
		return validateWeight(weight);
	}

	public boolean validateWeight(long weight)
	{
		L2Player owner = getOwner();
		if(owner == null)
			return false;
		return getTotalWeight() + weight <= owner.getMaxLoad();
	}

	@Override
	public void restore()
	{
		super.restore();
	}
}