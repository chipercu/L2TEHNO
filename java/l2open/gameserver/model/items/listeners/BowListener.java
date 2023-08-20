package l2open.gameserver.model.items.listeners;

import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon.WeaponType;

public final class BowListener implements PaperdollListener
{
	private Inventory _inv;

	public BowListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(slot != Inventory.PAPERDOLL_RHAND || _inv.isRefreshingListeners() || !item.isEquipable())
			return;
		if(item.getItemType() == WeaponType.BOW || item.getItemType() == WeaponType.CROSSBOW || item.getItemType() == WeaponType.ROD)
			_inv.unEquipItemInBodySlotAndNotify(L2Item.SLOT_L_HAND, null, true);
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(slot != Inventory.PAPERDOLL_RHAND || _inv.isRefreshingListeners() || !item.isEquipable())
			return;
		if(item.getItemType() == WeaponType.BOW)
		{
			L2ItemInstance arrow = _inv.findArrowForBow(item.getItem());
			if(arrow != null)
				_inv.setPaperdollItem(Inventory.PAPERDOLL_LHAND, arrow);
		}
		if(item.getItemType() == WeaponType.CROSSBOW)
		{
			L2ItemInstance bolt = _inv.findArrowForCrossbow(item.getItem());
			if(bolt != null)
				_inv.setPaperdollItem(Inventory.PAPERDOLL_LHAND, bolt);
		}
		if(item.getItemType() == WeaponType.ROD)
		{
			L2ItemInstance bait = _inv.findEquippedLure();
			if(bait != null)
				_inv.setPaperdollItem(Inventory.PAPERDOLL_LHAND, bait);
		}
	}
}