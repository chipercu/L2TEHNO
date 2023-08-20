package l2open.gameserver.instancemanager;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.util.Location;
import l2open.util.Log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class NaiaTowerManager
{
	private static final Logger _log = Logger.getLogger(NaiaTowerManager.class.getName());

	public static ConcurrentHashMap<Integer, List<L2Player>> _groupList = new ConcurrentHashMap<Integer, List<L2Player>>();
	public static ConcurrentHashMap<Integer, List<L2Player>> _roomsDone = new ConcurrentHashMap<Integer, List<L2Player>>();
	public static ConcurrentHashMap<Integer, Long> _groupTimer = new ConcurrentHashMap<Integer, Long>();
	public static Map<Integer, List<L2NpcInstance>> _roomMobs;
	public static List<L2NpcInstance> _roomMobList;
	public static long _towerAccessible = 0L;
	public static Integer _index = 0;
	public static Map<Integer, Boolean> lockedRooms;
	private static final NaiaTowerManager _instance = new NaiaTowerManager();

	public static NaiaTowerManager getInstance()
	{
		return _instance;
	}

	public NaiaTowerManager()
	{
		if(lockedRooms == null)
		{
			lockedRooms = new HashMap<Integer, Boolean>();
			for(int i = 18494; i <= 18505; i++)
				lockedRooms.put(i, false);
			_roomMobs = new HashMap<Integer, List<L2NpcInstance>>();
			for(int i = 18494; i <= 18505; i++)
			{
				_roomMobList = new ArrayList<L2NpcInstance>();
				_roomMobs.put(i, _roomMobList);
			}

			_log.info("Naia Tower Manager: Loaded 12 rooms");
		}
		ThreadPoolManager.getInstance().schedule(new GroupTowerTimer(), 30000L);
	}

	public static void startNaiaTower(L2Player leader)
	{
		if(leader == null || _towerAccessible > System.currentTimeMillis())
			return;
		Location loc = leader.getLoc();
		for(L2Player member : leader.getParty().getPartyMembers())
		{
			if(member.isInRange(loc, 2000))
			{
				member.teleToLocation(new Location(-47271, 246098, -9120));
				Log.add("ROOM_START: "+member.getName(), "beleth_enter_room");
			}
			else
				Log.add("ROOM_NO_START: "+member.getName(), "beleth_enter_room");
		}
		addGroupToTower(leader);
		_towerAccessible = System.currentTimeMillis() + 1200000L;

		DoorTable.getInstance().getDoor(18250001).openMe();
	}

	private static void addGroupToTower(L2Player leader)
	{
		_index = _groupList.keySet().size() + 1;
		_groupList.put(_index, leader.getParty().getPartyMembers());
		_groupTimer.put(_index, System.currentTimeMillis() + 300000L);

		leader.sendMessage("The Tower of Naia countdown has begun. You have only 5 minutes to pass each room.");
	}

	public static void updateGroupTimer(L2Player player)
	{
		if(player.getParty() != null && !player.getParty().isLeader(player))
			player = player.getParty().getPartyLeader();
		for(int i : _groupList.keySet())
		{
			if(_groupList.get(i).contains(player))
			{
				_groupTimer.put(i, System.currentTimeMillis() + 300000L);
				player.sendMessage("Group timer has been updated");
				return;
			}
		}
	}

	public static void removeGroupTimer(L2Player player)
	{
		for(int i : _groupList.keySet())
		{
			if(_groupList.get(i).contains(player))
			{
				_groupList.remove(i);
				_groupTimer.remove(i);
			}
		}
	}

	public static boolean isLegalGroup(L2Player player)
	{
		if(_groupList == null || _groupList.isEmpty())
			return false;
		for(int i : _groupList.keySet())
			if(_groupList.get(i).contains(player))
				return true;
		return false;
	}

	public static void lockRoom(int npcId)
	{
		lockedRooms.remove(npcId);
		lockedRooms.put(npcId, true);
	}

	public static void unlockRoom(int npcId)
	{
		lockedRooms.remove(npcId);
		lockedRooms.put(npcId, false);
	}

	public static boolean isLockedRoom(int npcId)
	{
		return lockedRooms.get(npcId);
	}

	public static void addRoomDone(Integer roomId, L2Player player)
	{
		if(player.getParty() != null)
			_roomsDone.put(roomId, player.getParty().getPartyMembers());
	}

	public static boolean isRoomDone(int roomId, L2Player player)
	{
		if(_roomsDone == null || _roomsDone.isEmpty())
			return false;
		else if(_roomsDone.get(roomId) == null || _roomsDone.get(roomId).isEmpty())
			return false;
		return _roomsDone.get(roomId).contains(player);
	}

	public static void addMobsToRoom(int roomId, List<L2NpcInstance> mob)
	{
		_roomMobs.put(roomId, mob);
	}

	public static List<L2NpcInstance> getRoomMobs(int roomId)
	{
		return _roomMobs.get(roomId);
	}

	public static void removeRoomMobs(int roomId)
	{
		_roomMobs.get(roomId).clear();
	}

	private class GroupTowerTimer extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			ThreadPoolManager.getInstance().schedule(new GroupTowerTimer(), 30000L);
			if(!_groupList.isEmpty() && !_groupTimer.isEmpty())
			{
				for(int i : _groupTimer.keySet())
					if(_groupTimer.get(i) < System.currentTimeMillis())
					{
						for(L2Player kicked : _groupList.get(i))
						{
							kicked.teleToLocation(new Location(17656, 244328, 11595));
							Log.add("ROOM_FAIL: "+kicked.getName(), "beleth_enter_room");
							kicked.sendMessage("The time has expired. You cannot stay in Tower of Naia any longer");
						}
						_groupList.remove(i);
						_groupTimer.remove(i);
					}
				/*for(int j = 18494; j <= 18505; j++)
				{
					lockedRooms.remove(j);
					lockedRooms.put(j, false);
					_roomsDone.remove(j);
					for(L2NpcInstance npc : _roomMobs.get(j))
						if(npc != null && !npc.isDead())
							npc.deleteMe();
					_roomMobs.get(j).clear();
				}*/
			}
		}
	}
}