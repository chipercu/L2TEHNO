package bosses;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class BelethManager extends Functions implements ScriptFile
{
	public int getRevision()
	{
		return 2400;
	}

	private static final Logger _log = Logger.getLogger(BelethManager.class.getName());
	private static L2Zone _zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.epic, 702144, false);
	private static ZoneListener _zoneListener = new ZoneListener();
	private static List<L2Player> _indexedPlayers = new ArrayList<L2Player>();
	private static List<L2NpcInstance> _npcList = new ArrayList<L2NpcInstance>();
	private static final int _doorWaitTimeDuration = 60000; // 1min
	private static final int _spawnWaitTimeDuration = 120000; // 2min
	private static final int _closeDoorTimeDuration = 180000; // 3min
	private static final int _clonesRespawnTimeTimeDuration = 40000; // 40sec
	private static final int _ringAvailableTime = 300000; // 5min
	private static final int _clearEntityTime = 600000; // 10min
	private static final long _entityInactivityTime = 2 * 60 * 60 * 1000; // 2 hours
	private static final int _ringSpawnTime = 300000; // 5min
	private static final int _lastSpawnTime = 600000; // 10min

	private static final int DOOR = 20240001; // Throne door
	private static final int CORRDOOR = 20240002; // Corridor door
	private static final int COFFDOOR = 20240003; // Tomb door

	private static boolean _taskStarted = false;
	private static boolean _entryLocked = false;
	private static int _ringAvailable = 0;
	private static boolean _belethAlive = false;

	private static final int VORTEX = 29125; // Vortex.
	private static final int ELF = 29128; // Elf corpse.
	private static final int COFFIN = 32470; // Beleth's coffin.
	private static final int BELETH = 29118; // Beleth.
	private static final int CLONE = 29119; // Beleth's clone.

	private static final int locZ = -9353; // Z value for all of npcs

	private static final int[] VORTEXSPAWN = {
			16325,
			214983,
			-9353
	};
	private static final int[] COFFSPAWN = {
			12471,
			215602,
			-9360,
			49152
	};
	private static final int[] BELSPAWN = {
			16325,
			214614,
			-9353,
			49152
	};

	private static L2RaidBossInstance _beleth = null;
	private static final int centerX = 16325; // Center of the room
	private static final int centerY = 213135;

	private static Map<L2MonsterInstance, Location> _clones = new ConcurrentHashMap<L2MonsterInstance, Location>();
	private static Location[] _cloneLoc = new Location[56];
	private static ScheduledFuture<?> cloneRespawnTask;
	private static ScheduledFuture<?> ringSpawnTask;
	private static ScheduledFuture<?> lastSpawnTask;

	private static L2NpcInstance spawn(int npcId, int x, int y, int z, int h)
	{
		Location loc = new Location(x, y, z);
		L2NpcTemplate template = NpcTable.getTemplate(npcId);
		L2NpcInstance npc = template.getNewInstance();
		npc.setSpawnedLoc(loc);
		npc.setLoc(loc);
		npc.setHeading(h);
		npc.spawnMe();
		return npc;
	}

	public static L2Zone getZone()
	{
		return _zone;
	}

	public static boolean checkPlayer(L2Player player)
	{
		if(player.isDead() || player.getLevel() < 80 || !player.canEnterBeleth())
			return false;
		return true;
	}

	private static boolean checkBossSpawnCond()
	{
		if(_indexedPlayers.size() < ConfigValue.BelethManagerCount || _taskStarted || ServerVariables.getLong("BelethKillTime", 0) > System.currentTimeMillis()) // 36 players
			return false;
		return true;
	}

	private static int _enterChar=0;
	public void enterToBeleth()
	{
		L2Player player = getSelfPlayer();
		L2NpcInstance npc = getNpc();

		if(!checkConditions(player))
			return;
		L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.poison, 875551);
		//myself.InstantTeleportMPCC( talker, 16342, 209557, -9352, 3000, 0, 300, 57, 0 );
		for(L2Player member : player.getParty().getCommandChannel().getMembers())
			//if(npc.isInRange(member, 3000))
			if(zone.checkIfInZone(member))
			{
				member.sendActionFailed();
				member.teleToLocation(16342+Rnd.get(0, 300), 209557+Rnd.get(0, 300), -9352);
				//if(member.getPet() != null)
				//	member.getPet().teleToLocation(16342, 209557, -9352);
			}
		if(_enterChar == 0)
			_enterChar = player.getObjectId();
	}

	/**
	* @param player
	* @return if player is qualified to start the raid
	*/
	private boolean checkConditions(L2Player player)
	{
		if(ServerVariables.getLong("BelethKillTime", 0) > System.currentTimeMillis())
		{
			player.sendMessage("Beleth already dead");
			return false;
		}
		L2Party party = player.getParty();
		if (party == null)
		{
			player.sendMessage("You are not in a party.");
			return false;
		}
		L2CommandChannel channel = party.getCommandChannel();
		if (channel == null)
		{
			player.sendMessage("Your party is not in a command channel.");
			return false;
		}
		else if(channel.getChannelLeader() != player)
		{
			player.sendMessage("Only the leader of the command channel may approach me.");
			return false;
		}
		else if(channel.getMemberCount() < ConfigValue.BelethManagerCount)
		{
			player.sendMessage("Is not enough members of the command channel");
			return false;
		}
		return true;
	}

	// Убираем от Белефа чаров, которые пытаются "сохранится" в его логове.
	public static void OnPlayerExit(L2Player player)
	{
		if(_zone.checkIfInZone(player))
			player.teleToLocation(-11802, 236360, -3264);
	}

	// Для надежности :)
	public static void OnPlayerEnter(L2Player player)
	{
		if(_zone.checkIfInZone(player))
			player.teleToLocation(-11802, 236360, -3264);
	}

	public static class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
  		public void objectEntered(L2Zone zone, L2Object object)
		{
			if(!object.isPlayer() || _entryLocked)
				return;

			L2Player player = object.getPlayer();

			if(!_indexedPlayers.contains(player))
				if(checkPlayer(player))
				{
					_indexedPlayers.add(player);
					Log.add("ENTER_TRUE["+_indexedPlayers.size()+"]: "+player.getName(), "beleth_enter_info");
				}
				else
					Log.add("ENTER_FALSE["+_indexedPlayers.size()+"]: "+player.getName()+" dead="+player.isDead()+" level="+ player.getLevel()+" can_enter="+player.canEnterBeleth(), "beleth_enter_info");

			if(checkBossSpawnCond())
			{
				ThreadPoolManager.getInstance().schedule(new BelethSpawnTask(), 10000L);
				_taskStarted = true;
			}
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			if(!object.isPlayer())
				return;

			L2Player player = object.getPlayer();

			if(_indexedPlayers.contains(player))
			{
				_indexedPlayers.remove(player);
				Log.add("LEAVED_TRUE["+_indexedPlayers.size()+"]: "+player.getName(), "beleth_enter_info");
			}
		}
	}

	private static class CloneRespawnTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(_clones == null || _clones.isEmpty())
				return;

			L2MonsterInstance nextclone;
			for(L2MonsterInstance clone : _clones.keySet())
				if(clone.isDead())
				{
					nextclone = (L2MonsterInstance) spawn(CLONE, _clones.get(clone).x, _clones.get(clone).y, locZ, 49152); // _cloneLoc[i].h
					nextclone.setCurrentHpMp(nextclone.getMaxHp(), nextclone.getMaxMp());
					_clones.put(nextclone, nextclone.getLoc());
					_clones.remove(clone);
				}
		}
	}

	private static class BelethSpawnTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			_indexedPlayers.clear();
			ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.start), 10000L);
			ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.inactivity_check), _entityInactivityTime);
			initSpawnLocs();
		}
	}

	private enum Event
	{
		none,
		start,
		open_door,
		close_door,
		beleth_spawn,
		beleth_despawn,
		clone_despawn,
		clone_spawn,
		ring_unset,
		beleth_dead,
		entity_clear,
		inactivity_check,
		spawn_ring,
		spawn_extras
	}

	public static class eventExecutor extends l2open.common.RunnableImpl
	{
		Event _event;
		int _objId;

		eventExecutor(Event event)
		{
			_event = event;
		}

		eventExecutor(Event event, int objId)
		{
			_event = event;
			_objId = objId;
		}

		@Override
		public void runImpl()
		{
			switch(_event)
			{
				case start:
					ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.open_door), _doorWaitTimeDuration);
					break;
				case open_door:
					DoorTable.getInstance().getDoor(DOOR).openMe();
					ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.close_door), _closeDoorTimeDuration);
					break;
				case close_door:
					DoorTable.getInstance().getDoor(DOOR).closeMe();
					_entryLocked = true;
					ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.beleth_spawn), _spawnWaitTimeDuration);
					break;
				case beleth_spawn:
					L2NpcInstance temp = spawn(VORTEX, VORTEXSPAWN[0], VORTEXSPAWN[1], VORTEXSPAWN[2], 16384);
					_npcList.add(temp);
					_beleth = (L2RaidBossInstance) spawn(BELETH, BELSPAWN[0], BELSPAWN[1], BELSPAWN[2], BELSPAWN[3]);
					_beleth.p_block_move(true, null);
					_beleth.setCurrentHpMp(_beleth.getMaxHp(), _beleth.getMaxMp());
					_belethAlive = true;
					ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.clone_spawn), 10); // initial clones
					ringSpawnTask = ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.spawn_ring), _ringSpawnTime); // inner ring
					lastSpawnTask = ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.spawn_extras), _lastSpawnTime); // last clones
					break;
				case clone_spawn:
					L2MonsterInstance clone;
					for(int i = 0; i < 32; i++)
					{
						clone = (L2MonsterInstance) spawn(CLONE, _cloneLoc[i].x, _cloneLoc[i].y, locZ, 49152); // _cloneLoc[i].h
						clone.setCurrentHpMp(clone.getMaxHp(), clone.getMaxMp());
						_clones.put(clone, clone.getLoc());
					}
					cloneRespawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CloneRespawnTask(), _clonesRespawnTimeTimeDuration, _clonesRespawnTimeTimeDuration);
					break;
				case spawn_ring:
					for(int i = 32; i < 48; i++)
						spawnClone(i);
					break;
				case spawn_extras:
					for(int i = 48; i < 56; i++)
						spawnClone(i);
					break;
				case beleth_dead:
					if(cloneRespawnTask != null)
					{
						cloneRespawnTask.cancel(false);
						cloneRespawnTask = null;
					}
					if(ringSpawnTask != null)
					{
						ringSpawnTask.cancel(false);
						ringSpawnTask = null;
					}
					if(lastSpawnTask != null)
					{
						lastSpawnTask.cancel(false);
						lastSpawnTask = null;
					}
					temp = spawn(ELF, _beleth.getLoc().x, _beleth.getLoc().y, locZ, BELSPAWN[3]);
					_npcList.add(temp);
					temp = spawn(COFFIN, COFFSPAWN[0], COFFSPAWN[1], COFFSPAWN[2], COFFSPAWN[3]);
					_npcList.add(temp);
					DoorTable.getInstance().getDoor(CORRDOOR).openMe();
					DoorTable.getInstance().getDoor(COFFDOOR).openMe();
					setRingAvailable(_enterChar);
					_belethAlive = false;
					long time = (long) (ConfigValue.FixintervalOfBeleth + Rnd.get(0, ConfigValue.RandomIntervalOfBeleth));
					ServerVariables.set("BelethKillTime", System.currentTimeMillis() + time);
					for(L2Player i : _zone.getInsidePlayers())
						i.sendMessage("Beleth's Lair will push you out in 10 minutes");
					ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.clone_despawn), 10);
					ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.ring_unset), _ringAvailableTime);
					ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.entity_clear), _clearEntityTime);
					break;
				case ring_unset:
					setRingAvailable(0);
					break;
				case entity_clear:
					for(L2NpcInstance n : _npcList)
						if(n != null)
							n.deleteMe();
					_npcList.clear();

					// Close coffin and corridor doors
					DoorTable.getInstance().getDoor(CORRDOOR).closeMe();
					DoorTable.getInstance().getDoor(COFFDOOR).closeMe();

					//oust players
					for(L2Player i : _zone.getInsidePlayers())
					{
						i.teleToLocation(-11802, 236360, -3271);
						i.sendMessage("Beleth's Lair has become unstable so you've been teleported out");
					}
					_enterChar=0;
					_entryLocked = false;
					_taskStarted = false;
					break;
				case clone_despawn:
					for(L2MonsterInstance clonetodelete : _clones.keySet())
						clonetodelete.deleteMe();
					_clones.clear();
					break;
				case inactivity_check:
					if(!_beleth.isDead())
					{
						_beleth.deleteMe();
						ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.entity_clear), 10);
					}
					break;
			}
		}
	}

	public static int isRingAvailable()
	{
		return _ringAvailable;
	}

	public static void setRingAvailable(int value)
	{
		_ringAvailable = value;
	}

	public static void setBelethDead()
	{
		if(_entryLocked && _belethAlive)
			ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.beleth_dead), 10);
	}

	private static void spawnClone(int id)
	{
		L2MonsterInstance clone;
		clone = (L2MonsterInstance) spawn(CLONE, _cloneLoc[id].x, _cloneLoc[id].y, locZ, 49152); // _cloneLoc[i].h
		clone.setCurrentHpMp(clone.getMaxHp(), clone.getMaxMp());
		_clones.put(clone, clone.getLoc());
	}

	private static void initSpawnLocs()
	{
		// Variables for Calculations
		double angle = Math.toRadians(22.5);
		int radius = 700;

		// Inner clone circle
		for(int i = 0; i < 16; i++)
		{
			if(i % 2 == 0)
				radius -= 50;
			else
				radius += 50;
			_cloneLoc[i] = new Location(centerX + (int) (radius * Math.sin(i * angle)), centerY + (int) (radius * Math.cos(i * angle)), convertDegreeToClientHeading(270 - i * 22.5));
		}
		// Outer clone square
		radius = 1340;
		angle = Math.asin(1 / Math.sqrt(3));
		int mulX = 1, mulY = 1, addH = 3;
		double decX = 1.0, decY = 1.0;
		for(int i = 0; i < 16; i++)
		{
			if(i % 8 == 0)
				mulX = 0;
			else if(i < 8)
				mulX = -1;
			else
				mulX = 1;
			if(i == 4 || i == 12)
				mulY = 0;
			else if(i > 4 && i < 12)
				mulY = -1;
			else
				mulY = 1;
			if(i % 8 == 1 || i == 7 || i == 15)
				decX = 0.5;
			else
				decX = 1.0;
			if(i % 10 == 3 || i == 5 || i == 11)
				decY = 0.5;
			else
				decY = 1.0;
			if((i + 2) % 4 == 0)
				addH++;
			_cloneLoc[i + 16] = new Location(centerX + (int) (radius * decX * mulX), centerY + (int) (radius * decY * mulY), convertDegreeToClientHeading(180 + addH * 90));
		}
		// Octagon #2 - Another ring of clones like the inner square, that
		// spawns after some time
		angle = Math.toRadians(22.5);
		radius = 1000;
		for(int i = 0; i < 16; i++)
		{
			if(i % 2 == 0)
				radius -= 70;
			else
				radius += 70;
			_cloneLoc[i + 32] = new Location(centerX + (int) (radius * Math.sin(i * angle)), centerY + (int) (radius * Math.cos(i * angle)), _cloneLoc[i].h);
		}
		// Extra clones - Another 8 clones that spawn when beleth is close to
		// dying
		int order = 48;
		radius = 650;
		for(int i = 1; i < 16; i += 2)
		{
			if(i == 1 || i == 15)
				_cloneLoc[order] = new Location(_cloneLoc[i].x, _cloneLoc[i].y + radius, _cloneLoc[i + 16].h);
			else if(i == 3 || i == 5)
				_cloneLoc[order] = new Location(_cloneLoc[i].x + radius, _cloneLoc[i].y, _cloneLoc[i].h);
			else if(i == 7 || i == 9)
				_cloneLoc[order] = new Location(_cloneLoc[i].x, _cloneLoc[i].y - radius, _cloneLoc[i + 16].h);
			else if(i == 11 || i == 13)
				_cloneLoc[order] = new Location(_cloneLoc[i].x - radius, _cloneLoc[i].y, _cloneLoc[i].h);
			order++;
		}
	}

	public static int convertDegreeToClientHeading(double degree)
	{
		if(degree < 0)
			degree = 360 + degree;
		return (int)(degree * 182.04444444399999);
	}
	@Override
	public void onLoad()
	{
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		_log.info("Beleth Manager: Loaded successfuly");
	}

	@Override
	public void onReload()
	{
		_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
	}

	@Override
	public void onShutdown()
	{
	}
}