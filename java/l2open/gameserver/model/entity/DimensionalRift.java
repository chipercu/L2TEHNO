package l2open.gameserver.model.entity;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.DimensionalRiftManager;
import l2open.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoom;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.Timer;
import java.util.TimerTask;

public class DimensionalRift extends Reflection
{
	protected static final long seconds_5 = 5000L;
	protected static final int MILLISECONDS_IN_MINUTE = 60000;

	protected Integer _roomType;
	GArray<Integer> _completedRooms = new GArray<Integer>();
	int jumps_current = 0;

	protected Timer teleporterTimer;
	protected TimerTask teleporterTimerTask;
	protected Timer spawnTimer;
	protected TimerTask spawnTimerTask;
	protected Timer killRiftTimer;
	protected TimerTask killRiftTimerTask;

	int _choosenRoom = -1;
	protected boolean _hasJumped = false;
	protected boolean isBossRoom = false;

	public DimensionalRift(L2Party party, int type, int room)
	{
		super("Dimensional Rift");

		DimensionalRiftManager.getInstance().getRoom(type, room);
		//System.out.println("DimensionalRift["+type+"]["+room+"]");
		startCollapseTimer(7200000); // 120 минут таймер, для защиты от утечек памяти
		if(this instanceof DelusionChamber)
		{
			FastMap<Integer, InstancedZoneManager.InstancedZone> izs = InstancedZoneManager.getInstance().getById(type + 120);
			if (izs == null)
				return;
			InstancedZoneManager.InstancedZone iz = izs.get(0);
			if (iz == null)
				return;
			setInstancedZone(iz);
			setInstancedZoneId(type + 120);
			setName(iz.getName());
		}
		_roomType = type;
		setParty(party);
		
		if(!(this instanceof DelusionChamber))
			party.setDimensionalRift(this);

		party.setReflection(this);
		_choosenRoom = room;
		checkBossRoom(_choosenRoom);

		Location coords = getRoomCoord(_choosenRoom);

		setReturnLoc(party.getPartyLeader().getLoc());
		setTeleportLoc(coords);
		for(L2Player p : party.getPartyMembers())
		{
			DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(coords, 50, 100, getGeoIndex()), this);
			p.setReflection(this);
		}

		createSpawnTimer(_choosenRoom);
		createTeleporterTimer();
		for(L2Player pl : getPlayers())
			if(pl != null)
				pl.sendPacket(new SystemMessage(SystemMessage.THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES).addNumber(120));

	}

	public int getType()
	{
		return _roomType;
	}

	public int getCurrentRoom()
	{
		return _choosenRoom;
	}

	protected void createTeleporterTimer()
	{
		if(teleporterTimerTask != null)
		{
			teleporterTimerTask.cancel();
			teleporterTimerTask = null;
		}

		if(teleporterTimer != null)
		{
			teleporterTimer.cancel();
			teleporterTimer = null;
		}

		teleporterTimer = new Timer();
		teleporterTimerTask = new TimerTask(){
			@Override
			public void run()
			{
				if(jumps_current < getMaxJumps() && getPlayersInside(true) > 0)
				{
					jumps_current++;
					teleportToNextRoom();
					createTeleporterTimer();
				}
				else
				{
					createNewKillRiftTimer();
					cancel();
				}
			}
		};
		teleporterTimer.schedule(teleporterTimerTask, calcTimeToNextJump()); //Teleporter task, 8-10 minutes
	}

	public void createSpawnTimer(int room)
	{
		if(spawnTimerTask != null)
		{
			spawnTimerTask.cancel();
			spawnTimerTask = null;
		}

		if(spawnTimer != null)
		{
			spawnTimer.cancel();
			spawnTimer = null;
		}

		final DimensionalRiftRoom riftRoom = DimensionalRiftManager.getInstance().getRoom(_roomType, room);

		Quest.addSpawnToInstance(getManagerId(), riftRoom.getTeleportCoords(), 0, _id);

		spawnTimer = new Timer();
		spawnTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				for(L2Spawn s : riftRoom.getSpawns())
				{
					L2Spawn sp = s.clone();
					sp.setReflection(_id);
					addSpawn(sp);
					if(!isBossRoom)
						sp.startRespawn();
					for(int i = 0; i < sp.getAmount(); i++)
						sp.doSpawn(true);
				}
			}
		};


		spawnTimer.schedule(spawnTimerTask, ConfigValue.RiftSpawnDelay);
	}

	public void createNewKillRiftTimer()
	{
		//l2open.util.Util.test();
		if(killRiftTimerTask != null)
		{
			killRiftTimerTask.cancel();
			killRiftTimerTask = null;
		}

		if(killRiftTimer != null)
		{
			killRiftTimer.cancel();
			killRiftTimer = null;
		}

		killRiftTimer = new Timer();
		killRiftTimerTask = new TimerTask(){
			@Override
			public void run()
			{
				if(isCollapseStarted())
					return;
				for(L2Player p : getParty().getPartyMembers())
					if(p != null && p.getReflectionId() == getId())
						DimensionalRiftManager.getInstance().teleportToWaitingRoom(p);
				DimensionalRift.this.collapse();
			}
		};

		killRiftTimer.schedule(killRiftTimerTask, 100);
	}

	public void partyMemberInvited()
	{
		createNewKillRiftTimer();
	}

	public void partyMemberExited(L2Player player)
	{
		if(getParty().getMemberCount() < ConfigValue.RiftMinPartySize || getParty().getMemberCount() == 1 || getPlayersInside(true) == 0)
			createNewKillRiftTimer();
	}

	public void manualTeleport(L2Player player, L2NpcInstance npc)
	{
		if(!player.isInParty() || !player.getParty().isInReflection() || !(player.getParty().getReflection() instanceof DimensionalRift))
			return;

		if(!player.getParty().isLeader(player))
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "/data/html/rift/NotPartyLeader.htm", npc);
			return;
		}

		if(!isBossRoom)
		{
			if(_hasJumped)
			{
				DimensionalRiftManager.getInstance().showHtmlFile(player, "/data/html/rift/AllreadyTeleported.html", npc);
				return;
			}
			_hasJumped = true;
		}
		else
		{
			manualExitRift(player, npc);
			return;
		}

		teleportToNextRoom();
		createTeleporterTimer();
	}

	public void manualExitRift(L2Player player, L2NpcInstance npc)
	{
		if(!player.isInParty() || !player.getParty().isInDimensionalRift())
			return;

		if(!player.getParty().isLeader(player))
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "/data/html/rift/NotPartyLeader.htm", npc);
			return;
		}

		createNewKillRiftTimer();
	}

	protected void teleportToNextRoom()
	{
		//l2open.util.Util.test();
		_completedRooms.add(_choosenRoom);

		for(L2Spawn s : getSpawns())
			s.despawnAll();

		int size = DimensionalRiftManager.getInstance().getRooms(_roomType).size();
		/*
		if(jumps_current < getMaxJumps())
			size--; // комната босса может быть только последней
		*/

		if(getType() >= 11 && jumps_current == getMaxJumps())
			_choosenRoom = 9; // В DC последние 2 печати всегда кончаются рейдом
		else
		{ // выбираем комнату, где еще не были
			GArray<Integer> notCompletedRooms = new GArray<Integer>();
			for(int i = 1; i <= size; i++)
				if(!_completedRooms.contains(i))
					notCompletedRooms.add(i);
			_choosenRoom = notCompletedRooms.get(Rnd.get(notCompletedRooms.size()));
		}

		checkBossRoom(_choosenRoom);
		setTeleportLoc(getRoomCoord(_choosenRoom));

		
		for(L2Player p : getParty().getPartyMembers())
			if(p.getReflection() == this)
				DimensionalRiftManager.teleToLocation(p, getRoomCoord(_choosenRoom).rnd(50, 100, false), this);

		createSpawnTimer(_choosenRoom);
	}

	@Override
	public void collapse()
	{
		TimerTask task = teleporterTimerTask;
		if(task != null)
			task.cancel();
		teleporterTimerTask = null;

		Timer timer = teleporterTimer;
		if(timer != null)
			timer.cancel();
		teleporterTimer = null;

		if((task = spawnTimerTask) != null)
			task.cancel();
		spawnTimerTask = null;

		if((timer = spawnTimer) != null)
			timer.cancel();
		spawnTimer = null;

		if((task = killRiftTimerTask) != null)
			task.cancel();
		killRiftTimerTask = null;

		if((timer = killRiftTimer) != null)
			timer.cancel();
		killRiftTimer = null;

		_completedRooms = null;

		L2Party party = getParty();
		if(party != null)
			party.setDimensionalRift(null);

		super.collapse();
	}

	protected long calcTimeToNextJump()
	{
		if(isBossRoom)
			return 60 * MILLISECONDS_IN_MINUTE;
		return ConfigValue.AutoJumpsDelay * MILLISECONDS_IN_MINUTE + Rnd.get(ConfigValue.AutoJumpsDelayRandom);
	}

	public void memberDead(L2Player player)
	{
		if(getPlayersInside(true) == 0)
			createNewKillRiftTimer();
	}

	public void usedTeleport(L2Player player)
	{
		if(getPlayersInside(false) < ConfigValue.RiftMinPartySize)
			createNewKillRiftTimer();
	}

	public void checkBossRoom(int room)
	{
		isBossRoom = DimensionalRiftManager.getInstance().getRoom(_roomType, room).isBossRoom();
	}

	public Location getRoomCoord(int room)
	{
		return DimensionalRiftManager.getInstance().getRoom(_roomType, room).getTeleportCoords();
	}

	/** По умолчанию 4 */
	public int getMaxJumps()
	{
		//return Math.max(Math.min(4, 8), 1);
		return 4;
	}

	@Override
	public boolean canChampions()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "Dimensional Rift";
	}

	protected int getManagerId()
	{
		return 31865;
	}

	protected int getPlayersInside(boolean alive)
	{
		if(_playerCount == 0)
			return 0;

		int sum = 0;
		_objects_lock.lock();
		for(L2Object o : _objects)
			if(o != null && o.isPlayer() && (!alive || !((L2Player) o).isAlikeDead()))
				sum++;
		_objects_lock.unlock();

		return sum;
	}

	@Override
	public void removeObject(L2Object o)
	{
		if(o.isPlayer())
			if(_playerCount <= 1)
				createNewKillRiftTimer();
		super.removeObject(o);
	}
}