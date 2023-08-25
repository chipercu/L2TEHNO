package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;

public interface OnPlayerPartyLeaveListener extends PlayerListener
{
	public void onPartyLeave(L2Player L2Player);
}
