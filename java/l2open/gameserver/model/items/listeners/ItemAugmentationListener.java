package l2open.gameserver.model.items.listeners;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;

public final class ItemAugmentationListener implements PaperdollListener
{
	private Inventory _inv;

	public ItemAugmentationListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		if(item.isAugmented())
		{
			L2Player player = _inv.getOwner().getPlayer();
			item.getAugmentation().removeBoni(player, false);
		}
		if(item.getItem().getAttachedTriggers() != null)
		{
			L2Player player = _inv.getOwner().getPlayer();
			player.removeTrigger(item.getItem().getAttachedTriggers());
		}
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		if(item.isAugmented())
		{
			L2Player player = _inv.getOwner().getPlayer();
			item.getAugmentation().applyBoni(player, false);
		}

		if(item.getItem().getAttachedTriggers() != null)
		{
			L2Player player = _inv.getOwner().getPlayer();
			player.addTrigger(item.getItem().getAttachedTriggers());
		}
	}
}