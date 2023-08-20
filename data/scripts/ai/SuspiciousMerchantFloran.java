//
// Suspicious Merchant - Floran Fortress (36035).
//
package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class SuspiciousMerchantFloran extends DefaultAI
{
	static final Location[] points = { new Location(14186, 149947, -3352), new Location(16180, 150387, -3216),
			new Location(18387, 151874, -3317), new Location(18405, 154770, -3616), new Location(17655, 156863, -3664),
			new Location(12303, 153937, -2680), new Location(17655, 156863, -3664), new Location(18405, 154770, -3616),
			new Location(18387, 151874, -3317), new Location(16180, 150387, -3216), new Location(14186, 149947, -3352) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantFloran(L2Character actor)
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
						wait_timeout = System.currentTimeMillis() + 20000;
						wait = true;
						return true;
					case 3:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 7:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 10:
						wait_timeout = System.currentTimeMillis() + 20000;
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