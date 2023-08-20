package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;

public interface OnAttackHitListener extends CharListener
{
	public void onAttackHit(L2Character actor, L2Character attacker);
}
