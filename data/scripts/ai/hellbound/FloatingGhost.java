package ai.hellbound;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

public class FloatingGhost extends Fighter
{
	public FloatingGhost(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor.isMoving)
			return false;

		randomWalk();
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		L2NpcInstance actor = getActor();
		Location sloc = actor.getSpawnedLoc();
		Location pos = Location.findPointToStay(actor, sloc, 50, 300);
		if(GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getReflection().getGeoIndex()))
		{
			actor.setRunning();
			addTaskMove(pos, false);
		}

		return true;
	}
}