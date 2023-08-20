package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import zones.TullyWorkshopZone;

public class Tully extends Fighter
{
	public Tully(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		TullyWorkshopZone.SpawnTully(getActor());
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		TullyWorkshopZone.DeadTully(getActor());
		super.MY_DYING(killer);
	}
}