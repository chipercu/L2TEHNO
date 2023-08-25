package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;

public interface OnDeathListener extends CharListener
{
	public void onDeath(L2Character actor, L2Character killer);
}
