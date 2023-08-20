package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;

public interface OnAttackListener extends CharListener
{
	public void onAttack(L2Character actor, L2Character target);
}
