package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;

/**
 * @author VISTALL
 * @date 15:37/05.08.2011
 */
public interface OnPlayerSummonServitorListener extends PlayerListener
{
	void onSummonServitor(L2Player L2Player, L2Summon servitor);
}
