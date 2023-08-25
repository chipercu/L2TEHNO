package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;
import l2open.util.Log;

public class RequestUnEquipItem extends L2GameClientPacket
{
	private int _slot;

	/**
	 * packet type id 0x16
	 * format:		cd
	 */
	@Override
	public void readImpl()
	{
		_slot = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		// Нельзя снимать проклятое оружие и флаги
		if((_slot == L2Item.SLOT_R_HAND || _slot == L2Item.SLOT_L_HAND || _slot == L2Item.SLOT_LR_HAND) && (activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped()))
			return;

		L2ItemInstance item = null;
		try
		{
			activeChar.getInventory().getPaperdollItem(_slot);
		}
		catch(Exception e)
		{
			Log.addBot("Char: " + activeChar.getName() + "["+activeChar.getLoc()+"][" + _slot + "]", "other", "un_equip_item");
		}
		if(item == null || activeChar.getEventMaster() != null && !activeChar.getEventMaster().canUseItem(activeChar, item))
			return;

		activeChar.getInventory().unEquipItemInBodySlotAndNotify(_slot, null, true);
	}
}