package l2open.gameserver.model;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.Announcements;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Auto Spawn Handler
 *
 * Allows spawning of a NPC object based on a timer.
 * (From the official idea used for the Merchant and Blacksmith of Mammon)
 *
 * General Usage:
 * - Call registerSpawn() with the parameters listed below.
 *			 int npcId
 *			 int[][] spawnPoints or specify NULL to add points later.
 *			 int initialDelay (If < 0 = default value)
 *			 int respawnDelay (If < 0 = default value)
 *			 int despawnDelay (If < 0 = default value or if = 0, function disabled)
 *
 *	 spawnPoints is a standard two-dimensional int array containing X,Y and Z coordinates.
 *	 The default respawn/despawn delays are currently every hour (as for Mammon on official servers).
 *
 * - The resulting AutoSpawnInstance object represents the newly added spawn index.
 * - The interal methods of this object can be used to adjust random spawning, for instance a call to setRandomSpawn(1, true); would set the spawn at index 1
 *	 to be randomly rather than sequentially-based.
 * - Also they can be used to specify the number of NPC instances to spawn
 *	 using setSpawnCount(), and broadcast a message to all users using setBroadcast().
 *
 *	 Random Spawning = OFF by default
 *	 Broadcasting = OFF by default
 *
 * @author Tempy
 *
 */
public class AutoSpawnHandler
{
	protected static Logger _log = Logger.getLogger(AutoSpawnHandler.class.getName());
	private static AutoSpawnHandler _instance;

	private static final int DEFAULT_INITIAL_SPAWN = 30000; // 30 seconds after registration
	private static final int DEFAULT_RESPAWN = 3600000; //1 hour in millisecs
	private static final int DEFAULT_DESPAWN = 3600000; //1 hour in millisecs

	protected Map<Integer, AutoSpawnInstance> _registeredSpawns;
	protected Map<Integer, ScheduledFuture<?>> _runningSpawns;
	protected boolean _activeState = true;

	public AutoSpawnHandler()
	{
		_registeredSpawns = new FastMap<Integer, AutoSpawnInstance>().setShared(true);
		_runningSpawns = new FastMap<Integer, ScheduledFuture<?>>().setShared(true);

		restoreSpawnData();
	}

	public static AutoSpawnHandler getInstance()
	{
		if(_instance == null)
			_instance = new AutoSpawnHandler();

		return _instance;
	}

	public final int size()
	{
		return _registeredSpawns.size();
	}

	private void restoreSpawnData()
	{
		int numLoaded = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		FiltredPreparedStatement statement2 = null;
		ResultSet rset = null, rset2 = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			// Restore spawn group data, then the location data.
			statement = con.prepareStatement("SELECT * FROM random_spawn ORDER BY groupId ASC");
			statement2 = con.prepareStatement("SELECT * FROM random_spawn_loc WHERE groupId=?");

			rset = statement.executeQuery();
			while(rset.next())
			{
				// Register random spawn group, set various options on the created spawn instance.
				AutoSpawnInstance spawnInst = registerSpawn(rset.getInt("npcId"), rset.getInt("initialDelay"), rset.getInt("respawnDelay"), rset.getInt("despawnDelay"));
				spawnInst.setSpawnCount(rset.getByte("count"));
				spawnInst.setBroadcast(rset.getBoolean("broadcastSpawn"));
				spawnInst.setRandomSpawn(rset.getBoolean("randomSpawn"));
				numLoaded++;

				// Restore the spawn locations for this spawn group/instance.

				statement2.setInt(1, rset.getInt("groupId"));
				rset2 = statement2.executeQuery();
				while(rset2.next())
					// Add each location to the spawn group/instance.
					spawnInst.addSpawnLocation(rset2.getInt("x"), rset2.getInt("y"), rset2.getInt("z"), rset2.getInt("heading"));
				DatabaseUtils.closeResultSet(rset2);
			}
		}
		catch(Exception e)
		{
			_log.warning("AutoSpawnHandler: Could not restore spawn data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseSR(statement2, rset2);
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * Registers a spawn with the given parameters with the spawner, and marks it as
	 * active. Returns a AutoSpawnInstance containing info about the spawn.
	 *
	 * @param int npcId
	 * @param int[][] spawnPoints
	 * @param int initialDelay (If < 0 = default value)
	 * @param int respawnDelay (If < 0 = default value)
	 * @param int despawnDelay (If < 0 = default value or if = 0, function disabled)
	 * @return AutoSpawnInstance spawnInst
	 */
	public AutoSpawnInstance registerSpawn(int npcId, int[][] spawnPoints, int initialDelay, int respawnDelay, int despawnDelay)
	{
		if(initialDelay < 0)
			initialDelay = DEFAULT_INITIAL_SPAWN;

		if(respawnDelay < 0)
			respawnDelay = DEFAULT_RESPAWN;

		if(despawnDelay < 0)
			despawnDelay = DEFAULT_DESPAWN;

		AutoSpawnInstance newSpawn = new AutoSpawnInstance(npcId, initialDelay, respawnDelay, despawnDelay);

		if(spawnPoints != null)
			for(int[] spawnPoint : spawnPoints)
				newSpawn.addSpawnLocation(spawnPoint);

		int newId = IdFactory.getInstance().getNextId();
		newSpawn._objectId = newId;
		_registeredSpawns.put(newId, newSpawn);

		setSpawnActive(newSpawn, true);

		return newSpawn;
	}

	/**
	 * Registers a spawn with the given parameters with the spawner, and marks it as
	 * active. Returns a AutoSpawnInstance containing info about the spawn.
	 * <BR>
	 * <B>Warning:</B> Spawn locations must be specified separately using addSpawnLocation().
	 *
	 * @param int npcId
	 * @param int initialDelay (If < 0 = default value)
	 * @param int respawnDelay (If < 0 = default value)
	 * @param int despawnDelay (If < 0 = default value or if = 0, function disabled)
	 * @return AutoSpawnInstance spawnInst
	 */
	public AutoSpawnInstance registerSpawn(int npcId, int initialDelay, int respawnDelay, int despawnDelay)
	{
		return registerSpawn(npcId, null, initialDelay, respawnDelay, despawnDelay);
	}

	/**
	 * Remove a registered spawn from the list, specified by the given spawn instance.
	 *
	 * @param AutoSpawnInstance spawnInst
	 * @return boolean removedSuccessfully
	 */
	public boolean removeSpawn(AutoSpawnInstance spawnInst)
	{
		if(!isSpawnRegistered(spawnInst))
			return false;

		try
		{
			// Try to remove from the list of registered spawns if it exists.
			_registeredSpawns.remove(spawnInst.getNpcId());

			// Cancel the currently associated running scheduled task.
			ScheduledFuture<?> respawnTask = _runningSpawns.remove(spawnInst._objectId);
			respawnTask.cancel(false);
		}
		catch(Exception e)
		{
			_log.warning("AutoSpawnHandler: Could not auto spawn for NPC ID " + spawnInst._npcId + " (Object ID = " + spawnInst._objectId + "): " + e);
			return false;
		}

		return true;
	}

	/**
	 * Remove a registered spawn from the list, specified by the given spawn object ID.
	 *
	 * @param int objectId
	 * @return boolean removedSuccessfully
	 */
	public void removeSpawn(int objectId)
	{
		removeSpawn(_registeredSpawns.get(objectId));
	}

	/**
	 * Sets the active state of the specified spawn.
	 *
	 * @param AutoSpawnInstance spawnInst
	 * @param boolean isActive
	 */
	public void setSpawnActive(AutoSpawnInstance spawnInst, boolean isActive)
	{
		int objectId = spawnInst._objectId;

		if(isSpawnRegistered(objectId))
		{
			ScheduledFuture<?> spawnTask = null;

			if(isActive)
			{
				AutoSpawner rset = new AutoSpawner(objectId);
				if(spawnInst._desDelay > 0)
					spawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(rset, spawnInst._initDelay, spawnInst._resDelay);
				else
				{
					long time = RaidBossSpawnManager.getInstance().getRespawnTime(spawnInst.getNpcId());
					//_log.info("setSpawnActive["+spawnInst.getNpcId()+"]: time="+time);
					spawnTask = ThreadPoolManager.getInstance().schedule(rset, time == 0 ? spawnInst._initDelay : time);
				}
				//spawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(rset, spawnInst._initDelay, spawnInst._resDelay);
				_runningSpawns.put(objectId, spawnTask);
			}
			else
			{
				spawnTask = _runningSpawns.remove(objectId);

				if(spawnTask != null)
					spawnTask.cancel(false);
			}

			spawnInst.setSpawnActive(isActive);
		}
	}

	/**
	 * Returns the number of milliseconds until the next occurrance of
	 * the given spawn.
	 *
	 * @param AutoSpawnInstance spawnInst
	 * @param long milliRemaining
	 */
	public final long getTimeToNextSpawn(AutoSpawnInstance spawnInst)
	{
		int objectId = spawnInst._objectId;

		if(!isSpawnRegistered(objectId))
			return -1;

		return _runningSpawns.get(objectId).getDelay(TimeUnit.MILLISECONDS);
	}

	/**
	 * Attempts to return the AutoSpawnInstance associated with the given NPC or Object ID type.
	 * <BR>
	 * Note: If isObjectId == false, returns first instance for the specified NPC ID.
	 *
	 * @param int id
	 * @param boolean isObjectId
	 * @return AutoSpawnInstance spawnInst
	 */
	public final AutoSpawnInstance getAutoSpawnInstance(int id, boolean isObjectId)
	{
		if(isObjectId)
		{
			if(isSpawnRegistered(id))
				return _registeredSpawns.get(id);
		}
		else
			for(AutoSpawnInstance spawnInst : _registeredSpawns.values())
				if(spawnInst._npcId == id)
					return spawnInst;

		return null;
	}

	public Map<Integer, AutoSpawnInstance> getAllAutoSpawnInstance(int id)
	{
		Map<Integer, AutoSpawnInstance> spawnInstList = new FastMap<Integer, AutoSpawnInstance>().setShared(true);

		for(AutoSpawnInstance spawnInst : _registeredSpawns.values())
			if(spawnInst._npcId == id)
				spawnInstList.put(spawnInst._objectId, spawnInst);

		return spawnInstList;
	}

	/**
	 * Tests if the specified object ID is assigned to an auto spawn.
	 *
	 * @param int objectId
	 * @return boolean isAssigned
	 */
	public final boolean isSpawnRegistered(int objectId)
	{
		return _registeredSpawns.containsKey(objectId);
	}

	/**
	 * Tests if the specified spawn instance is assigned to an auto spawn.
	 *
	 * @param AutoSpawnInstance spawnInst
	 * @return boolean isAssigned
	 */
	public final boolean isSpawnRegistered(AutoSpawnInstance spawnInst)
	{
		return _registeredSpawns.containsValue(spawnInst);
	}

	public long getRespawnDelay(int id)
	{
		AutoSpawnInstance spawn = getAutoSpawnInstance(id, false);
		if(spawn == null)
			return 0;
		return System.currentTimeMillis()/1000+spawn.getRespawnDelay();
	}

	public void setResp(int id)
	{
		AutoSpawnInstance spawn = getAutoSpawnInstance(id, false);
		if(spawn != null && spawn.isSpawnActive())
			setSpawnActive(spawn, true);
	}

	/**
	 * AutoSpawner Class
	 * <BR><BR>
	 * This handles the main spawn task for an auto spawn instance, and initializes
	 * a despawner if required.
	 *
	 * @author Tempy
	 */
	private class AutoSpawner extends l2open.common.RunnableImpl
	{
		private int _objectId;

		AutoSpawner(int objectId)
		{
			_objectId = objectId;
		}

		public void runImpl()
		{
			try
			{
				// Retrieve the required spawn instance for this spawn task.
				AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

				// If the spawn is not scheduled to be active, cancel the spawn task.
				if(!spawnInst.isSpawnActive() || ConfigValue.StartWhisoutSpawn)
					return;

				Location[] locationList = spawnInst.getLocationList();

				// If there are no set co-ordinates, cancel the spawn task.
				if(locationList.length == 0)
				{
					_log.info("AutoSpawnHandler: No location co-ords specified for spawn instance (Object ID = " + _objectId + ").");
					return;
				}

				int locationCount = locationList.length;
				int locationIndex = Rnd.get(locationCount);

				/*
				 * If random spawning is disabled, the spawn at the next set of
				 * co-ordinates after the last. If the index is greater than the number
				 * of possible spawns, reset the counter to zero.
				 */
				if(!spawnInst.isRandomSpawn())
				{
					locationIndex = spawnInst._lastLocIndex;
					locationIndex++;

					if(locationIndex == locationCount)
						locationIndex = 0;

					spawnInst._lastLocIndex = locationIndex;
				}

				// Set the X, Y and Z co-ordinates, where this spawn will take place.
				final int x = locationList[locationIndex].x;
				final int y = locationList[locationIndex].y;
				final int z = locationList[locationIndex].z;
				final int heading = locationList[locationIndex].h;

				// Fetch the template for this NPC ID and create a new spawn.
				L2NpcTemplate npcTemp = NpcTable.getTemplate(spawnInst.getNpcId());
				L2Spawn newSpawn = new L2Spawn(npcTemp);

				newSpawn.setLocx(x);
				newSpawn.setLocy(y);
				newSpawn.setLocz(z);
				if(heading != -1)
					newSpawn.setHeading(heading);
				newSpawn.setAmount(spawnInst.getSpawnCount());
				if(spawnInst._desDelay <= 0)
					newSpawn.setRespawnDelay(spawnInst._resDelay);

				if(spawnInst.getNpcId() == 25286 || spawnInst.getNpcId() == 25283)
					RaidBossSpawnManager.getInstance().addNewSpawn(newSpawn);

				// Add the new spawn information to the spawn table, but do not store it.
				L2NpcInstance npcInst = null;

				for(int i = 0; i < spawnInst._spawnCount; i++)
				{
					npcInst = newSpawn.doSpawn(true);

					// To prevent spawning of more than one NPC in the exact same spot,
					// move it slightly by a small random offset.
					npcInst.setXYZ(npcInst.getX() + Rnd.get(50), npcInst.getY() + Rnd.get(50), npcInst.getZ());

					// Add the NPC instance to the list of managed instances.
					spawnInst.addAttackable(npcInst);
				}

				String nearestTown = TownManager.getInstance().getClosestTownName(npcInst);

				// Announce to all players that the spawn has taken place, with the nearest town location.
				if(spawnInst.isBroadcasting() && npcInst != null)
					Announcements.getInstance().announceToAll("The " + npcInst.getName() + " has spawned near " + nearestTown + "!");

				// If there is no despawn time, do not create a despawn task.
				if(spawnInst.getDespawnDelay() > 0)
				{
					AutoDespawner rd = new AutoDespawner(_objectId);
					ThreadPoolManager.getInstance().schedule(rd, spawnInst.getDespawnDelay() - 1000);
				}
			}
			catch(Exception e)
			{
				_log.warning("AutoSpawnHandler: An error occurred while initializing spawn instance (Object ID = " + _objectId + "): " + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * AutoDespawner Class
	 * <BR><BR>
	 * Simply used as a secondary class for despawning an auto spawn instance.
	 *
	 * @author Tempy
	 */
	private class AutoDespawner extends l2open.common.RunnableImpl
	{
		private int _objectId;

		AutoDespawner(int objectId)
		{
			_objectId = objectId;
		}

		public void runImpl()
		{
			try
			{
				AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

				for(L2NpcInstance npcInst : spawnInst.getAttackableList())
				{
					npcInst.deleteMe();
					spawnInst.removeAttackable(npcInst);
				}
			}
			catch(Exception e)
			{
				_log.warning("AutoSpawnHandler: An error occurred while despawning spawn (Object ID = " + _objectId + "): " + e);
			}
		}
	}

	/**
	 * AutoSpawnInstance Class
	 * <BR><BR>
	 * Stores information about a registered auto spawn.
	 *
	 * @author Tempy
	 */
	public class AutoSpawnInstance
	{
		protected int _objectId;
		protected int _spawnIndex;

		protected int _npcId;
		protected int _initDelay;
		protected int _resDelay;
		protected int _desDelay;
		protected byte _spawnCount = 1;
		protected int _lastLocIndex = -1;

		private GArray<L2NpcInstance> _npcList = new GArray<L2NpcInstance>();
		private GArray<Location> _locList = new GArray<Location>();

		private boolean _spawnActive;
		private boolean _randomSpawn = false;
		private boolean _broadcastAnnouncement = false;

		protected AutoSpawnInstance(int npcId, int initDelay, int respawnDelay, int despawnDelay)
		{
			_npcId = npcId;
			_initDelay = initDelay;
			_resDelay = respawnDelay;
			_desDelay = despawnDelay;
		}

		void setSpawnActive(boolean activeValue)
		{
			_spawnActive = activeValue;
		}

		boolean addAttackable(L2NpcInstance npcInst)
		{
			return _npcList.add(npcInst);
		}

		boolean removeAttackable(L2NpcInstance npcInst)
		{
			return _npcList.remove(npcInst);
		}

		public int getObjectId()
		{
			return _objectId;
		}

		public int getInitialDelay()
		{
			return _initDelay;
		}

		public int getRespawnDelay()
		{
			return _resDelay;
		}

		public int getDespawnDelay()
		{
			return _desDelay;
		}

		public int getNpcId()
		{
			return _npcId;
		}

		public int getSpawnCount()
		{
			return _spawnCount;
		}

		public Location[] getLocationList()
		{
			return _locList.toArray(new Location[_locList.size()]);
		}

		public L2NpcInstance[] getAttackableList()
		{
			return _npcList.toArray(new L2NpcInstance[_npcList.size()]);
		}

		public L2Spawn[] getSpawns()
		{
			GArray<L2Spawn> npcSpawns = new GArray<L2Spawn>();

			for(L2NpcInstance npcInst : _npcList)
				npcSpawns.add(npcInst.getSpawn());

			return npcSpawns.toArray(new L2Spawn[npcSpawns.size()]);
		}

		public void setSpawnCount(byte spawnCount)
		{
			_spawnCount = spawnCount;
		}

		public void setRandomSpawn(boolean randValue)
		{
			_randomSpawn = randValue;
		}

		public void setBroadcast(boolean broadcastValue)
		{
			_broadcastAnnouncement = broadcastValue;
		}

		public boolean isSpawnActive()
		{
			return _spawnActive;
		}

		public boolean isRandomSpawn()
		{
			return _randomSpawn;
		}

		public boolean isBroadcasting()
		{
			return _broadcastAnnouncement;
		}

		public boolean addSpawnLocation(int x, int y, int z, int heading)
		{
			return _locList.add(new Location(x, y, z, heading));
		}

		public boolean addSpawnLocation(int[] spawnLoc)
		{
			if(spawnLoc.length != 3)
				return false;

			return addSpawnLocation(spawnLoc[0], spawnLoc[1], spawnLoc[2], -1);
		}

		public Location removeSpawnLocation(int locIndex)
		{
			try
			{
				return _locList.remove(locIndex);
			}
			catch(IndexOutOfBoundsException e)
			{
				return null;
			}
		}
	}
}
