package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;

public interface OnSetClassListener extends PlayerListener
{
	public void onSetClass(L2Player actor, int class_id);
}
