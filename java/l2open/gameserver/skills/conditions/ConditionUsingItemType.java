package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.skills.Env;

public final class ConditionUsingItemType extends Condition
{
	private final long _mask;

	public ConditionUsingItemType(long mask)
	{
		_mask = mask;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.isPlayer())
		{
			Inventory inv = ((L2Player) env.character).getInventory();
			return (_mask & inv.getWearedMask()) != 0;
		}
		return env.character.getActiveWeaponItem() == null ? ((_mask & env.character.getFistWeaponType().mask()) != 0) : ((_mask & env.character.getActiveWeaponItem().getItemMask()) != 0);
	}
}
