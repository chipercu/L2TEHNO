//
// Suspicious Merchant - Southern Gludio Fortress (35690).
//
package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

public class SuspiciousMerchantSouthernGludio extends DefaultAI
{
	static final Location[] points = { new Location(-28169, 216864, -3544), new Location(-29028, 215089, -3672),
			new Location(-30888, 213455, -3656), new Location(-31937, 211656, -3656), new Location(-30880, 211006, -3552),
			new Location(-27690, 210004, -3272), new Location(-25784, 210108, -3272), new Location(-21682, 211459, -3272),
			new Location(-18430, 212927, -3704), new Location(-16247, 212795, -3664), new Location(-16868, 214267, -3648),
			new Location(-17263, 215887, -3552), new Location(-18352, 216841, -3504), new Location(-17263, 215887, -3552),
			new Location(-16868, 214267, -3648), new Location(-16247, 212795, -3664), new Location(-18430, 212927, -3704),
			new Location(-21682, 211459, -3272), new Location(-25784, 210108, -3272), new Location(-27690, 210004, -3272),
			new Location(-30880, 211006, -3552), new Location(-31937, 211656, -3656), new Location(-30888, 213455, -3656),
			new Location(-29028, 215089, -3672), new Location(-28169, 216864, -3544) };

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantSouthernGludio(L2Character actor)
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
					case 12:
						wait_timeout = System.currentTimeMillis() + 60000;
						wait = true;
						return true;
					case 24:
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