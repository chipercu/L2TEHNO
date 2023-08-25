package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;
import l2open.util.Location;

public interface OnMoveListener extends CharListener
{
	void onMove(final L2Character p0, final Location p1);
}
