package l2open.status;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;

public class DummyL2Object extends L2Object
{
	public DummyL2Object()
	{
		super(-1);
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
}