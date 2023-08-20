package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;

public interface OnSetPrivateStoreType extends PlayerListener
{
	public void onSetPrivateStoreType(L2Player player, short type);
}
