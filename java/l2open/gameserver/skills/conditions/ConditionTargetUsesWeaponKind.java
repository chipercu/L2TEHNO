package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;
import l2open.gameserver.templates.L2Weapon;

public class ConditionTargetUsesWeaponKind extends Condition
{
	private final long _weaponMask;

	public ConditionTargetUsesWeaponKind(long weaponMask)
	{
		_weaponMask = weaponMask;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.target == null)
			return false;
		L2Weapon item = env.target.getActiveWeaponItem();
		if(item == null)
			return false;
		return (item.getItemType().mask() & _weaponMask) != 0;
	}
}
