package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Player;

public interface OnAutoSoulShotListener extends CharListener
{
	void onAutoSoulShot(L2Player actor, int itemId, boolean enable);
}
