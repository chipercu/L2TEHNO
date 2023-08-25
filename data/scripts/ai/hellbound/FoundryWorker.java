package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

public class FoundryWorker extends Fighter
{
	public FoundryWorker(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(attacker != null)
		{
			Location pos = Location.findPointToStay(actor, 150, 250);
			if(GeoEngine.canMoveToCoord(attacker.getX(), attacker.getY(), attacker.getZ(), pos.x, pos.y, pos.z, actor.getReflection().getGeoIndex()))
			{
				actor.setRunning();
				addTaskMove(pos, false);
			}
		}
	}

	@Override
	public void checkAggression(L2Character target)
	{
		super.checkAggression(target);
	}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}