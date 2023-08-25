package bosses;

import bosses.EpicBossState.State;
import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2BossInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.Earthquake;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Crontab;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public class BaiumManager extends Functions implements ScriptFile
{
	// call Arcangels
	public static class CallArchAngel extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			for(L2Spawn spawn : _angelSpawns)
				_angels.add(spawn.doSpawn(true));
		}
	}

	public static class CheckLastAttack extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(_state.getState().equals(EpicBossState.State.ALIVE))
				if(_lastAttackTime + FWB_LIMITUNTILSLEEP < System.currentTimeMillis())
					sleepBaium();
				else
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000);
		}
	}

	// do spawn teleport cube.
	public static class CubeSpawn extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(_teleportCubeSpawn != null)
				_teleportCube = _teleportCubeSpawn.doSpawn(true);
		}
	}

	public static class EarthquakeTask extends l2open.common.RunnableImpl
	{
		private final L2BossInstance	baium;

		public EarthquakeTask(L2BossInstance _baium)
		{
			baium = _baium;
		}

		@Override
		public void runImpl()
		{
			Earthquake eq = new Earthquake(baium.getLoc(), 40, 5);
			baium.broadcastPacket(eq);
		}
	}

	// at end of interval.
	public static class IntervalEnd extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();

			// statue of Baium respawn.
			_statueSpawn.doSpawn(true);
		}
	}

	// kill pc
	public static class KillPc extends l2open.common.RunnableImpl
	{
		private L2BossInstance	_boss;
		private L2Player		_target;

		public KillPc(L2Player target, L2BossInstance boss)
		{
			_target = target;
			_boss = boss;
		}

		@Override
		public void runImpl()
		{
			L2Skill skill = SkillTable.getInstance().getInfo(4136, 1);
			if(_target != null && skill != null)
			{
				_boss.setTarget(_target);
				_boss.doCast(skill, _target, false);
			}
		}
	}

	// Move at random on after Baium appears.
	public static class MoveAtRandom extends l2open.common.RunnableImpl
	{
		private L2NpcInstance	_npc;
		private Location		_pos;

		public MoveAtRandom(L2NpcInstance npc, Location pos)
		{
			_npc = npc;
			_pos = pos;
		}

		@Override
		public void runImpl()
		{
			if(_npc.getAI().getIntention() == AI_INTENTION_ACTIVE)
				_npc.moveToLocation(_pos, 0, false);
		}
	}

	// action is enabled the boss.
	public static class SetMobilised extends l2open.common.RunnableImpl
	{
		private L2BossInstance	_boss;

		public SetMobilised(L2BossInstance boss)
		{
			_boss = boss;
		}

		@Override
		public void runImpl()
		{
			_boss.p_block_move(false, null);
		}
	}

	// do social.
	public static class Social extends l2open.common.RunnableImpl
	{
		private int				_action;
		private L2NpcInstance	_npc;

		public Social(L2NpcInstance npc, int actionId)
		{
			_npc = npc;
			_action = actionId;
		}

		@Override
		public void runImpl()
		{
			SocialAction sa = new SocialAction(_npc.getObjectId(), _action);
			_npc.broadcastPacket(sa);
		}
	}

	private static GArray<L2NpcInstance>		_angels						= new GArray<L2NpcInstance>();
	private static GArray<L2Spawn>				_angelSpawns				= new GArray<L2Spawn>();
	// tasks.
	private static ScheduledFuture<?>			_callAngelTask				= null;
	private static ScheduledFuture<?>			_cubeSpawnTask				= null;
	private static ScheduledFuture<?>			_intervalEndTask			= null;
	private static ScheduledFuture<?>			_killPcTask					= null;
	private static ScheduledFuture<?>			_mobiliseTask				= null;
	private static ScheduledFuture<?>			_moveAtRandomTask			= null;
	private static ScheduledFuture<?>			_sleepCheckTask				= null;
	private static ScheduledFuture<?>			_socialTask					= null;
	private static ScheduledFuture<?>			_socialTask2				= null;
	private static ScheduledFuture<?>			_activityTimeEndTask		= null;

	private static long							_lastAttackTime				= 0;

	private static GArray<L2NpcInstance>		_monsters					= new GArray<L2NpcInstance>();

	private static FastMap<Integer, L2Spawn>	_monsterSpawn				= new FastMap<Integer, L2Spawn>();

	public static EpicBossState				_state;

	private static L2Spawn						_statueSpawn				= null;
	private static L2NpcInstance				_teleportCube				= null;
	private static L2Spawn						_teleportCubeSpawn			= null;
	private static L2Zone						_zone;

	// location of arcangels.
	private final static Location[]				ANGEL_LOCATION				= new Location[]
	{
			new Location(113004, 16209, 10076, 60242),
			new Location(114053, 16642, 10076, 4411),
			new Location(114563, 17184, 10076, 49241),
			new Location(116356, 16402, 10076, 31109),
			new Location(115015, 16393, 10076, 32760),
			new Location(115481, 15335, 10076, 16241),
			new Location(114680, 15407, 10051, 32485),
			new Location(114886, 14437, 10076, 16868),
			new Location(115391, 17593, 10076, 55346),
			new Location(115245, 17558, 10076, 35536)
	};

	private final static int					ARCHANGEL					= 29021;
	private final static int					BAIUM						= 29020;
	private final static int					BAIUM_NPC					= 29025;

	// location of teleport cube.
	private final static Location				CUBE_LOCATION				= new Location(115203, 16620, 10078, 0);
	private final static Location				STATUE_LOCATION				= new Location(116033, 17447, 10107, 41740);

	private static boolean						Dying						= false;

	private final static int					TELEPORT_CUBE				= 31759;

	private final static int					FWB_LIMITUNTILSLEEP			= 30 * 60000;
	//private final static int					FWB_FIXINTERVALOFBAIUM		= 5 * 24 * 60 * 60000;
	//private final static int					FWB_RANDOMINTERVALOFBAIUM	= 8 * 60 * 60000;
	private final static int					FWB_ACTIVITYTIMEOFBAIUM		= 120 * 60000;

	private static void banishForeigners()
	{
		for(L2Player player : getPlayersInside())
			player.teleToClosestTown();
	}

	// Archangel ascension.
	private static void deleteArchangels()
	{
		for(L2NpcInstance angel : _angels)
			if(angel != null && angel.getSpawn() != null)
			{
				angel.getSpawn().stopRespawn();
				angel.deleteMe();
			}
		_angels.clear();
	}

	private static GArray<L2Player> getPlayersInside()
	{
		return getZone().getInsidePlayersIncludeZ();
	}

	private static long getRespawnInterval(boolean inc, String txt)
	{
		long time = Rnd.get(ConfigValue.FixIntervalOfBaium, ConfigValue.FixIntervalOfBaium + ConfigValue.RandomIntervalOfBaium);
		int index = ServerVariables.getInt("FixRespHourOfBaium", 0);
		// Это не нужно в силу наличия крона, но лень всем клиентам объяснять что и как
		if(ConfigValue.FixRespHourOfBaium.length > index && ConfigValue.FixRespHourOfBaium[index] > -1 || ConfigValue.FixRespDayOfWeekBaium > -1)
		{
			if(ConfigValue.FixRespDayOfWeekBaium > -1)
				time = 7*24*60*60000L;
			final Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis()+time);
			if(ConfigValue.FixRespHourOfBaium.length > index && ConfigValue.FixRespHourOfBaium[index] > -1)
			{
				c.set(Calendar.HOUR_OF_DAY, ConfigValue.FixRespHourOfBaium[index]);
				c.set(Calendar.MINUTE, 00);
				c.set(Calendar.SECOND, 00);
				if(inc)
				{
					index++;
					if(ConfigValue.FixRespHourOfBaium.length <= index)
						index=0;
					ServerVariables.set("FixRespHourOfBaium", String.valueOf(index));
				}
				Log.add("Baium getRespawnInterval["+index+"]["+inc+"]["+txt+"]", "bosses");
			}
			if(ConfigValue.FixRespDayOfWeekBaium > -1)
				c.set(Calendar.DAY_OF_WEEK, ConfigValue.FixRespDayOfWeekBaium);
			return c.getTimeInMillis()+(Rnd.get(ConfigValue.AddRndRespHourOfBaium)*60000);
		}
		return new Crontab(ConfigValue.BaiumCronResp).timeNextUsage(System.currentTimeMillis())+time;
	}

	public static L2Zone getZone()
	{
		return _zone;
	}

	public static void init()
	{
		_state = new EpicBossState(BAIUM);
		communityboard.CommunityBoardFullStats.addEpic(_state);
		_zone = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702001, false);

		try
		{
			L2Spawn tempSpawn;

			// Statue of Baium
			_statueSpawn = new L2Spawn(NpcTable.getTemplate(BAIUM_NPC));
			_statueSpawn.setAmount(1);
			_statueSpawn.setLoc(STATUE_LOCATION);
			_statueSpawn.stopRespawn();

			// Baium
			tempSpawn = new L2Spawn(NpcTable.getTemplate(BAIUM));
			tempSpawn.setAmount(1);
			_monsterSpawn.put(BAIUM, tempSpawn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// Teleport Cube
		try
		{
			L2NpcTemplate Cube = NpcTable.getTemplate(TELEPORT_CUBE);
			_teleportCubeSpawn = new L2Spawn(Cube);
			_teleportCubeSpawn.setAmount(1);
			_teleportCubeSpawn.setLoc(CUBE_LOCATION);
			_teleportCubeSpawn.setRespawnDelay(60);
			_teleportCubeSpawn.setLocation(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// Archangels
		try
		{
			L2NpcTemplate angel = NpcTable.getTemplate(ARCHANGEL);
			L2Spawn spawnDat;
			_angelSpawns.clear();

			// 5 random numbers of 10, no duplicates
			GArray<Integer> random = new GArray<Integer>();
			for(int i = 0; i < 5; i++)
			{
				int r = -1;
				while (r == -1 || random.contains(r))
					r = Rnd.get(10);
				random.add(r);
			}

			for(int i : random)
			{
				spawnDat = new L2Spawn(angel);
				spawnDat.setAmount(1);
				spawnDat.setLoc(ANGEL_LOCATION[i]);
				spawnDat.setRespawnDelay(300000);
				spawnDat.setLocation(0);
				_angelSpawns.add(spawnDat);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		_log.info("BaiumManager: State of Baium is " + _state.getState() + ".");
		if(_state.getState().equals(EpicBossState.State.NOTSPAWN))
			_statueSpawn.doSpawn(true);
		else if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
			_statueSpawn.doSpawn(true);
		}
		else if(_state.getState().equals(EpicBossState.State.INTERVAL) || _state.getState().equals(EpicBossState.State.DEAD))
			setIntervalEndTask();

		Date dt = new Date(_state.getRespawnDate());
		_log.info("Loaded Boss: Baium. Next spawn date: " + dt);
	}

	public static void onBaiumDie(L2Character self)
	{
		if(Dying)
			return;

		setUnspawn(false);
		Dying = true;
		self.broadcastPacket(new PlaySound(1, "BS02_D", 1, 0, self.getLoc()));
		_state.setRespawnDate2(getRespawnInterval(true, "DIE"));
		_state.setState(EpicBossState.State.INTERVAL);
		_state.update();

		Log.add("Baium died", "bosses");

		deleteArchangels();

		_cubeSpawnTask = ThreadPoolManager.getInstance().schedule(new CubeSpawn(), 10000);

		if(ConfigValue.BaiumCustomRewardPlayerCount > 0)
		{
			GArray<L2Player> _players = new GArray<L2Player>();
			_players.addAll(getPlayersInside());

			Announcements.getInstance().announceToAll("Поздравляем они получили частичку души Баюма: ");
			for(int i=0;i<ConfigValue.BaiumCustomRewardPlayerCount && _players.size() > 0;i++)
			{
				L2Player player = _players.remove(Rnd.get(_players.size()));
				if(player != null)
				{
					Announcements.getInstance().announceToAll(player.getName());
					if(ConfigValue.BaiumCustomReward.length > 0)
						for(int i2=0;i2<ConfigValue.BaiumCustomReward.length;i2+=2)
							Functions.addItem(player, (int)ConfigValue.BaiumCustomReward[i2], ConfigValue.BaiumCustomReward[i2+1]);

					int rnd_size = ConfigValue.BaiumCustomRewardRnd.length;
					if(rnd_size > 0)
					{
						long[] reward = ConfigValue.BaiumCustomRewardRnd[Rnd.get(rnd_size)];
						Functions.addItem(player, (int)reward[0], reward[1]);
					}
				}
			}
		}
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(self == null)
			return;
		else if(self.isNpc() && self.getNpcId() == BAIUM)
			onBaiumDie(self);
	}

	// start interval.
	private static void setIntervalEndTask()
	{
		setUnspawn(true);

		//init state of Baium's lair.
		if(!_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			_state.setRespawnDate2(getRespawnInterval(false, "INTERVAL"));
			_state.setState(EpicBossState.State.INTERVAL);
			_state.update();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	public static void setLastAttackTime()
	{
		//if(_lastAttackTime + 600000 < System.currentTimeMillis())
		//	mysql.set("UPDATE `bos_debug` SET `attacked`=1 where bos_id=29020");
		_lastAttackTime = System.currentTimeMillis();
	}

	/** Class<?> ends the activity of the Bosses after a interval of time Exits the battle field in any way... */
	private static class ActivityTimeEnd extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			sleepBaium();
		}
	}

	// clean Baium's lair.
	public static void setUnspawn(boolean clearPlayer)
	{
		// eliminate players.
		if(clearPlayer)
			banishForeigners();

		// delete monsters.
		if(clearPlayer)
			deleteArchangels();
		for(L2NpcInstance mob : _monsters)
		{
			mob.getSpawn().stopRespawn();
			mob.deleteMe();
			if(mob.getNpcId() == 29020)
				mob.MPCC_SetMasterPartyRouting(null, 0);
		}
		_monsters.clear();

		// delete teleport cube.
		if(_teleportCube != null)
		{
			_teleportCube.getSpawn().stopRespawn();
			_teleportCube.deleteMe();
			_teleportCube = null;
		}

		// not executed tasks is canceled.
		if(_cubeSpawnTask != null)
		{
			_cubeSpawnTask.cancel(false);
			_cubeSpawnTask = null;
		}
		if(_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
		}
		if(_socialTask != null)
		{
			_socialTask.cancel(false);
			_socialTask = null;
		}
		if(_mobiliseTask != null)
		{
			_mobiliseTask.cancel(false);
			_mobiliseTask = null;
		}
		if(_moveAtRandomTask != null)
		{
			_moveAtRandomTask.cancel(false);
			_moveAtRandomTask = null;
		}
		if(_socialTask2 != null)
		{
			_socialTask2.cancel(false);
			_socialTask2 = null;
		}
		if(_killPcTask != null)
		{
			_killPcTask.cancel(false);
			_killPcTask = null;
		}
		if(_callAngelTask != null)
		{
			_callAngelTask.cancel(false);
			_callAngelTask = null;
		}
		if(_sleepCheckTask != null)
		{
			_sleepCheckTask.cancel(false);
			_sleepCheckTask = null;
		}
		if(_activityTimeEndTask != null)
		{
			_activityTimeEndTask.cancel(true);
			_activityTimeEndTask = null;
		}
		//mysql.set("UPDATE `bos_debug` SET `attacked`=0 where bos_id=29020");
	}

	// Baium sleeps if not attacked for 30 minutes.
	private static void sleepBaium()
	{
		setUnspawn(true);
		Log.add("Baium going to sleep, spawning statue", "bosses");
		_state.setState(EpicBossState.State.NOTSPAWN);
		_state.update();

		// statue of Baium respawn.
		_statueSpawn.doSpawn(true);
	}

	// do spawn Baium.
	public static void spawnBaium(L2Player awake_by)
	{
		Dying = false;
		// do spawn.
		L2Spawn baiumSpawn = _monsterSpawn.get(BAIUM);
		baiumSpawn.setLoc(STATUE_LOCATION);

		final L2BossInstance baium = (L2BossInstance) baiumSpawn.doSpawn(true);
		_monsters.add(baium);

		_state.setRespawnDate2(getRespawnInterval(false, "SPAWN"));
		_state.setState(State.ALIVE);
		_state.update();

		Log.add("Spawned Baium, awake by: " + awake_by, "bosses");

		// set last attack time.
		setLastAttackTime();

		baium.p_block_move(true, null);
		baium.broadcastPacket(new PlaySound(1, "BS02_A", 1, 0, baium.getLoc()));
		if(awake_by != null && awake_by.getParty() != null && awake_by.getParty().getCommandChannel() != null)
			if(awake_by.getParty().getCommandChannel().getMemberCount() > ConfigValue.MinChannelMembersBaium)
				baium.MPCC_SetMasterPartyRouting(awake_by.getParty().getCommandChannel(), 1);
		else
			baium.setMasterPartyRouting(1);
		ThreadPoolManager.getInstance().schedule(new Social(baium, 2), 100);

		_socialTask = ThreadPoolManager.getInstance().schedule(new Social(baium, 3), 15000);

		ThreadPoolManager.getInstance().schedule(new EarthquakeTask(baium), 25000);

		_socialTask2 = ThreadPoolManager.getInstance().schedule(new Social(baium, 1), 25000);
		_killPcTask = ThreadPoolManager.getInstance().schedule(new KillPc(awake_by, baium), 26000);
		_callAngelTask = ThreadPoolManager.getInstance().schedule(new CallArchAngel(), 35000);
		_mobiliseTask = ThreadPoolManager.getInstance().schedule(new SetMobilised(baium), 35500);

		// move at random.
		Location pos = new Location(Rnd.get(112826, 116241), Rnd.get(15575, 16375), 10078, 0);
		_moveAtRandomTask = ThreadPoolManager.getInstance().schedule(new MoveAtRandom(baium, pos), 36000);

		// Set delete task.
		_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000);

		//_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWB_ACTIVITYTIMEOFBAIUM);
		
		L2NpcInstance isbaiumNpc = L2ObjectsStorage.getByNpcId(BAIUM_NPC);
		if(isbaiumNpc != null)
		{
			// delete statue
			isbaiumNpc.getSpawn().stopRespawn();
			isbaiumNpc.deleteMe();
		}
	}

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}
