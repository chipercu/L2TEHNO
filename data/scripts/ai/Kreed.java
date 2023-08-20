package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class Kreed extends DefaultAI
{
	static final Location[] points = { new Location(23436, 11164, -3728), new Location(20256, 11104, -3728),
			new Location(17330, 13579, -3720), new Location(17415, 13044, -3736), new Location(20153, 12880, -3728),
			new Location(21621, 13349, -3648), new Location(20686, 10432, -3720), new Location(22426, 10260, -3648),
			new Location(23436, 11164, -3728) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Kreed(L2Character actor)
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
					case 3:
						wait_timeout = System.currentTimeMillis() + 15000;
						wait = true;
						return true;
					case 7:
						wait_timeout = System.currentTimeMillis() + 60000;
						Functions.npcSay(actor, "The Mass of Darkness will start in a couple of days. Pay more attention to the guard!");
						wait = true;
						return true;
				}

			wait_timeout = 0;
			wait = false;
			current_point++;

			if(current_point >= points.length)
				current_point = 0;

			addTaskMove(points[current_point], true);
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