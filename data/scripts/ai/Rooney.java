package ai;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * Blacksmith of Wind Rooney, телепортируется раз в 15 минут по 5м разным точкам FoG.
 *
 * @author SYS
 */
public class Rooney extends DefaultAI
{
	static final Location[] points = { new Location(184022, -117083, -3342), new Location(183516, -118815, -3093),
			new Location(185007, -115651, -1587), new Location(186191, -116465, -1587), new Location(189630, -115611, -1587) };

	private static final long TELEPORT_PERIOD = 15 * 60 * 1000; // 15 min
	private long _lastTeleport = System.currentTimeMillis();

	public Rooney(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || System.currentTimeMillis() - _lastTeleport < TELEPORT_PERIOD)
			return false;

		for(int i = 0; i < points.length; i++)
		{
			Location loc = points[Rnd.get(points.length)];
			if(actor.getLoc().equals(loc))
				continue;

			actor.broadcastSkill(new MagicSkillUse(actor, actor, 4671, 1, 1000, 0));
			ThreadPoolManager.getInstance().schedule(new Teleport(loc), 1000);
			_lastTeleport = System.currentTimeMillis();
			break;
		}
		return true;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}