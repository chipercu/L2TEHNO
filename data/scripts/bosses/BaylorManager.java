package bosses;

import bosses.EpicBossState.State;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.FlyToLocation;
import l2open.gameserver.serverpackets.FlyToLocation.FlyType;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.*;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public class BaylorManager extends Functions implements ScriptFile
{

    private static L2NpcInstance spawn(int npcId, Location loc, int refId)
	{
            L2NpcInstance npc = NpcTable.getTemplate(npcId).getNewInstance();
            npc.setReflection(refId);
            npc.setSpawnedLoc(loc);
            npc.onSpawn();
            npc.spawnMe(npc.getSpawnedLoc());
            return npc;
	}

	private static class ActivityTimeEnd extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			setIntervalEndTask();
		}
	}

	private static class BaylorSpawn extends l2open.common.RunnableImpl
	{
		private int _npcId;
		private Location _pos = new Location(153569, 142075, -12711, 44732);

		public BaylorSpawn(int npcId)
		{
			_npcId = npcId;
		}

		public void runImpl()
		{
			switch(_npcId)
			{
				case CrystalPrisonGuard:

					Reflection ref = ReflectionTable.getInstance().get(currentReflection);

                    for(int doorId : doors)
                       ref.openDoor(doorId);

					for(int i = 0; i < _crystalineLocation.length; i++)
					{
						_crystaline[i] = spawn(CrystalPrisonGuard, _crystalineLocation[i], currentReflection);
						_crystaline[i].setRunning();
						_crystaline[i].moveToLocation(_pos, 300, false);
						ThreadPoolManager.getInstance().schedule(new Social(_crystaline[i], 2), 15000);
					}

					break;
				case Baylor:
					Dying = false;

					_baylor = spawn(Baylor, new Location(153569, 142075, -12732, 59864), currentReflection);

					_state.setRespawnDate(getRespawnInterval() + FWBA_ACTIVITYTIMEOFMOBS);
					_state.setState(EpicBossState.State.ALIVE);
					_state.update();

					if(_socialTask != null)
					{
						_socialTask.cancel(true);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_baylor, 1), 500);

					if(_endSceneTask != null)
					{
						_endSceneTask.cancel(true);
						_endSceneTask = null;
					}
					_endSceneTask = ThreadPoolManager.getInstance().schedule(new EndScene(), 23000);

					if(_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(true);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWBA_ACTIVITYTIMEOFMOBS);

					break;
			}
		}
	}

	// Interval end.
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

	private static class EndScene extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			for(L2Player player : getPlayersInside())
			{
				//player.unblock();
				if(_baylor != null)
				{
					double angle = Util.convertHeadingToDegree(_baylor.getHeading());
					double radian = Math.toRadians(angle - 90);
					int x1 = -(int) (Math.sin(radian) * 600);
					int y1 = (int) (Math.cos(radian) * 600);
					Location flyLoc = GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), player.getX() + x1, player.getY() + y1, player.getReflection().getGeoIndex());
					player.setLoc(flyLoc);
					player.broadcastPacket(new FlyToLocation(player, flyLoc, FlyType.THROW_HORIZONTAL));
				}
			}
			for(int i = 0; i < _crystaline.length; i++)
			{
				L2NpcInstance npc = _crystaline[i];
				if(npc != null)
					npc.reduceCurrentHp(npc.getMaxHp() + 1, npc, null, true, true, false, false, false,npc.getMaxHp() + 1, true, false, false, false);
			}
		}
	}

	private static final int Baylor = 29186;
	private static final int CrystalPrisonGuard = 29100;
	private static final int Parme = 32271;
	private static final int Oracle = 32273;

	private static final Location _crystalineLocation[] = { new Location(154404, 140596, -12711, 44732),
			new Location(153574, 140402, -12711, 44732), new Location(152105, 141230, -12711, 44732),
			new Location(151877, 142095, -12711, 44732), new Location(152109, 142920, -12711, 44732),
			new Location(152730, 143555, -12711, 44732), new Location(154439, 143538, -12711, 44732),
			new Location(155246, 142068, -12711, 44732) };

	private static final Location _baylorChestLocation[] = { new Location(153763, 142075, -12741, 64792),
			new Location(153701, 141942, -12741, 57739), new Location(153573, 141894, -12741, 49471),
			new Location(153445, 141945, -12741, 41113), new Location(153381, 142076, -12741, 32767),
			new Location(153441, 142211, -12741, 25730), new Location(153573, 142260, -12741, 16185),
			new Location(153706, 142212, -12741, 7579), new Location(153571, 142860, -12741, 16716),
			new Location(152783, 142077, -12741, 32176), new Location(153571, 141274, -12741, 49072),
			new Location(154365, 142073, -12741, 64149), new Location(154192, 142697, -12741, 7894),
			new Location(152924, 142677, -12741, 25072), new Location(152907, 141428, -12741, 39590),
			new Location(154243, 141411, -12741, 55500) };

	private static final int[] doors = { 24220009, 24220011, 24220012, 24220014, 24220015, 24220016, 24220017, 24220019 };

	// Instance of monsters
	private static L2NpcInstance[] _crystaline = new L2NpcInstance[8];

	private static L2NpcInstance _baylor;
	// Tasks
	private static ScheduledFuture<?> _intervalEndTask = null;
	private static ScheduledFuture<?> _activityTimeEndTask = null;
	private static ScheduledFuture<?> _socialTask = null;
	private static ScheduledFuture<?> _endSceneTask = null;

	// State of baylor's lair.
	private static boolean _isAlreadyEnteredOtherParty = false;

	private static EpicBossState _state;
	private static L2Zone _zone;

	private static final int FWBA_ACTIVITYTIMEOFMOBS = 120 * 60000;
	private static final int FWBA_FIXINTERVALOFBAYLORSPAWN = 1440 * 60000;
	private static final int FWBA_RANDOMINTERVALOFBAYLORSPAWN = 1440 * 60000;

	private static final boolean FWBA_ENABLESINGLEPLAYER = false;

	private static boolean Dying = false;

	private static int currentReflection;

	// Whether it is permitted to enter the baylor's lair is confirmed. 
	public static int canIntoBaylorLair(L2Player pc)
	{
		if(pc.isGM())
			return 0;
		if(!FWBA_ENABLESINGLEPLAYER && !pc.isInParty())
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

private synchronized static void checkAnnihilated()
	{
		if(isPlayersAnnihilated())
			setIntervalEndTask();
	}

	// Teleporting player to baylor's lair.
	public synchronized static void entryToBaylorLair(L2Player pc, L2NpcInstance myself)
	{

		currentReflection = pc.getReflection().getId();
		ReflectionTable.getInstance().get(currentReflection).closeDoor(24220008);
		ThreadPoolManager.getInstance().schedule(new BaylorSpawn(CrystalPrisonGuard), 20000);
		ThreadPoolManager.getInstance().schedule(new BaylorSpawn(Baylor), 40000);

		L2Party party0 = pc.getParty();
		int i1 = party0.getMemberCount();
		for(int i2 = 0; i2 < i1;i2++)
		{
			L2Player c0 = party0.getPartyMember(i2);
			if(myself.OwnItemCount(c0,9695) >= 1 && myself.OwnItemCount(c0,9696) >= 1 && myself.OwnItemCount(c0,9697) >= 1)
			{
				int i3 = Rnd.get(100);
				if( i3 <= 33 )
					myself.DeleteItem1(c0,9695,1);
				else if( i3 <= 66 )
					myself.DeleteItem1(c0,9696,1);
				else
					myself.DeleteItem1(c0,9697,1);

				if(i2 == 0 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153526,142172,-12743,22324);
				else if(i2 == 1 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153600,142184,-12743,12828);
				else if(i2 == 2 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153650,142140,-12743,6252);
				else if(i2 == 3 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153680,142076,-12743,0);
				else if(i2 == 4 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153653,142013,-12743,-5556);
				else if(i2 == 5 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153591,141969,-12743,-12036);
				else if(i2 == 6 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153529,141983,-12743,-18372);
				else if(i2 == 7 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153471,142039,-12743,-31088);
				else if(i2 == 8 && myself.DistFromMe(c0) < 1000)
					c0.teleToLocation(153468,142113,-12743,27912);
				//c0.block();
			}
		}
		/*if(pc.getParty() == null)
		{
			pc.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
			pc.block();
		}
		else
		{
			GArray<L2Player> members = new GArray<L2Player>(); // list of member of teleport candidate.
			for(L2Player mem : pc.getParty().getPartyMembers())
				// teleporting it within alive and the range of recognition of the leader of the party. 
				if(!mem.isDead() && mem.isInRange(pc, 1500))
					members.add(mem);
			for(L2Player mem : members)
			{
				mem.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
				mem.block();
			}
		}*/
		_isAlreadyEnteredOtherParty = true;
	}

	private static GArray<L2Player> getPlayersInside()
	{
		GArray<L2Player> result = new GArray<L2Player>();
		for(L2Player player : getZone().getInsidePlayers())
			if(player.getReflection().getId() == currentReflection)
				result.add(player);
		return result;
	}

	private static int getRespawnInterval()
	{
		return (int) (ConfigValue.AltRaidRespawnMultiplier * (FWBA_FIXINTERVALOFBAYLORSPAWN + Rnd.get(0, FWBA_RANDOMINTERVALOFBAYLORSPAWN)));
	}

	public static L2Zone getZone()
	{
		return _zone;
	}

	private static void init()
	{
		_state = new EpicBossState(Baylor);
		communityboard.CommunityBoardFullStats.addEpic(_state);
		_zone = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702101, false);

		_isAlreadyEnteredOtherParty = false;

		Log.add("BaylorManager : State of Baylor is " + _state.getState() + ".", "bosses");
		if(!_state.getState().equals(EpicBossState.State.NOTSPAWN))
			setIntervalEndTask();

		Date dt = new Date(_state.getRespawnDate());
		Log.add("BaylorManager : Next spawn date of Baylor is " + dt + ".", "bosses");
		Log.add("BaylorManager : Init BaylorManager.", "bosses");
	}

	private static boolean isPlayersAnnihilated()
	{
		for(L2Player pc : getPlayersInside())
			if(!pc.isDead())
				return false;
		return true;
	}

	private static void onBaylorDie()
	{
		if(Dying)
			return;

		Dying = true;
		_state.setRespawnDate(getRespawnInterval());
		_state.setState(EpicBossState.State.INTERVAL);
		_state.update();

		Log.add("Baylor died", "bosses");

		for(Location loc : _baylorChestLocation)
			spawn(29116 + Rnd.get(2), loc, currentReflection);

		spawn(Parme, new Location(153570, 142067, -9727, 55500), currentReflection);
		spawn(Oracle, new Location(153569, 142075, -12732, 55500), currentReflection);

		startCollapse();
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(self == null || !_isAlreadyEnteredOtherParty || self.getReflection().getId() != currentReflection)
			return;
		if(self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self.getX(), self.getY()))
			checkAnnihilated();
		else if(self.isNpc() && self.getNpcId() == Baylor)
			onBaylorDie();
	}

	// Task of interval of baylor spawn.
	private static void setIntervalEndTask()
	{
		setUnspawn();

		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
			return;
		}

		if(!_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			_state.setRespawnDate(getRespawnInterval());
			_state.setState(EpicBossState.State.INTERVAL);
			_state.update();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	// Clean up Baylor's lair.
	private static void setUnspawn()
	{
		if(!_isAlreadyEnteredOtherParty)
			return;
		_isAlreadyEnteredOtherParty = false;

		startCollapse();

		if(_baylor != null)
			_baylor.deleteMe();
		_baylor = null;

		for(L2NpcInstance npc : _crystaline)
			if(npc != null)
				npc.deleteMe();

		if(_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
		}
		if(_activityTimeEndTask != null)
		{
			_activityTimeEndTask.cancel(false);
			_activityTimeEndTask = null;
		}
	}

	private static void startCollapse()
	{
		if(currentReflection > 0)
		{
			Reflection reflection = ReflectionTable.getInstance().get(currentReflection);
			if(reflection != null)
				reflection.startCollapseTimer(300000);
			currentReflection = 0;
		}
	}

	public void onLoad()
	{
		init();
	}

	public void onReload()
	{
		setUnspawn();
	}

	public void onShutdown()
	{}
}