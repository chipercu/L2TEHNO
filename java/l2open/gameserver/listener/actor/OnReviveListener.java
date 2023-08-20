package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;

/**
 * @author VISTALL
 */
public interface OnReviveListener extends CharListener
{
	public void onRevive(L2Character actor);
}
