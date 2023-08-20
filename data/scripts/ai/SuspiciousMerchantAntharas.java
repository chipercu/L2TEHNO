//
// Suspicious Merchant - Antharas Fortress (36173).
//
package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class SuspiciousMerchantAntharas extends DefaultAI
{
	static final Location[] points = 
	{
		new Location(80966, 93990, -3152),
		new Location(81471, 93692, -3256),
		new Location(82631, 92393, -3456),
		new Location(84648, 91353, -3520),
		new Location(85623, 89739, -3288),
		new Location(83768, 88400, -3368),
		new Location(81597, 86574, -3400),
		new Location(80952, 85862, -3456),
		new Location(79186, 85996, -3584),
		new Location(78307, 85729, -3624),
		new Location(78087, 86850, -3480)
	};

	private int current_point = 0;

	public SuspiciousMerchantAntharas(L2Character actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
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
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}