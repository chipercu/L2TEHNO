package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class Rogin extends DefaultAI
{
	static final Location[] points =
	{
		new Location(115756, -183472, -1480), new Location(115866, -183287, -1480),
		new Location(116280, -182918, -1520), new Location(116587, -184306, -1568),
		new Location(116392, -184090, -1560), new Location(117083, -182538, -1528),
		new Location(117802, -182541, -1528), new Location(116720, -182479, -1528),
		new Location(115857, -183295, -1480), new Location(115756, -183472, -1480)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Rogin(L2Character actor)
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
			if(doTask())
				clearTasks();
			return true;
		}

		if(System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if(!wait)
				switch(current_point)
				{
					case 3:
						wait_timeout = System.currentTimeMillis() + 15000;
						Functions.npcSay(actor, "Have you seen Torocco today?");
						wait = true;
						return true;
					case 6:
						wait_timeout = System.currentTimeMillis() + 15000;
						Functions.npcSay(actor, "Have you seen Torocco?");
						wait = true;
						return true;
					case 7:
						wait_timeout = System.currentTimeMillis() + 15000;
						Functions.npcSay(actor, "Where is that fool hiding?");
						wait = true;
						return true;
					case 8:
						wait_timeout = System.currentTimeMillis() + 60000;
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