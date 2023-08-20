package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.gameserver.taskmanager.SpawnTaskManager;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;
import l2open.util.SchedulableEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L2Spawn implements Cloneable
{
	private static Logger _log = Logger.getLogger(L2Spawn.class.getName());

	/** The link on the L2NpcTemplate object containing generic and static properties of this spawn (ex : RewardExp, RewardSP, AggroRange...) */
	private int _npcId;

	private String _AIParam="-1";

	/** Position of the spawn point */
	private int _locx, _locy, _locz, _heading, _location;
	private L2Territory[] _territory = null;
	private L2Territory[] _banedTerritory = null;

	/** The maximum number of L2NpcInstance that can manage this L2Spawn */
	private int _maximumCount;

	/** То количество что установлено в базе (текущий максимум может изменяться) */
	private int _referenceCount;

	/** The current number of L2NpcInstance managed by this L2Spawn */
	private int _currentCount;

	/** The current number of SpawnTask in progress or stand by of this L2Spawn */
	private int _scheduledCount;

	/** The delay between a L2NpcInstance remove and its re-spawn */
	private int _respawnDelay, _respawnDelayRandom, _nativeRespawnDelay;

	/** Время респауна, в unixtime */
	private int _respawnTime;

	/** If True a L2NpcInstance is respawned each time that another is killed */
	boolean _doRespawn=false;
	
	private L2NpcTemplate _npcTemplate;

	public boolean isDoRespawn()
	{
		return _doRespawn;
	}

	private L2NpcInstance _lastSpawn;

	private static final GArray<SpawnListener> _spawnListeners = new GArray<SpawnListener>();

	public HashMap<String, GArray<SchedulableEvent>> _events;

	private GArray<L2NpcInstance> _spawned;

	private int _siegeId;

	private int _reflection;

	public int getReflection()
	{
		return _reflection;
	}

	public void setReflection(int reflection)
	{
		_reflection = reflection;
	}

	public void decreaseScheduledCount()
	{
		if(_scheduledCount > 0)
			_scheduledCount--;
	}

	/**
	 * Устаревший конструктор, используйте в качестве параметра npcId. 
	 */
	public L2Spawn(L2NpcTemplate mobTemplate) throws ClassNotFoundException
	{
		_npcId = mobTemplate.getNpcId();
		if(mobTemplate == null || mobTemplate.getInstanceConstructor() == null)
			throw new ClassNotFoundException("Unable to instantiate npc " + _npcId);
		_spawned = new GArray<L2NpcInstance>(1);
		_npcTemplate = mobTemplate;
	}

	public L2Spawn(int npcId) throws ClassNotFoundException
	{
		_npcId = npcId;
		L2NpcTemplate mobTemplate = NpcTable.getTemplate(npcId);
		if(mobTemplate == null || mobTemplate.getInstanceConstructor() == null)
			throw new ClassNotFoundException("Unable to instantiate npc " + npcId);
		_spawned = new GArray<L2NpcInstance>(1);
		if(_npcTemplate == null)
			_npcTemplate = mobTemplate;
	}

	/**
	 * Return the maximum number of L2NpcInstance that this L2Spawn can manage.<BR><BR>
	 */
	public int getAmount()
	{
		return _maximumCount;
	}

	/**
	 * Return the number of L2NpcInstance that this L2Spawn spawned.<BR><BR>
	 */
	public int getSpawnedCount()
	{
		return _currentCount;
	}

	/**
	 * Return the number of L2NpcInstance that this L2Spawn sheduled.<BR><BR>
	 */
	public int getSheduledCount()
	{
		return _scheduledCount;
	}

	/**
	 * Return the Identifier of the location area where L2NpcInstance can be spwaned.<BR><BR>
	 */
	public int getLocation()
	{
		return _location;
	}

	public L2Territory[] getLocation2()
	{
		return _territory;
	}

	/**
	 * Return the position of the spawn point.<BR><BR>
	 */
	public Location getLoc()
	{
		return new Location(_locx, _locy, _locz);
	}

	/**
	 * Return the X position of the spawn point.<BR><BR>
	 */
	public int getLocx()
	{
		return _locx;
	}

	/**
	 * Return the Y position of the spawn point.<BR><BR>
	 */
	public int getLocy()
	{
		return _locy;
	}

	/**
	 * Return the Z position of the spawn point.<BR><BR>
	 */
	public int getLocz()
	{
		return _locz;
	}

	/**
	 * Return the Identifier of the L2NpcInstance manage by this L2Spwan contained in the L2NpcTemplate.<BR><BR>
	 */
	public int getNpcId()
	{
		return _npcId;
	}

	/**
	 * Return the heading of L2NpcInstance when they are spawned.<BR><BR>
	 */
	public int getHeading()
	{
		return _heading;
	}

	/**
	 * Return the delay between a L2NpcInstance remove and its re-spawn.<BR><BR>
	 */
	public int getRespawnDelay()
	{
		return _respawnDelay;
	}

	public int getNativeRespawnDelay()
	{
		return _nativeRespawnDelay;
	}

	public int getRespawnDelayRandom()
	{
		return _respawnDelayRandom;
	}

	public int getRespawnDelayWithRnd()
	{
		return _respawnDelayRandom == 0 ? _respawnDelay : Rnd.get(_respawnDelay - _respawnDelayRandom, _respawnDelay + _respawnDelayRandom);
	}

	public int getRespawnTime()
	{
		return _respawnTime;
	}

	/**
	 * Set the maximum number of L2NpcInstance that this L2Spawn can manage.<BR><BR>
	 */
	public void setAmount(int amount)
	{
		if(_referenceCount == 0)
			_referenceCount = amount;
		_maximumCount = amount;
	}

	/**
	 * Восстанавливает измененное количество
	 */
	public void restoreAmount()
	{
		_maximumCount = _referenceCount;
	}

	/**
	 * Set the Identifier of the location area where L2NpcInstance can be spawned.<BR><BR>
	 */
	public void setLocation(int location)
	{
		_location = location;
	}

	public void setLocation2(String location)
	{
		String loc[] = location.split(";");
		if(loc.length == 1)
			_location = Integer.parseInt(location);
		else
		{
			_location = 0;
			_territory = new L2Territory[loc.length];
			for(int i = 0;i<loc.length;i++)
				_territory[i] = TerritoryTable.getInstance().getLocation(Integer.parseInt(loc[i]));
		}
	}

	public void setLocation3(L2Territory[] location)
	{
		_territory = location;
	}
	

	/**
	 * Set the position(x, y, z, heading) of the spawn point.
	 * @param loc Location
	 */
	public void setLoc(Location loc)
	{
		_locx = loc.x;
		_locy = loc.y;
		_locz = loc.z;
		_heading = loc.h;
	}

	public void setLoc(int[] loc, int h)
	{
		_locx = loc[0];
		_locy = loc[1];
		_locz = loc[2];
		_heading = h;
	}

	/**
	 * Set the X position of the spawn point.<BR><BR>
	 */
	public void setLocx(int locx)
	{
		_locx = locx;
	}

	/**
	 * Set the Y position of the spawn point.<BR><BR>
	 */
	public void setLocy(int locy)
	{
		_locy = locy;
	}

	/**
	 * Set the Z position of the spawn point.<BR><BR>
	 */
	public void setLocz(int locz)
	{
		_locz = locz;
	}

	/**
	 * Set the heading of L2NpcInstance when they are spawned.<BR><BR>
	 */
	public void setHeading(int heading)
	{
		_heading = heading;
	}

	public void decreaseCount(L2NpcInstance oldNpc)
	{
		// Decrease the current number of L2NpcInstance of this L2Spawn
		_currentCount--;

		if(_currentCount < 0)
			_currentCount = 0;

		notifyNpcDeSpawned(oldNpc);

		// Check if respawn is possible to prevent multiple respawning caused by lag
		if(_doRespawn && _scheduledCount + _currentCount < _maximumCount)
		{
			// Update the current number of SpawnTask in progress or stand by of this L2Spawn
			_scheduledCount++;

			long delay = (long) (NpcTable.getTemplate(_npcId).isRaid ? ConfigValue.AltRaidRespawnMultiplier * getRespawnDelayWithRnd() : getRespawnDelayWithRnd()) * 1000L;
			delay = Math.max(1000, delay - oldNpc.getDeadTime());

			_respawnTime = (int) ((System.currentTimeMillis() + delay) / 1000);

			addSpawnTask(oldNpc, delay);
		}
		else
			oldNpc.deleteMe();
	}

	/**
	 * Create the initial spawning and set _doRespawn to True.<BR><BR>
	 *
	 * @return The number of L2NpcInstance that were spawned
	 */
	public int init()
	{
		while(_currentCount + _scheduledCount < _maximumCount)
			doSpawn(false);

		_doRespawn = true;

		return _currentCount;
	}

	/**
	 * Create a L2NpcInstance in this L2Spawn.<BR><BR>
	 */
	public L2NpcInstance spawnOne()
	{
		return doSpawn(false);
	}

	public void despawnAll()
	{
		stopRespawn();
		for(L2NpcInstance npc : getAllSpawned())
			if(npc != null)
				npc.deleteMe();
		_currentCount = 0;
	}

	/**
	 * Set _doRespawn to False to stop respawn in this L2Spawn.<BR><BR>
	 */
	public void stopRespawn()
	{
		_doRespawn = false;
	}

	/**
	 * Set _doRespawn to True to start or restart respawn in this L2Spawn.<BR><BR>
	 */
	public void startRespawn()
	{
		_doRespawn = true;
	}

	public L2NpcInstance doSpawn(boolean spawn)
	{
		return doSpawn(spawn, false, 0, 0, 0, null);
	}

	public L2NpcInstance doSpawn(boolean spawn, boolean isPts, int param1, int param2, int param3, L2NpcInstance leader)
	{
		try
		{
			L2NpcTemplate template = _npcTemplate;

			// Check if the L2Spawn is not a L2Pet or L2Minion spawn
			if(template.isInstanceOf(L2PetInstance.class) || (template.isInstanceOf(L2MinionInstance.class) && !isPts))
			{
				_currentCount++;
				//_log.info("L2NpcInstance 1: "+getNpcId());
				return null;
			}

			if(template.level > ConfigValue.MaxSpawnLevel && template.isInstanceOf(L2MonsterInstance.class))
			{
				_currentCount++;
				//_log.info("L2NpcInstance 1: "+getNpcId());
				return null;
			}

			// Get L2NpcInstance Init parameters and its generate an Identifier
			// Call the constructor of the L2NpcInstance
			// (can be a L2ArtefactInstance, L2FriendlyMobInstance, L2GuardInstance, L2MonsterInstance, L2SiegeGuardInstance, L2BoxInstance or L2NpcInstance)
			Object tmp;
			if(isPts)
				tmp = new L2MinionInstance(IdFactory.getInstance().getNextId(), template);
			else
				tmp = template.getNewInstance();

			// Check if the Instance is a L2NpcInstance
			//if(!(tmp instanceof L2NpcInstance) && !(tmp instanceof L2MinionInstance))
			//{
				//_log.info("L2NpcInstance 2: "+getNpcId());
				//return null;
			//}

			if(!spawn)
				spawn = _respawnTime <= System.currentTimeMillis() / 1000 + ConfigValue.MinRespawnTime;

			_spawned.add((L2NpcInstance) tmp);

			if(_events != null)
				for(String methodName : _events.keySet())
					for(SchedulableEvent se : _events.get(methodName))
						if(se != null)
							((L2NpcInstance) tmp).addMethodInvokeListener(methodName, se);

			((L2NpcInstance) tmp).param1=param1;
			((L2NpcInstance) tmp).param2=param2;
			((L2NpcInstance) tmp).param3=param3;
			((L2NpcInstance) tmp).setNpcLeader(leader);
			return intializeNpc((L2NpcInstance) tmp, spawn, false);
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "NPC " + _npcId + " class not found");
			e.printStackTrace();
		}
		//_log.info("L2NpcInstance 3: "+getNpcId());
		return null;
	}

	public GArray<L2NpcInstance> getAllSpawned()
	{
		return _spawned;
	}

	private L2NpcInstance intializeNpc(L2NpcInstance mob, boolean spawn, boolean respawn)
	{
		if(!getAIParam().equals("-1") && !getAIParam().isEmpty())
			for(String params : getAIParam().split(";"))
				setAIField(mob.getAI(), params.split("=")[0],params.split("=")[1]);

		Location newLoc;

		// If Locx=0 and Locy=0, the L2NpcInstance must be spawned in an area defined by location
		if(getLocation() > 0)
		{
			// Calculate the random position in the location area
			int p[] = TerritoryTable.getInstance().getLocation(getLocation()).getRandomPoint(getBanedTerritory());

			// Set the calculated position of the L2NpcInstance
			newLoc = new Location(p[0], p[1], p[2], Rnd.get(0xFFFF));
		}
		else if(getLocation2() != null)
		{
			int p[] = getRandomPoint();
			newLoc = new Location(p[0], p[1], p[2], Rnd.get(0xFFFF));
		}
		else
		{
			// The L2NpcInstance is spawned at the exact position (Lox, Locy, Locz)
			newLoc = getLoc();

			// random heading if not defined
			newLoc.h = getHeading() == -1 ? Rnd.get(0xFFFF) : getHeading();
		}
		mob.getEffectList().stopAllEffects(false);

		// Set the HP and MP of the L2NpcInstance to the max
		mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp(), true);

		// Link the L2NpcInstance to this L2Spawn
		mob.setSpawn(this);

		// Set the heading of the L2NpcInstance (random heading if not defined)
		mob.setHeading(newLoc.h);

		// save spawned points
		mob.setSpawnedLoc(newLoc);

		// Является ли моб "подземным" мобом?
		mob.setUnderground(GeoEngine.getHeight(newLoc, 0) < GeoEngine.getHeight(newLoc.clone().changeZ(5000), 0));

		if(spawn)
		{
			// Launch the action onSpawn for the L2NpcInstance
			mob.onSpawn();

			// Init other values of the L2NpcInstance (ex : from its L2CharTemplate for INT, STR, DEX...) and add it in the world as a visible object
			mob.spawnMe2(newLoc, respawn);

			notifyNpcSpawned(mob);

			// Increase the current number of L2NpcInstance managed by this L2Spawn
			_currentCount++;
		}
		else
		{
			mob.setXYZInvisible(newLoc);

			// Update the current number of SpawnTask in progress or stand by of this L2Spawn
			_scheduledCount++;

			// Create a new SpawnTask to launch after the respawn Delay
			addSpawnTask(mob, _respawnTime * 1000L - System.currentTimeMillis());
		}

		// Спавнится в указанном отражении
		mob.setReflection(getReflection());

		_lastSpawn = mob;
		//if(mob == null)
		//	_log.info("L2NpcInstance 4: "+getNpcId());
		return mob;
	}

	private void addSpawnTask(L2NpcInstance actor, long interval)
	{
		SpawnTaskManager.getInstance().addSpawnTask(actor, interval);
	}

	public static void addSpawnListener(SpawnListener listener)
	{
		synchronized (_spawnListeners)
		{
			_spawnListeners.add(listener);
		}
	}

	public static void notifyNpcSpawned(L2NpcInstance npc)
	{
		synchronized (_spawnListeners)
		{
			for(SpawnListener listener : _spawnListeners)
				listener.npcSpawned(npc);
		}
	}

	public static void notifyNpcDeSpawned(L2NpcInstance npc)
	{
		synchronized (_spawnListeners)
		{
			for(SpawnListener listener : _spawnListeners)
				listener.npcDeSpawned(npc);
		}
	}

	/**
	 * @param respawnDelay delay in seconds
	 */
	public void setRespawnDelay(int respawnDelay, int respawnDelayRandom)
	{
		if(respawnDelay < 0)
			_log.warning("respawn delay is negative for npcId: " + getNpcId());

		_nativeRespawnDelay = respawnDelay;
		_respawnDelay = respawnDelay > ConfigValue.MinRespawnTime ? respawnDelay : ConfigValue.MinRespawnTime;
		_respawnDelayRandom = respawnDelayRandom > 0 ? respawnDelayRandom : 0;
	}

	public void setRespawnDelay(int respawnDelay)
	{
		setRespawnDelay(respawnDelay, 0);
	}

	/**
	 * Устанавливает время следующего респауна, в unixtime
	 */
	public void setRespawnTime(int respawnTime)
	{
		_respawnTime = respawnTime;
	}

	public L2NpcInstance getLastSpawn()
	{
		return _lastSpawn;
	}

	/**
	 * @param oldNpc
	 */
	public void respawnNpc(L2NpcInstance oldNpc)
	{
		oldNpc.refreshID();
		intializeNpc(oldNpc, true, true);
	}

	public void setSiegeId(int id)
	{
		_siegeId = id;
	}

	public int getSiegeId()
	{
		return _siegeId;
	}

	public L2NpcTemplate getTemplate()
	{
		return _npcTemplate;
	}

	@Override
	public L2Spawn clone()
	{
		L2Spawn spawnDat = null;
		try
		{
			spawnDat = new L2Spawn(_npcId);
			spawnDat.setLocation(_location);
			spawnDat.setLocation3(_territory);
			spawnDat.setBanedTerritory(_banedTerritory);
			spawnDat.setLocx(_locx);
			spawnDat.setLocy(_locy);
			spawnDat.setLocz(_locz);
			spawnDat.setHeading(_heading);
			spawnDat.setAmount(_maximumCount);
			spawnDat.setRespawnDelay(_respawnDelay, _respawnDelayRandom);
			spawnDat.setAIParam(getAIParam());
			spawnDat._events = _events;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return spawnDat;
	}

	public void setBanedTerritory(L2Territory[] terr)
	{
		_banedTerritory = terr;
	}

	public void setBanedTerritory(String terr)
	{
		String t[] = terr.split(";");
		if(t.length > 1)
		{
			_banedTerritory = new L2Territory[t.length];
			for(int i = 0;i<t.length;i++)
				_banedTerritory[i] = TerritoryTable.getInstance().getLocation(Integer.parseInt(t[i]));
		}
	}

	public L2Territory[] getBanedTerritory()
	{
		return _banedTerritory;
	}

	public int[] getRandomPoint()
	{
		int rnd = Rnd.get(getLocation2().length);
		int p[] = null;
		try
		{
			L2Territory terr = getLocation2()[rnd];
			p = terr.getRandomPoint(getBanedTerritory());
			setLocation(terr.getId()*-1);
		}
		catch(Exception e)
		{
			System.out.println("getRandomPoint: rnd="+rnd+" getNpcId="+getNpcId()+"  getLocation2().length="+getLocation2().length);
			e.printStackTrace();
		}
		return p;
	}

	public void setAIParam(String value)
	{
		_AIParam = value;
	}

	public String getAIParam()
	{
		return _AIParam;
	}

	private void setAIField(L2CharacterAI templ, String fieldName, String value)
	{
		try
		{
			Field f = templ.getClass().getField(fieldName);
			setToType(templ, fieldName, value, "L2Spawn(686): TODO::Warning text...");
			f = null;
			fieldName = null;
		}
		catch(Exception e)
		{
			_log.info("L2Spawn(724): Error setAIField=>: AI='"+templ+"' field='"+fieldName+"' value='"+value+"'");
			e.printStackTrace();
		}
	}

	private void setToType(L2CharacterAI nem, String fieldName, String value, String text)
	{
		try
		{
			Field f = nem.getClass().getField(fieldName);
			if(f.getType().getName().equals("int"))
				f.setInt(nem, Integer.parseInt(value));
			else if(f.getType().getName().equals("boolean"))
				f.setBoolean(nem, Boolean.parseBoolean(value));
			else if(f.getType().getName().equals("byte"))
				f.setByte(nem, Byte.parseByte(value));
			else if(f.getType().getName().equals("double"))
				f.setDouble(nem, Double.parseDouble(value));
			else if(f.getType().getName().equals("float"))
				f.setFloat(nem, Float.parseFloat(value));
			else if(f.getType().getName().equals("long"))
				f.setLong(nem, Long.parseLong(value));
			else if(f.getType().getName().equals("short"))
				f.setShort(nem, Short.parseShort(value));
			else if(f.getType().getName().equals("java.lang.String"))
				f.set(nem, value);
			else if(f.getType().getName().equals("[J"))
				f.set(f, Util.parseCommaSeparatedLongArray(value.replace(" ", "")));
			else if(f.getType().getName().equals("[I"))
				f.set(nem, Util.parseCommaSeparatedIntegerArray(value));
			else if(f.getType().getName().equals("[D"))
				f.set(nem, Util.parseCommaSeparatedDoubleArray(value));
			else if(f.getType().getName().startsWith("[F"))
				f.set(nem, Util.parseCommaSeparatedFloatArray(value));
			else if(f.getType().getName().startsWith("[Ljava.lang.String"))
				f.set(nem, value);
		}
		catch(Exception e)
		{
			_log.warning(text);
			e.printStackTrace();
		}
	}
}