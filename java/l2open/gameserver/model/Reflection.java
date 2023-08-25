package l2open.gameserver.model;

import l2open.common.ThreadPoolManager;
import l2open.database.mysql;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.InstancedZoneManager.SpawnInfo;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.gameserver.serverpackets.ExSendUIEvent;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import gnu.trove.TIntHashSet;
import javolution.util.FastMap;

public class Reflection
{
	protected int _id = 0;
	private InstancedZone _instance = null;
	private int _instancedZoneId = 0;
	private Location _coreLoc; // место, к которому кидает при использовании SoE/unstuck, иначе выбрасывает в основной мир
	private Location _returnLoc; // если не прописано core, но прописан return, то телепортит туда, одновременно перемещая в основной мир
	private Location _teleportLoc; // точка входа
	private List<L2Spawn> _spawns = new ArrayList<L2Spawn>();
	private List<L2DoorInstance> _doors = new ArrayList<L2DoorInstance>();
	protected List<L2Object> _objects = new ArrayList<L2Object>();
	protected final ReentrantLock _objects_lock = new ReentrantLock(), _CollapseTimer_lock = new ReentrantLock();
	protected int _playerCount = 0;
	private Timer _collapseTimer;
	private Timer _collapse1minTimer;
	private TimerTask _collapseTimerTask;
	private TimerTask _collapse1minTimerTask;
	private L2Party _party;
	private L2CommandChannel _commandChannel;
	private boolean _isCollapseStarted;
	private int _geoIndex;
	private String _name = "";
	private boolean _notCollapseWithoutPlayers = false;

	private boolean is_peace = false;

	protected TIntHashSet _visitors = new TIntHashSet();

	public int getGeoIndex()
	{
		return _geoIndex;
	}

	public void setGeoIndex(int id)
	{
		_geoIndex = id;
	}

	public int getInstancedZoneId()
	{
		return _instancedZoneId;
	}

	protected void setInstancedZone(InstancedZone iz)
	{
		_instance = iz;
	}

	public void setInstancedZoneId(int id)
	{
		_instancedZoneId = id;
		InstancedZone iz = InstancedZoneManager.getInstance().getById(id).get(0);
		if(iz.getMapX() >= 0)
		{
			int geoIndex = GeoEngine.NextGeoIndex(iz.getMapX(), iz.getMapY(), getId(), getInstancedZoneId());
			//System.out.println("getMapX: " + iz.getMapX() + " getMapY: " + iz.getMapY());
			setGeoIndex(geoIndex);
		}
	}

	/**
	 * Использовать только для статичных отражений с id <= 0.
	 */
	public Reflection(int id)
	{
		_id = id;
		ReflectionTable.getInstance().addReflection(this);
	}

	/**
	 * Создает отражение и регистрирует его в индексе. Вызывать только из конструктора отражения.
	 * @param name
	 */
	public Reflection(String name)
	{
		_id = IdFactory.getInstance().getNextId();
		_name = name;
		ReflectionTable.getInstance().addReflection(this);
	}

	public Reflection setName(String name)
	{
		_name = name;
		return this;
	}

	/**
	 * Создает отражение и регистрирует его в индексе. Сохраняет информацию о инстансе.
	 */
	public Reflection(InstancedZone iz)
	{
		_id = IdFactory.getInstance().getNextId();
		_instance = iz;
		_name = iz.getName();
		ReflectionTable.getInstance().addReflection(this);
		if(iz.getMapX() >= 0)
		{
			int geoIndex = GeoEngine.NextGeoIndex(iz.getMapX(), iz.getMapY(), getId(), getInstancedZoneId());
			//System.out.println("getMapX: " + iz.getMapX() + " getMapY: " + iz.getMapY());
			setGeoIndex(geoIndex);
		}
	}

	public void addSpawn(L2Spawn spawn)
	{
		if(spawn != null)
			_spawns.add(spawn);
	}

	public boolean addDoor(int id)
	{
		if(DoorTable.getInstance().getDoor(Integer.valueOf(id)) != null)
		{
			L2DoorInstance door = DoorTable.getInstance().getDoor(id).clone();
			addDoor(door);
			return true;
		}
		return false;
	}

	public void addDoor(L2DoorInstance door)
	{
		_doors.add(door);
	}

	public void setParty(L2Party party)
	{
		_party = party;
	}

	public L2Party getParty()
	{
		return _party;
	}

	public void setCommandChannel(L2CommandChannel commandChannel)
	{
		_commandChannel = commandChannel;
	}

	public L2CommandChannel getCommandChannel()
	{
		return _commandChannel;
	}

	public void setNotCollapseWithoutPlayers(boolean value)
	{
		_notCollapseWithoutPlayers = value;
	}

	/**
	 * Время в мс
	 * @param time
	 */
	public void startCollapseTimer(long time)
	{
		if(_id <= 0)
		{
			new Exception("Basic reflection " + _id + " could not be collapsed!").printStackTrace();
			return;
		}

		_CollapseTimer_lock.lock();
		if(_collapseTimerTask != null)
		{
			_collapseTimerTask.cancel();
			_collapseTimerTask = null;
		}

		if(_collapse1minTimerTask != null)
		{
			_collapse1minTimerTask.cancel();
			_collapse1minTimerTask = null;
		}

		if(_collapseTimer != null)
		{
			_collapseTimer.cancel();
			_collapseTimer = null;
		}

		if(_collapse1minTimer != null)
		{
			_collapse1minTimer.cancel();
			_collapse1minTimer = null;
		}

		_collapseTimer = new Timer();
		_collapseTimerTask = new TimerTask(){
			@Override
			public void run()
			{
				collapse();
			}
		};

		_collapse1minTimer = new Timer();
		_collapse1minTimerTask = new TimerTask(){
			@Override
			public void run()
			{
				minuteBeforeCollapse();
			}
		};

		if(time > 60 * 1000L)
			_collapse1minTimer.schedule(_collapse1minTimerTask, time - 60 * 1000L);
		_collapseTimer.schedule(_collapseTimerTask, time);
		_CollapseTimer_lock.unlock();
	}

	public void stopCollapseTimer()
	{
		_CollapseTimer_lock.lock();
		if(_collapseTimerTask != null)
			_collapseTimerTask.cancel();
		_collapseTimerTask = null;
		if(_collapse1minTimerTask != null)
			_collapse1minTimerTask.cancel();
		_collapse1minTimerTask = null;

		if(_collapseTimer != null)
			_collapseTimer.cancel();
		if(_collapse1minTimer != null)
			_collapse1minTimer.cancel();
		_collapseTimer = null;
		_collapse1minTimer = null;
		_CollapseTimer_lock.unlock();
	}

	public void minuteBeforeCollapse()
	{
		if(_isCollapseStarted)
			return;
		_objects_lock.lock();
		for(L2Object o : _objects)
			if(o != null && o.isPlayer())
				((L2Player) o).sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
		_objects_lock.unlock();
	}

	public void collapse()
	{
		if(_id <= 0)
		{
			new Exception("Basic reflection " + _id + " could not be collapsed!").printStackTrace();
			return;
		}

		_CollapseTimer_lock.lock();
		if(_isCollapseStarted)
		{
			_CollapseTimer_lock.unlock();
			return;
		}
		_isCollapseStarted = true;
		_CollapseTimer_lock.unlock();

		try
		{
			stopCollapseTimer();

			for(L2Spawn s : _spawns)
				if(s != null)
					s.despawnAll();

			for(L2DoorInstance d : _doors)
				d.deleteMe();

			List<L2Player> teleport_list = new ArrayList<L2Player>();
			List<L2Object> delete_list = new ArrayList<L2Object>();

			_objects_lock.lock();
			for(L2Object o : _objects)
				if(o != null)
					if(o.isPlayer())
						teleport_list.add((L2Player) o);
					else if(!o.isSummon() && !o.isPet())
						delete_list.add(o);
			_objects_lock.unlock();

			for(L2Player player : teleport_list)
			{
				if(player.getParty() != null)
				{
					if(equals(player.getParty().getReflection()))
						player.getParty().setReflection(null);
					if(player.getParty().getCommandChannel() != null && equals(player.getParty().getCommandChannel().getReflection()))
						player.getParty().getCommandChannel().setReflection(null);
				}
				if(equals(player.getReflection()))
				{
					player.setIsInvul(false); // костыль...
					if(getReturnLoc() != null)
						player.teleToLocation(getReturnLoc(), 0);
					else
					{
						try
						{
							String back = player.getVar("backCoords");
							if(back != null)
								player.teleToLocation(new Location(back), 0);
							else
								player.setReflection(0);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					player.unsetVar("backCoords");
					player.unsetVar("reflection");
				}
				player.sendPacket(new ExSendUIEvent(player, true, true, 0, 10, _name)); // остановка счётчика (для инстанта закена).
				ReflectionTable.getInstance().playerRemoveReflection(Integer.valueOf(player.getObjectId()));
			}

			if(_commandChannel != null)
				_commandChannel.setReflection(null);

			if(getParty() != null)
				getParty().setReflection(null);

			setParty(null);

			for(L2Object o : delete_list)
				o.deleteMe();

			_doors.clear();
			_objects.clear();
			_commandChannel = null;
			teleport_list.clear();
			delete_list.clear();
			_visitors.clear();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ReflectionTable.getInstance().remove(this);
			
			InstancedZone iz = null;
			FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(_instancedZoneId);
			if(izs != null && izs.size() > 0)
				iz = izs.get(0);

			GeoEngine.FreeGeoIndex(iz != null ? iz.getMapX() : 0, iz != null ? iz.getMapY() : 0, getId(), getInstancedZoneId(), getGeoIndex());
			IdFactory.getInstance().releaseId(getId());
		}
	}

	public int getId()
	{
		return _id;
	}

	public void addObject(L2Object o)
	{
		if(_isCollapseStarted)
			return;
		_objects_lock.lock();
		_objects.add(o);
		if(o.isPlayer() && _id > 0)
		{
			_playerCount++;
			if(_id > 0)
			{
				ReflectionTable.getInstance().playerAddReflection(Integer.valueOf(o.getObjectId()), this);
				_visitors.add(o.getObjectId());
			}
			//showTimer(o.getPlayer(), false);
		}
		_objects_lock.unlock();
	}

	public void removeObject(L2Object o)
	{
		if(_isCollapseStarted)
			return;
		
		_objects_lock.lock();
		_objects.remove(o);
		_objects_lock.unlock();
		if(o.isPlayer())
			_playerCount--;
		if(_playerCount <= 0 && _id > 0 && !_notCollapseWithoutPlayers)
			collapse();
		
	}

	public void setCoreLoc(Location l)
	{
		_coreLoc = l;
	}

	public Location getCoreLoc()
	{
		return _coreLoc;
	}

	public void setReturnLoc(Location l)
	{
		_returnLoc = l;
	}

	public Location getReturnLoc()
	{
		return _returnLoc;
	}

	public void setTeleportLoc(Location l)
	{
		_teleportLoc = l;
	}

	public Location getTeleportLoc()
	{
		return _teleportLoc;
	}

	public List<L2Spawn> getSpawns()
	{
		return _spawns;
	}

	public List<L2Player> getPlayers()
	{
		List<L2Player> result = new ArrayList<L2Player>();
		_objects_lock.lock();
		if(_objects != null)
			for(L2Object o : _objects)
				if(o != null && o.isPlayer())
					result.add((L2Player) o);
		_objects_lock.unlock();
		return result;
	}

	public List<L2NpcInstance> getNpcs()
	{
		List<L2NpcInstance> result = new ArrayList<L2NpcInstance>();
		_objects_lock.lock();
		if(_objects != null)
			for(L2Object o : _objects)
				if (o != null && o.isNpc())
					result.add((L2NpcInstance)o);
		_objects_lock.unlock();
		return result;
	}

	public List<L2DoorInstance> getDoors()
	{
		return _doors;
	}

	@Override
	protected void finalize()
	{
		collapse();
	}

	public boolean canChampions()
	{
		return _id <= 0;
	}

	public boolean isAutolootForced()
	{
		return false;
	}

	public boolean isCollapseStarted()
	{
		return _isCollapseStarted;
	}

	public int getPlayerCount()
	{
		return _playerCount;
	}

	public String getName()
	{
		return _name;
	}

	public InstancedZone getInstancedZone()
	{
		return _instance;
	}

	public void FillSpawns(GArray<SpawnInfo> si)
	{
		if(si == null)
			return;
		for(SpawnInfo s : si)
		{
			L2Spawn c;
			if(s == null)
				continue;
			L2Territory tr = TerritoryTable.getInstance().getLocation(s.getLocationId());
			if(tr == null)
				continue;
			GArray<int[]> points = tr.getCoords();
			switch(s.getType())
			{
				case 0: // точечный спаун, в каждой указанной точке
					for(int[] point : points)
					{
						c = s.getSpawn().clone();
						addSpawn(c);
						c.setReflection(getId());
						c.setRespawnDelay(s.getSpawn().getRespawnDelay(), s.getSpawn().getRespawnDelayRandom());
						c.setLocation(0);
						c.setLoc(new Location(point));
						if(!NpcTable.getTemplate(c.getNpcId()).isInstanceOf(L2ReflectionBossInstance.class))
							c.startRespawn();
						c.doSpawn(true);
						if(s.getSpawn().getNativeRespawnDelay() == 0)
							c.stopRespawn();
					}
					break;
				case 1: // один точечный спаун в рандомной точке
					c = s.getSpawn().clone();
					addSpawn(c);
					c.setReflection(getId());
					c.setRespawnDelay(s.getSpawn().getRespawnDelay(), s.getSpawn().getRespawnDelayRandom());
					c.setLocation(0);
					c.setLoc(new Location(points.get(Rnd.get(points.size()))));
					if(!NpcTable.getTemplate(c.getNpcId()).isInstanceOf(L2ReflectionBossInstance.class))
						c.startRespawn();
					c.doSpawn(true);
					if(s.getSpawn().getNativeRespawnDelay() == 0)
						c.stopRespawn();
					break;
				case 2: // локационный спаун
					c = s.getSpawn().clone();
					addSpawn(c);
					c.setReflection(getId());
					c.setRespawnDelay(s.getSpawn().getRespawnDelay(), s.getSpawn().getRespawnDelayRandom());
					if(!NpcTable.getTemplate(c.getNpcId()).isInstanceOf(L2ReflectionBossInstance.class))
						c.startRespawn();
					for(int j = 0; j < c.getAmount(); j++)
						c.doSpawn(true);
					if(s.getSpawn().getNativeRespawnDelay() == 0)
						c.stopRespawn();
					break;
				case 3: // спаун в рандомной локе
					c = s.getSpawn().clone();
					addSpawn(c);
					c.setReflection(getId());
					c.setRespawnDelay(s.getSpawn().getRespawnDelay(), s.getSpawn().getRespawnDelayRandom());
					c.setLocation(s.getLocationId());
					if(!NpcTable.getTemplate(c.getNpcId()).isInstanceOf(L2ReflectionBossInstance.class))
						c.startRespawn();
					c.doSpawn(true);
					if(s.getSpawn().getNativeRespawnDelay() == 0)
						c.stopRespawn();
					break;
			}
		}
	}

	public void FillDoors(GArray<L2DoorInstance> doors)
	{
		if(doors == null)
			return;
		for(L2DoorInstance d : doors)
		{
			L2DoorInstance door = d.clone();
			door.setReflection(this);
			addDoor(door);
			door.spawnMe();
			if(d.isOpen())
				door.openMe();
			door.setIsInvul(d.isInvul());
		}
	}

	/**
	 * Открывает дверь в отражении
	 */
	public void openDoor(int doorId)
	{
		for(L2DoorInstance door : getDoors())
			if(door.getDoorId() == doorId)
				door.openMe();
	}

	/**
	 * Закрывает дверь в отражении
	 */
	public void closeDoor(int doorId)
	{
		for(L2DoorInstance door : getDoors())
			if(door.getDoorId() == doorId)
				door.closeMe();
	}

	/**
	 * Удаляет все спауны из рефлекшена и запускает коллапс-таймер. Время указывается в минутах.
	 */
	public void clearReflection(int collapseTime, boolean message)
	{
		if(getId() <= 0)
			return;

		ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl(){
			@Override
			public void runImpl()
			{
				for(L2Spawn s : getSpawns())
				{
					s.despawnAll();
					s.stopRespawn();
				}
			}
		}, 1000);

		startCollapseTimer(collapseTime * 60 * 1000L);

		if(message)
			for(L2Player pl : getPlayers())
				if(pl != null)
					pl.sendPacket(new SystemMessage(SystemMessage.THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES).addNumber(collapseTime));
	}
	
	public List<L2MonsterInstance> getMonsters()
	{
		List<L2MonsterInstance> result = new ArrayList<L2MonsterInstance>();
		_objects_lock.lock();
		if(_objects != null)
			for(L2Object o : _objects)
				if(o != null && o.isMonster())
					result.add((L2MonsterInstance)o);
		_objects_lock.unlock();
		return result;
	}

	public L2NpcInstance findFirstNPC(int paramInt)
	{
		_objects_lock.lock();
		if(_objects != null)
			for(L2Object obj : _objects)
				if(obj != null && obj.isNpc() && ((L2NpcInstance)obj).getNpcId() == paramInt)
				{
					_objects_lock.unlock();
					return (L2NpcInstance)obj;
				}
		_objects_lock.unlock();
		return null;
	}

	public GArray<Integer> getPlayersEntered()
	{
		return ReflectionTable.getInstance().getPlayersEntered(this);
	}

	public L2NpcInstance addSpawnWithoutRespawn(int npcId, Location loc, int randomOffset)
	{
		Location newLoc;
		if(randomOffset > 0)
			newLoc = Location.findPointToStay(loc, 0, randomOffset, getGeoIndex()).setH(loc.h);
		else
			newLoc = loc;

		return NpcUtils.spawnSingle(npcId, newLoc, this.getId(), 0);
	}

	public void CreateOnePrivateEx(int npc_id, Location loc, String ai_type, String instance)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npc_id);
		if(template == null)
			return;
		template.ai_type = ai_type;
		template.setInstance(instance);

		try
		{
			L2Spawn sp = new L2Spawn(template);
			sp.setLocx(loc.x);
			sp.setLocy(loc.y);
			sp.setLocz(loc.z);
			sp.setRespawnDelay(0);
			sp.setReflection(this.getId());
			sp.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void clearVisitors()
	{
		_visitors.clear();
	}

	public int[] getVisitors()
	{
		return _visitors.toArray();
	}

	public void setReenterTime()
	{
		int[] players = null;
	//	lock.lock();
		try
		{
			players = _visitors.toArray();
		}
		finally
		{
			//lock.unlock();
		}

		if(players != null)
		{
			for(int objectId : players)
			{
				try
				{
					L2Player player = L2ObjectsStorage.getPlayer(objectId);
					if(player != null)
						player.setVarInst(_name, String.valueOf(System.currentTimeMillis()));
					else
						mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,-1)", objectId, _name, String.valueOf(System.currentTimeMillis()));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void dissolveParty(L2Party party)
	{}

	public void oustPartyMember(L2Party party, L2Player player)
	{}

	public void setPeace(boolean value)
	{
		is_peace = value;

		_objects_lock.lock();
		if(_objects != null)
			for(L2Object o : _objects)
				if(o != null && o.isPlayer())
					o.broadcastRelationChanged();
		_objects_lock.unlock();
	}

	public boolean isPeace()
	{
		return is_peace;
	}
}