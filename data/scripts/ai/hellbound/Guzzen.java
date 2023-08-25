package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;

public class Guzzen extends Fighter
{
	public Guzzen(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		CreateOnePrivateEx(32301,"npc", "L2Npc",getActor().getX(), getActor().getY(), getActor().getZ(), 180 * 60 * 1000);
		super.MY_DYING(killer);
	}
}