package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;

public interface OnMagicUseListener extends CharListener
{
	public void onMagicUse(L2Character actor, L2Skill skill, L2Character target, boolean alt);
}
