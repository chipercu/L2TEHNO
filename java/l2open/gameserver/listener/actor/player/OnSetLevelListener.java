package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;

public interface OnSetLevelListener extends PlayerListener
{
	public void onSetLevel(L2Player player, int level);
}
