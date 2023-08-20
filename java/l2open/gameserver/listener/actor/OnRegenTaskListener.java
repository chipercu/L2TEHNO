package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;

public interface OnRegenTaskListener extends CharListener
{
	public void onAddCp(L2Character actor, double addCp);

	public void onAddHp(L2Character actor, double addHp);

	public void onAddMp(L2Character actor, double addMp);

}
