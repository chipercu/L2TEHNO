//
// Suspicious Merchant - Bayou Fortress (35859).
//
package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class SuspiciousMerchantBayou extends DefaultAI
{
	static final Location[] points = { new Location(190423, 43540, -3656), new Location(189579, 45949, -4240),
			new Location(187058, 43551, -4808), new Location(185916, 41869, -4512), new Location(185292, 39403, -4200),
			new Location(185167, 38401, -4200), new Location(184984, 36863, -4152), new Location(184377, 36425, -4080),
			new Location(185314, 35866, -3936), new Location(185781, 35955, -3832), new Location(186686, 35667, -3752),
			new Location(185781, 35955, -3832), new Location(185314, 35866, -3936), new Location(184377, 36425, -4080),
			new Location(184984, 36863, -4152), new Location(185167, 38401, -4200), new Location(185292, 39403, -4200),
			new Location(185916, 41869, -4512), new Location(187058, 43551, -4808), new Location(189579, 45949, -4240),
			new Location(190423, 43540, -3656) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantBayou(L2Character actor)
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
					case 3:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 10:
						wait_timeout = System.currentTimeMillis() + 60000;
						wait = true;
						return true;
					case 17:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 20:
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