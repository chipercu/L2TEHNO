package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;

public interface OnMagicHitListener extends CharListener
{
	public void onMagicHit(L2Character actor, L2Skill skill, L2Character caster);
}
