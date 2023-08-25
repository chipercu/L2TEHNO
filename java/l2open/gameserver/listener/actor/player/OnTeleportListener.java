package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;

public interface OnTeleportListener extends PlayerListener
{
	public void onTeleport(L2Player player, int x, int y, int z, int reflection);
}
