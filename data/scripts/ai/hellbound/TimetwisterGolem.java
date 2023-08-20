package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import zones.TullyWorkshopZone;

/**
 * User: Drizzy
 * Date: 09.05.12
 * Time: 16:13
 */
public class TimetwisterGolem extends Fighter
{
	public TimetwisterGolem(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		TullyWorkshopZone.DeadTimetwisterGolem(getActor());
		super.MY_DYING(killer);
	}
}
