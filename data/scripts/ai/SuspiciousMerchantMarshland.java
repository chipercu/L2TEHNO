//
// Suspicious Merchant - Marshland Fortress (35966).
//
package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class SuspiciousMerchantMarshland extends DefaultAI
{
	static final Location[] points = { new Location(71436, -58182, -2904), new Location(71731, -56949, -3080),
			new Location(72715, -56729, -3104), new Location(73277, -56055, -3104), new Location(73369, -55636, -3104),
			new Location(74136, -54646, -3104), new Location(73408, -54422, -3104), new Location(72998, -53404, -3136),
			new Location(71661, -52937, -3104), new Location(71127, -52304, -3104), new Location(70225, -52304, -3064),
			new Location(69668, -52780, -3064), new Location(68422, -52407, -3240), new Location(67702, -52940, -3208),
			new Location(67798, -52940, -3232), new Location(66667, -55841, -2840), new Location(67798, -52940, -3232),
			new Location(67702, -52940, -3208), new Location(68422, -52407, -3240), new Location(69668, -52780, -3064),
			new Location(70225, -52304, -3064), new Location(71127, -52304, -3104), new Location(71661, -52937, -3104),
			new Location(72998, -53404, -3136), new Location(73408, -54422, -3104), new Location(74136, -54646, -3104),
			new Location(73369, -55636, -3104), new Location(73277, -56055, -3104), new Location(72715, -56729, -3104),
			new Location(71731, -56949, -3080), new Location(71436, -58182, -2904) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantMarshland(L2Character actor)
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
					case 15:
						wait_timeout = System.currentTimeMillis() + 60000;
						wait = true;
						return true;
					case 27:
						wait_timeout = System.currentTimeMillis() + 30000;
						wait = true;
						return true;
					case 30:
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