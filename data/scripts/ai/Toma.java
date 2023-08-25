package ai;

import l2open.config.ConfigValue;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * Master Toma, телепортируется раз в 30 минут по 3м разным точкам гномьего острова.
 *
 * @author SYS
 */
public class Toma extends DefaultAI
{
	private Location[] _points = { new Location(151680, -174891, -1807, 41400), new Location(154153, -220105, -3402),
			new Location(178834, -184336, -352) };
	private static long TELEPORT_PERIOD = 30 * 60 * 1000; // 30 min
	private long _lastTeleport = System.currentTimeMillis();

	public Toma(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if(System.currentTimeMillis() - _lastTeleport < TELEPORT_PERIOD)
			return false;

		L2NpcInstance _thisActor = getActor();

		Location loc = _points[Rnd.get(_points.length)];
		if(_thisActor.getLoc().equals(loc))
			return false;

		_thisActor.broadcastSkill(new MagicSkillUse(_thisActor, _thisActor, 4671, 1, 1000, 0));
		ThreadPoolManager.getInstance().schedule(new Teleport(loc), 1000);
		_lastTeleport = System.currentTimeMillis();

		return true;
	}

	public static void onLoad()
	{
		if(ConfigValue.AltTeleportingToma)
		{
			for(L2NpcInstance i : L2ObjectsStorage.getAllByNpcId(30556, false))
				i.setAI(new Toma(i));
			NpcTable.getTemplate(30556).ai_type = "Toma";
		}
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}