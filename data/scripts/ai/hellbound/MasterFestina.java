package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * Через 10 сек после смерти активирует зону перехода на следующий этаж Tylly's Workshop.
 * Контролирует спаун охраны.
 * @author SYS
 */
public class MasterFestina extends Fighter
{
	private static L2Zone _zone;
	private static Location[] _mysticSpawnPoints;
	private static Location[] _spiritGuardSpawnPoints;
	private final static int FOUNDRY_MYSTIC_ID = 22387;
	private final static int FOUNDRY_SPIRIT_GUARD_ID = 22389;
	private long _lastFactionNotifyTime = 0;

	public MasterFestina(L2Character actor)
	{
		super(actor);

		_zone = ZoneManager.getInstance().getZoneById(ZoneType.dummy, 797552);

		_mysticSpawnPoints = new Location[] { new Location(-11480, 273992, -11768), new Location(-11128, 273992, -11864),
				new Location(-10696, 273992, -11936), new Location(-12552, 274920, -11752),
				new Location(-12568, 275320, -11864), new Location(-12568, 275784, -11936),
				new Location(-13480, 273880, -11752), new Location(-13880, 273880, -11864),
				new Location(-14328, 273880, -11936), new Location(-12456, 272968, -11752),
				new Location(-12456, 272552, -11864), new Location(-12456, 272120, -11936) };

		_spiritGuardSpawnPoints = new Location[] { new Location(-12552, 272168, -11936),
				new Location(-12552, 272520, -11872), new Location(-12552, 272984, -11744),
				new Location(-13432, 273960, -11736), new Location(-13864, 273960, -11856),
				new Location(-14296, 273976, -11936), new Location(-12504, 275736, -11936),
				new Location(-12472, 275288, -11856), new Location(-12472, 274888, -11744),
				new Location(-11544, 273912, -11752), new Location(-11160, 273912, -11856),
				new Location(-10728, 273896, -11936) };
	}

	@Override
	protected void onEvtSpawn()
	{
		ThreadPoolManager.getInstance().schedule(new spawnMasterFestina(), 10000);
	}

	private class spawnMasterFestina extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || !actor.isVisible())
				return;
			// Спауним охрану
			for(Location loc : _mysticSpawnPoints)
			{
				L2MonsterInstance mob = new L2MonsterInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(FOUNDRY_MYSTIC_ID));
				mob.setSpawnedLoc(loc);
				mob.onSpawn();
				mob.spawnMe(loc);
			}
			for(Location loc : _spiritGuardSpawnPoints)
			{
				L2MonsterInstance mob = new L2MonsterInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(FOUNDRY_SPIRIT_GUARD_ID));
				mob.setSpawnedLoc(loc);
				mob.onSpawn();
				mob.spawnMe(loc);
			}
			setZoneInactive();
		}
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(System.currentTimeMillis() - _lastFactionNotifyTime > actor.minFactionNotifyInterval)
		{
			for(L2NpcInstance npc : actor.getAroundNpc(3000, 500))
				if(npc.getNpcId() == FOUNDRY_MYSTIC_ID || npc.getNpcId() == FOUNDRY_SPIRIT_GUARD_ID)
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[] { attacker, Rnd.get(1, 100) });

			_lastFactionNotifyTime = System.currentTimeMillis();
		}

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_lastFactionNotifyTime = 0;
		super.MY_DYING(killer);
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		// Удаляем охрану
		for(L2NpcInstance npc : actor.getAroundNpc(3000, 500))
			if(npc.getNpcId() == FOUNDRY_MYSTIC_ID || npc.getNpcId() == FOUNDRY_SPIRIT_GUARD_ID)
				npc.deleteMe();

		setZoneActive();
		super.MY_DYING(killer);
	}

	private void setZoneActive()
	{
		_zone.setActive(true);
	}

	private void setZoneInactive()
	{
		_zone.setActive(false);
	}
}