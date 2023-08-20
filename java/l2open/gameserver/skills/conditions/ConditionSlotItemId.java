package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.skills.Env;

public final class ConditionSlotItemId extends ConditionInventory
{
	private final int _itemId;

	private final int _enchantLevel;

	public ConditionSlotItemId(short slot, int itemId, int enchantLevel)
	{
		super(slot);
		_itemId = itemId;
		_enchantLevel = enchantLevel;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		Inventory inv = ((L2Player) env.character).getInventory();
		L2ItemInstance item = inv.getPaperdollItem(_slot);
		if(item == null)
			return _itemId == 0;
		return item.getItemId() == _itemId && item.getEnchantLevel() >= _enchantLevel;
	}
}
