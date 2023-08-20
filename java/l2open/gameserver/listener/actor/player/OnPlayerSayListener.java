package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;

public interface OnPlayerSayListener extends PlayerListener
{
	void onSay(L2Player activeChar, int type, String target, String text);
}
