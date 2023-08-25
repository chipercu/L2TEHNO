package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;

public interface OnCurrentHpDamageListener extends CharListener
{
	public void onCurrentHpDamage(L2Character actor, double damage, L2Character attacker, L2Skill skill, boolean crit);
}
