package ai.hellbound;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import zones.TullyWorkshopZone;

/**
 * User: Drizzy
 * Date: 09.05.12
 * Time: 20:59
 */
public class Pillar extends DefaultAI
{
	public Pillar(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		TullyWorkshopZone.onSpawnPillar(getActor());
		super.onEvtSpawn();
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		TullyWorkshopZone.onDeadPillar(getActor());
		super.MY_DYING(killer);
	}
}
