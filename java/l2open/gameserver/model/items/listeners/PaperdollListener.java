package l2open.gameserver.model.items.listeners;

import l2open.gameserver.model.items.L2ItemInstance;

public interface PaperdollListener
{
	public void notifyEquipped(int slot, L2ItemInstance inst, boolean update_icon);

	public void notifyUnequipped(int slot, L2ItemInstance inst, boolean update_icon);
}
