package bosses;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;
import bosses.EpicBossState.State;

public class SailrenManager extends Functions implements ScriptFile
{
	private static class ActivityTimeEnd extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			sleep();
		}
	}

	private static class CubeSpawn extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			_teleportCube = spawn(new Location(27734, -6838, -1982, 0), TeleportCubeId);
		}
	}

	private static class IntervalEnd extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
		}
	}

	private static class Social extends l2open.common.RunnableImpl
	{
		private int _action;
		private L2NpcInstance _npc;

		public Social(L2NpcInstance npc, int actionId)
		{
			_npc = npc;
			_action = actionId;
		}

		public void runImpl()
		{
			_npc.broadcastPacket(new SocialAction(_npc.getObjectId(), _action));
		}
	}

	private static class onAnnihilated extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			sleep();
		}
	}

	// Do spawn Valakas.
	private static class SailrenSpawn extends l2open.common.RunnableImpl
	{
		private int _npcId;
		private final Location _pos = new Location(27628, -6109, -1982, 44732);

		SailrenSpawn(int npcId)
		{
			_npcId = npcId;
		}

		public void runImpl()
		{
			if(_socialTask != null)
			{
				_socialTask.cancel(false);
				_socialTask = null;
			}

			switch(_npcId)
			{
				case Velociraptor:
					_velociraptor = spawn(new Location(27852, -5536, -1983, 44732), Velociraptor);
					_velociraptor.getAI().addTaskMove(_pos, false);

					if(_socialTask != null)
					{
						_socialTask.cancel(true);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_velociraptor, 2), 6000);
					if(_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(true);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
				case Pterosaur:
					_pterosaur = spawn(new Location(27852, -5536, -1983, 44732), Pterosaur);
					_pterosaur.getAI().addTaskMove(_pos, false);
					if(_socialTask != null)
					{
						_socialTask.cancel(true);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_pterosaur, 2), 6000);
					if(_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(true);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
				case Tyrannosaurus:
					_tyranno = spawn(new Location(27852, -5536, -1983, 44732), Tyrannosaurus);
					_tyranno.getAI().addTaskMove(_pos, false);
					if(_socialTask != null)
					{
						_socialTask.cancel(true);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_tyranno, 2), 6000);
					if(_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(true);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
				case Sailren:
					_sailren = spawn(new Location(27810, -5655, -1983, 44732), Sailren);

					_state.setRespawnDate(getRespawnInterval() + FWS_ACTIVITYTIMEOFMOBS);
					_state.setState(EpicBossState.State.ALIVE);
					_state.update();

					_sailren.setRunning();
					_sailren.getAI().addTaskMove(_pos, false);
					if(_socialTask != null)
					{
						_socialTask.cancel(true);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_sailren, 2), 6000);
					if(_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(true);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
			}
		}
	}

	private static L2NpcInstance _velociraptor;
	private static L2NpcInstance _pterosaur;
	private static L2NpcInstance _tyranno;
	private static L2NpcInstance _sailren;
	private static L2NpcInstance _teleportCube;

	// Tasks.
	private static ScheduledFuture<?> _cubeSpawnTask = null;
	private static ScheduledFuture<?> _monsterSpawnTask = null;
	private static ScheduledFuture<?> _intervalEndTask = null;
	private static ScheduledFuture<?> _socialTask = null;
	private static ScheduledFuture<?> _activityTimeEndTask = null;
	private static ScheduledFuture<?> _onAnnihilatedTask = null;

	private static final int Sailren = 29065;
	private static final int Velociraptor = 22198;
	private static final int Pterosaur = 22199;
	private static final int Tyrannosaurus = 22217;
	private static final int TeleportCubeId = 31759;

	private static EpicBossState _state;
	private static L2Zone _zone;

	private static final boolean FWS_ENABLESINGLEPLAYER = Boolean.TRUE;

	private static final int FWS_ACTIVITYTIMEOFMOBS = 120 * 60000;
	private static final int FWS_FIXINTERVALOFSAILRENSPAWN = 1 * 24 * 60 * 60000;
	private static final int FWS_RANDOMINTERVALOFSAILRENSPAWN = 1 * 24 * 60 * 60000;
	private static final int FWS_INTERVALOFNEXTMONSTER = 60000;

	private static boolean _isAlreadyEnteredOtherParty = false;

	private static boolean Dying = false;

	private static void banishForeigners()
	{
		for(L2Player player : getPlayersInside())
			player.teleToClosestTown();
	}

	private synchronized static void checkAnnihilated()
	{
		if(_onAnnihilatedTask == null && isPlayersAnnihilated())
			_onAnnihilatedTask = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), 5000);
	}

	private static GArray<L2Player> getPlayersInside()
	{
		return getZone().getInsidePlayers();
	}

	private static int getRespawnInterval()
	{
		return (int) (ConfigValue.AltRaidRespawnMultiplier * (FWS_FIXINTERVALOFSAILRENSPAWN + Rnd.get(0, FWS_RANDOMINTERVALOFSAILRENSPAWN)));
	}

	public static L2Zone getZone()
	{
		return _zone;
	}

	private static void init()
	{
		_state = new EpicBossState(Sailren);
		communityboard.CommunityBoardFullStats.addEpic(_state);
		_zone = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702004, false);

		_log.info("SailrenManager: State of Sailren is " + _state.getState() + ".");
		if(!_state.getState().equals(EpicBossState.State.NOTSPAWN))
			setIntervalEndTask();

		Date dt = new Date(_state.getRespawnDate());
		_log.info("SailrenManager: Next spawn date of Sailren is " + dt + ".");
	}

	private static boolean isPlayersAnnihilated()
	{
		for(L2Player pc : getPlayersInside())
			if(!pc.isDead())
				return false;
		return true;
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(self == null)
			return;
		if(self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self.getX(), self.getY()))
			checkAnnihilated();
		else if(self == _velociraptor)
		{
			if(_monsterSpawnTask != null)
				_monsterSpawnTask.cancel(false);
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Pterosaur), FWS_INTERVALOFNEXTMONSTER);
		}
		else if(self == _pterosaur)
		{
			if(_monsterSpawnTask != null)
				_monsterSpawnTask.cancel(false);
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Tyrannosaurus), FWS_INTERVALOFNEXTMONSTER);
		}
		else if(self == _tyranno)
		{
			if(_monsterSpawnTask != null)
				_monsterSpawnTask.cancel(false);
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Sailren), FWS_INTERVALOFNEXTMONSTER);
		}
		else if(self == _sailren)
			onSailrenDie(killer);
	}

	private static void onSailrenDie(L2Character killer)
	{
		if(Dying)
			return;

		Dying = true;
		_state.setRespawnDate(getRespawnInterval());
		_state.setState(EpicBossState.State.INTERVAL);
		_state.update();

		Log.add("Sailren died", "bosses");

		_cubeSpawnTask = ThreadPoolManager.getInstance().schedule(new CubeSpawn(), 10000);
	}

	// Start interval.
	private static void setIntervalEndTask()
	{
		setUnspawn();

		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
			return;
		}

		//init state of Sailren lair.
		if(!_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			_state.setRespawnDate(getRespawnInterval());
			_state.setState(EpicBossState.State.INTERVAL);
			_state.update();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	private static void setUnspawn()
	{
		banishForeigners();

		if(_velociraptor != null)
		{
			_velociraptor.getSpawn().stopRespawn();
			_velociraptor.deleteMe();
			_velociraptor = null;
		}
		if(_pterosaur != null)
		{
			_pterosaur.getSpawn().stopRespawn();
			_pterosaur.deleteMe();
			_pterosaur = null;
		}
		if(_tyranno != null)
		{
			_tyranno.getSpawn().stopRespawn();
			_tyranno.deleteMe();
			_tyranno = null;
		}
		if(_sailren != null)
		{
			_sailren.getSpawn().stopRespawn();
			_sailren.deleteMe();
			_sailren = null;
		}
		if(_teleportCube != null)
		{
			_teleportCube.getSpawn().stopRespawn();
			_teleportCube.deleteMe();
			_teleportCube = null;
		}
		if(_cubeSpawnTask != null)
		{
			_cubeSpawnTask.cancel(false);
			_cubeSpawnTask = null;
		}
		if(_monsterSpawnTask != null)
		{
			_monsterSpawnTask.cancel(false);
			_monsterSpawnTask = null;
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
		if(_activityTimeEndTask != null)
		{
			_activityTimeEndTask.cancel(true);
			_activityTimeEndTask = null;
		}
		if(_onAnnihilatedTask != null)
		{
			_onAnnihilatedTask.cancel(false);
			_onAnnihilatedTask = null;
		}
	}

	private static void sleep()
	{
		setUnspawn();
		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
		}
	}

	public synchronized static void setSailrenSpawnTask()
	{
		if(_monsterSpawnTask == null)
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Velociraptor), FWS_INTERVALOFNEXTMONSTER);
	}

	public static boolean isEnableEnterToLair()
	{
		return _state.getState() == EpicBossState.State.NOTSPAWN;
	}

	public static int canIntoSailrenLair(L2Player pc)
	{
		if((!FWS_ENABLESINGLEPLAYER) && (pc.getParty() == null))
			return 4;
		else if(_isAlreadyEnteredOtherParty)
			return 2;
		else if(_state.getState().equals(EpicBossState.State.NOTSPAWN))
			return 0;
		else if(_state.getState().equals(EpicBossState.State.ALIVE) || _state.getState().equals(EpicBossState.State.DEAD))
			return 1;
		else if(_state.getState().equals(EpicBossState.State.INTERVAL))
			return 3;
		else
			return 0;
	}

	public static void entryToSailrenLair(L2Player pc)
	{
		if(pc.getParty() == null)
			pc.teleToLocation(new Location(27734, -6938, -1982).rnd(0, 80, true));
		else
		{
			GArray<L2Player> members = new GArray<L2Player>();
			for(L2Player mem : pc.getParty().getPartyMembers())
				if(mem != null && !mem.isDead() && mem.isInRange(pc, 1000))
					members.add(mem);
			for(L2Player mem : members)
				mem.teleToLocation(new Location(27734, -6938, -1982).rnd(0, 80, true));
		}
		_isAlreadyEnteredOtherParty = true;
	}

	public void onLoad()
	{
		init();
	}

	public void onReload()
	{
		sleep();
	}

	public void onShutdown()
	{}
}