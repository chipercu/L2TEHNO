package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;

public interface OnCurrentMpReduceListener extends CharListener
{
	void onCurrentMpReduce(L2Character p0, double p1, L2Character p2);
}
