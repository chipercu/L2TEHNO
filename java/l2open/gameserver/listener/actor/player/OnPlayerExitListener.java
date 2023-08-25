package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;

public interface OnPlayerExitListener extends PlayerListener
{
	public void onPlayerExit(L2Player L2Player);
}
