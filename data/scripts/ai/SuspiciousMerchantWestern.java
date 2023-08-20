//
// Suspicious Merchant - Western Fortress (36211).
//
package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class SuspiciousMerchantWestern extends DefaultAI
{
	static final Location[] points =
	{
		new Location(113798, -18726, -1744),
		new Location(115914, -19179, -2120),
		new Location(117146, -19776, -2416),
		new Location(118384, -20124, -2624),
		new Location(118859, -20021, -2704),
		new Location(117205, -18328, -1816)
	};

	private int current_point = 0;

	public SuspiciousMerchantWestern(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(_def_think)
		{
			doTask();
			return true;
		}

		if(getActor().isMoving)
			return false;
		current_point++;

		if(current_point >= points.length)
		{
			current_point = 0;
			actor.teleToLocation(points[0]);
			return true;
		}

		addTaskMove(points[current_point], false);
		doTask();
		return true;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}