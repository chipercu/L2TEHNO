package l2open.gameserver.listener.actor.player;

import l2open.gameserver.listener.PlayerListener;
import l2open.gameserver.model.L2Player;

/**
 * @author VISTALL
 * @date 9:37/15.04.2011
 */
public interface OnAnswerListener extends PlayerListener
{
	void sayYes(L2Player L2Player);

	void sayNo(L2Player L2Player);
}
