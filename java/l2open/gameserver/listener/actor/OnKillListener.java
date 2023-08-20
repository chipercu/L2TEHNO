package l2open.gameserver.listener.actor;

import l2open.gameserver.listener.CharListener;
import l2open.gameserver.model.L2Character;

public interface OnKillListener extends CharListener
{
	public void onKill(L2Character actor, L2Character victim);

	/**
	 * FIXME [VISTALL]
	 * Когда на игрока добавить OnKillListener, он не добавляется суммону, и нужно вручну добавлять
	 * но при ресумоне, проследить трудно
	 * Если возратить тру, то с убийцы будет братся игрок, и на нем вызывать onKill
	 * @return
	 */
	public boolean ignorePetOrSummon();
}
