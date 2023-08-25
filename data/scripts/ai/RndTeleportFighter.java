package ai;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * Моб использует телепортацию вместо рандом валка.
 *
 * @author SYS
 */
public class RndTeleportFighter extends Fighter
{
	private long _lastTeleport;

	public RndTeleportFighter(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || System.currentTimeMillis() - _lastTeleport < 10000)
			return false;

		boolean randomWalk = actor.hasRandomWalk();
		Location sloc = actor.getSpawnedLoc();
		if(sloc == null)
			return false;

		// Random walk or not?
		if(randomWalk && (!ConfigValue.RndWalk || Rnd.chance(ConfigValue.RndWalkRate)))
			return false;

		if(!randomWalk && actor.isInRangeZ(sloc, ConfigValue.MaxDriftRange))
			return false;

		int x = sloc.x + Rnd.get(-ConfigValue.MaxDriftRange, ConfigValue.MaxDriftRange);
		int y = sloc.y + Rnd.get(-ConfigValue.MaxDriftRange, ConfigValue.MaxDriftRange);
		int z = GeoEngine.getHeight(x, y, sloc.z, actor.getReflection().getGeoIndex());

		if(sloc.z - z > 64)
			return false;

		L2Spawn spawn = actor.getSpawn();
		if(spawn != null && spawn.getLocation() != 0 && !TerritoryTable.getInstance().getLocation(spawn.getLocation()).isInside(x, y))
			return false;

		actor.broadcastSkill(new MagicSkillUse(actor, actor, 4671, 1, 500, 0));
		ThreadPoolManager.getInstance().schedule(new Teleport(new Location(x, y, z)), 500);
		_lastTeleport = System.currentTimeMillis();

		return true;
	}
}