package ai;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

import l2open.common.*;
import l2open.config.ConfigValue;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Log;

/**
 * AI боса Tiat (ID: 29163).
 *
 * @author SYS
 */
public class Tiat extends Fighter
{
	private static final int TIAT_TRANSFORMATION_SKILL_ID = 5974;
	private static final L2Skill TIAT_TRANSFORMATION_SKILL = SkillTable.getInstance().getInfo(TIAT_TRANSFORMATION_SKILL_ID, 1);
	private boolean _notUsedTransform = true;
	private static final int TRAPS_COUNT = 4;
	private static final Location[] TRAP_LOCS =
	{
		new Location(-248776, 206872, -11968),
		new Location(-252024, 206872, -11968),
		new Location(-251544, 209592, -11968),
		new Location(-249256, 209592, -11968)
	};
	private ArrayList<L2MonsterInstance> _traps = new ArrayList<L2MonsterInstance>(TRAPS_COUNT);
	private ScheduledFuture<?> _trapsSpawnTask;
	private ScheduledFuture<?> _DeadMovieTask;
	private static final long TRAPS_SPAWN_INTERVAL = 3 * 60 * 1000; // 3 мин
	private static final long COLLAPSE_BY_INACTIVITY_INTERVAL = 10 * 60 * 1000; // 10 мин
	private long _lastAttackTime = 0;
	private static final int TRAP_NPC_ID = 18696;
	private static final int TIAT_MINION_ID = 29162;
	private long _lastFactionNotifyTime = 0;

	public Tiat(L2Character actor)
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

		actor.p_block_move(true, null);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		_lastAttackTime = System.currentTimeMillis();

		if(_notUsedTransform && actor.getCurrentHpPercents() < 50)
		{
			actor.p_block_move(false, null);
			_notUsedTransform = false;

			// Если вдруг запущен таск, останавливаем его 
			if(_trapsSpawnTask != null)
			{
				System.out.println("WARNING! Tiat AI: _trapsSpawnTask already running!");
				_trapsSpawnTask.cancel(true);
				_trapsSpawnTask = null;
			}
			_trapsSpawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TrapsSpawnTask(), 1, TRAPS_SPAWN_INTERVAL);

			clearTasks();
			addTaskBuff(actor, TIAT_TRANSFORMATION_SKILL);
		}

		if(System.currentTimeMillis() - _lastFactionNotifyTime > actor.minFactionNotifyInterval)
		{
			for(L2NpcInstance npc : actor.getAroundNpc(10000, 500))
				if(npc.getNpcId() == TIAT_MINION_ID)
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[] { attacker, 1 });

			_lastFactionNotifyTime = System.currentTimeMillis();
		}

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		// Коллапсируем инстанс, если Тиата не били более 10 мин
		if(_lastAttackTime != 0 && _lastAttackTime + COLLAPSE_BY_INACTIVITY_INTERVAL < System.currentTimeMillis())
		{
			final Reflection r = actor.getReflection();

			// Очищаем инстанс, запускаем 5 мин коллапс
			r.clearReflection(5, true);

			// Показываем финальный ролик при фейле серез секунду после очистки инстанса
			ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
				@Override
				public void runImpl()
				{
					for(L2Player pl : r.getPlayers())
						if(pl != null)
							pl.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_FAIL);
				}
			}, 1000);

			return true;
		}

		return super.thinkActive();
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_notUsedTransform = true;
		_lastAttackTime = 0;
		_lastFactionNotifyTime = 0;
		if(_trapsSpawnTask != null)
		{
			_trapsSpawnTask.cancel(true);
			_trapsSpawnTask = null;
		}

		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		// Переключаем SoD в режим сбора атрибут камней
		if(actor.getTemplate().killscount >= ConfigValue.SeedofDestructionOpenKillFirst && actor.getTemplate().killscount % ConfigValue.SeedofDestructionOpenKill == 0)
			if(ServerVariables.getLong("SoD_opened", 0) * 1000L + ConfigValue.SeedofDestructionOpenTime * 60 * 60 * 1000L < System.currentTimeMillis())
				ServerVariables.set("SoD_opened", System.currentTimeMillis() / 1000L);

		final Reflection r = actor.getReflection();
		r.setReenterTime();
		for(L2NpcInstance npc : r.getNpcs())
			if(npc != null)
				npc.deleteMe();

		// Очищаем инстанс, запускаем 5 мин коллапс
		r.clearReflection(5, true);

		// Показываем финальный ролик серез секунду после очистки инстанса
		if(_DeadMovieTask == null)
			_DeadMovieTask = ThreadPoolManager.getInstance().schedule(new MovieTask(r), 1000);
	}
	
	public class MovieTask extends l2open.common.RunnableImpl
	{
		private Reflection _r;
		
		public MovieTask(Reflection r)
		{
			_r = r;
		}
		
		public void runImpl()
		{
			for(L2Player pl : _r.getPlayers())
				if(pl != null)
					pl.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_SUCCESS);
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	public class TrapsSpawnTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || actor.isDead())
				return;
			actor.broadcastPacket(new ExShowScreenMessage("Come out, warriors. Protect Seed of Destruction.", 3000, ScreenMessageAlign.MIDDLE_CENTER, false));
			Reflection r = actor.getReflection();
			for(int index = 0; index < TRAPS_COUNT; index++)
			{
				// Не спауним ловушки, если они уже есть в том месте
				L2MonsterInstance oldTrap = null;
				if(index < _traps.size())
					oldTrap = _traps.get(index);
				if(oldTrap != null && !oldTrap.isDead())
					continue;

				L2MonsterInstance trap = new L2MonsterInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(TRAP_NPC_ID));
				trap.setSpawnedLoc(TRAP_LOCS[index]);
				trap.setReflection(r);
				trap.onSpawn();
				trap.spawnMe(trap.getSpawnedLoc());
				r.addSpawn(trap.getSpawn());
				Log.add("TrapsSpawnTask["+trap+"]["+r+"]: "+trap.getLoc(), "debug_tiat_spawn");
				if(index < _traps.size())
					_traps.remove(index);
				_traps.add(index, trap);
			}
		}
	}
}