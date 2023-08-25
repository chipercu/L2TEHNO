//
// Suspicious Merchant - White Sands Fortress (35897).
//
package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class SuspiciousMerchantWhiteSands extends DefaultAI
{
	static final Location[] points = { new Location(114436, 202528, -3408), new Location(113809, 200514, -3720),
			new Location(116035, 199822, -3664), new Location(117017, 199876, -3632), new Location(119959, 201032, -3608),
			new Location(121849, 200614, -3384), new Location(122868, 200874, -3168), new Location(123130, 202427, -3128),
			new Location(122427, 204162, -3488), new Location(122661, 204842, -3576), new Location(124051, 205402, -3576),
			new Location(124211, 206023, -3504), new Location(124948, 206778, -3400), new Location(124483, 207777, -3200),
			new Location(124948, 206778, -3400), new Location(124211, 206023, -3504), new Location(124051, 205402, -3576),
			new Location(122661, 204842, -3576), new Location(122427, 204162, -3488), new Location(123130, 202427, -3128),
			new Location(122868, 200874, -3168), new Location(121849, 200614, -3384), new Location(119959, 201032, -3608),
			new Location(117017, 199876, -3632), new Location(116035, 199822, -3664), new Location(113809, 200514, -3720),
			new Location(114436, 202528, -3408) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantWhiteSands(L2Character actor)
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

		if(System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if(!wait)
				switch(current_point)
				{
					case 0:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 7:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 13:
						wait_timeout = System.currentTimeMillis() + 60000;
						wait = true;
						return true;
					case 19:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 26:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
				}

			wait_timeout = 0;
			wait = false;
			current_point++;

			if(current_point >= points.length)
				current_point = 0;

			addTaskMove(points[current_point], false);
			doTask();
			return true;
		}

		if(randomAnimation())
			return true;

		return false;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}