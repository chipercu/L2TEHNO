package ai;

import java.util.concurrent.ScheduledFuture;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.reference.*;

/**
 * AI Dimension Moving Device в Seed of Destruction:
 *
 * Из ловушки спаунятся мобы с задержкой в 3 сек через 5 сек после спауна в следующей последовательности:
 * Dragon Steed Troop Commander
 * White Dragon Leader
 * Dragon Steed Troop Healer (not off-like)
 * Dragon Steed Troop Magic Leader
 * Dragon Steed Troop Javelin Thrower
 *
 * @author SYS
 */
public class DimensionMovingDevice extends DefaultAI
{
	private static final int TIAT_NPC_ID = 29163;
	private static final int INIT_DELAY = 5 * 1000; // 5 сек
	private static final int MOBS_DELAY = 3 * 1000; // 3 сек
	private static final int MOBS_WAVE_DELAY = 90 * 1000; // 1.5 мин между волнами мобов

	private static final int[] MOBS = { 22538, // Dragon Steed Troop Commander
			22540, // White Dragon Leader
			22547, // Dragon Steed Troop Healer
			22542, // Dragon Steed Troop Magic Leader
			22548 // Dragon Steed Troop Javelin Thrower
	};

	private static final Location TIATROOM_LOC = new Location(-250408, 208568, -11968);
	private ScheduledFuture<?> _mobssSpawnTask;
	private long _search_timeout = 0;
	private static long _spawnTime = 0; // static для всех ловушек
	private HardReference<? extends L2NpcInstance> _tiat_ref = HardReferences.emptyRef();

	public DimensionMovingDevice(L2Character actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		_spawnTime = System.currentTimeMillis();

		if(_mobssSpawnTask == null)
			_mobssSpawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MobssSpawnTask(), INIT_DELAY, MOBS_WAVE_DELAY);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(_mobssSpawnTask != null)
		{
			_mobssSpawnTask.cancel(true);
			_mobssSpawnTask = null;
		}

		_spawnTime = 0;

		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(checkAllDestroyed(actor.getNpcId(), actor.getReflection().getId()))
		{
			L2NpcInstance tiat = findTiat(actor.getReflection().getId());
			if(tiat != null && !tiat.isDead())
				tiat.broadcastPacket(new ExShowScreenMessage("You'll regret challenging me!!!!", 3000, ScreenMessageAlign.MIDDLE_CENTER, false));
		}

		super.MY_DYING(killer);
	}

	/**
	 * Возвращает L2NpcInstance боса Tiat в отражении actor-а.
	 * Если не найден, возвращает null.
	 */
	private L2NpcInstance findTiat(long refId)
	{
		L2NpcInstance _tiat = _tiat_ref.get();
		if(_tiat != null)
			return _tiat;

		// Ищем Тиата не чаще, чем раз в 10 секунд, если по каким-то причинам его нету
		if(System.currentTimeMillis() > _search_timeout)
		{
			_search_timeout = System.currentTimeMillis() + 10000;
			for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(TIAT_NPC_ID, true))
				if(npc.getReflection().getId() == refId)
				{
					_tiat_ref = npc.getRef();
					return npc;
				}
		}
		return null;
	}

	/**
	 * Проверяет, уничтожены ли все Dimension Moving Device в текущем измерении
	 * @return true если все уничтожены
	 */
	private static boolean checkAllDestroyed(int mobId, long refId)
	{
		for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(mobId, true))
			if(npc.getReflection().getId() == refId)
				return false;
		return true;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	/**
	 * Таск запускает волну спауна мобов
	 */
	public class MobssSpawnTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || actor.isDead())
				return;

			// Не кричим при первой волне мобов
			L2NpcInstance tiat = findTiat(actor.getReflection().getId());
			if(tiat != null && !tiat.isDead() && _spawnTime + MOBS_WAVE_DELAY < System.currentTimeMillis())
				tiat.broadcastPacket(new ExShowScreenMessage("Whoaaaaaa!!!!", 3000, ScreenMessageAlign.MIDDLE_CENTER, false));

			long delay = 0;
			for(int mobId : MOBS)
			{
				ThreadPoolManager.getInstance().schedule(new SpawnerTask(mobId), delay);
				delay += MOBS_DELAY;
			}
		}
	}

	/**
	 * Таск спаунит мобов в волне
	 */
	public class SpawnerTask extends l2open.common.RunnableImpl
	{
		private int _mobId;

		public SpawnerTask(int mobId)
		{
			_mobId = mobId;
		}

		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || actor.isDead())
				return;

			Reflection r = actor.getReflection();
			L2MonsterInstance mob = new L2MonsterInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(_mobId));
			mob.setSpawnedLoc(actor.getLoc());
			mob.setReflection(r);
			mob.onSpawn();
			mob.spawnMe(mob.getSpawnedLoc());

			mob.setRunning();
			mob.getAI().setMaxPursueRange(20000);
			mob.getAI().setGlobalAggro(0);

			// После спауна мобы бегут к тиату или в фортресс, если тиата еще нету
			L2NpcInstance tiat = findTiat(r.getId());
			Location homeLoc;
			if(tiat != null && !tiat.isDead())
			{
				homeLoc = Rnd.coordsRandomize(tiat.getLoc(), 200, 500);
				mob.setSpawnedLoc(homeLoc);
				mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, tiat.getRandomHated(), 1);
				mob.getAI().addTaskMove(homeLoc, true);
			}
			else
			{
				homeLoc = Rnd.coordsRandomize(TIATROOM_LOC, 200, 500);
				mob.setSpawnedLoc(homeLoc);
				mob.getAI().addTaskMove(homeLoc, true);
			}
		}
	}
}