package ai.dragonvalley;

import l2open.config.ConfigValue;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.*;

public class DrakeRunners extends Patrollers
{
	public DrakeRunners(L2NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(148984, 112952, -3720),
			new Location(149160, 114312, -3720),
			new Location(149096, 115480, -3720),
			new Location(147720, 116216, -3720),
			new Location(146536, 116296, -3720),
			new Location(145192, 115304, -3720),
			new Location(144888, 114504, -3720),
			new Location(145240, 113272, -3720),
			new Location(145960, 112696, -3720),
			new Location(147416, 112488, -3720),
			new Location(148104, 112696, -3720)
		};
		MaxPursueRange = 6000;
	}

	protected boolean maybeMoveToHome()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;

		boolean randomWalk = actor.hasRandomWalk();
		Location sloc = actor.getSpawnedLoc();
		if(sloc == null)
			return false;

		// Моб попал на другой этаж
		if(Math.abs(sloc.z - actor.getZ()) > 128 && !isGlobalAI())
		{
			teleportHome(true);
			return true;
		}

		// Random walk or not?
		if(randomWalk && (!ConfigValue.RndWalk || !Rnd.chance(ConfigValue.RndWalkRate)))
			return false;

		boolean isInRange = actor.isInRangeZ(sloc, ConfigValue.MaxDriftRange);

		if(!randomWalk && isInRange)
			return false;

		int x = sloc.x + Rnd.get(-ConfigValue.MaxDriftRange, ConfigValue.MaxDriftRange);
		int y = sloc.y + Rnd.get(-ConfigValue.MaxDriftRange, ConfigValue.MaxDriftRange);
		int z = GeoEngine.getHeight(x, y, sloc.z, actor.getReflection().getGeoIndex());

		if(Math.abs(sloc.z - z) > 64)
			return false;

		L2Spawn spawn = actor.getSpawn();
		if(spawn != null && spawn.getLocation() > 0 && !TerritoryTable.getInstance().getLocation(spawn.getLocation()).isInside(x, y))
			return false;

		if(spawn != null && spawn.getLocation2() != null)
			for(L2Territory terr : spawn.getLocation2())
				if(!terr.isInside(x, y))
					return false;

		actor.setWalking();

		// Телепортируемся домой, только если далеко от дома
		if(!actor.moveToLocation(x, y, z, 0, false) && !isInRange)
			teleportHome(true);

		return true;
	}

	public boolean isNotReturnHome()
	{
		return false;
	}
}
