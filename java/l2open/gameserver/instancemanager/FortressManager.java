package l2open.gameserver.instancemanager;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.tables.ClanTable;
import l2open.util.GArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class FortressManager
{
	protected static Logger _log = Logger.getLogger(FortressManager.class.getName());

	private static FortressManager _instance;
	private HashMap<Integer, Fortress> _fortresses;
	private ScheduledFuture<?> _fortCheckTask;

	public static FortressManager getInstance()
	{
		if(_instance == null)
		{
			_log.info("Initializing FortressManager");
			_instance = new FortressManager();
			_instance.load();
		}
		return _instance;
	}

	private void load()
	{
		GArray<L2Zone> zones = ZoneManager.getInstance().getZoneByType(ZoneType.Fortress);
		if(zones.size() == 0)
			_log.info("Not found zones for Fortresses!!!");
		else
			for(L2Zone zone : zones)
			{
				Fortress fortress = new Fortress(zone.getIndex());
				fortress.init();
				getFortresses().put(zone.getIndex(), fortress);
			}
		if(_fortCheckTask != null)
			_fortCheckTask.cancel(false);
		_fortCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FortCheckTask(), 3600000, 3600000);
		_log.info("Loaded: " + getFortresses().size() + " fortresses.");
	}

	/**
	 * Возвращает крепость, соответствующую индексу.
	 */
	public Fortress getFortressByIndex(int index)
	{
		return getFortresses().get(index);
	}

	/**
	 * Находит крепость по имени. Если такой крепости нет - возвращает null.
	 */
	public Fortress getFortressByName(String name)
	{
		int index = getFortressIndexByName(name);
		if(index > 0)
			return getFortresses().get(index);
		return null;
	}

	/**
	 * Если координаты принадлежат зоне какой-либо крепости, возвращает эту крепость.
	 * Иначе возвращает null.
	 */
	public Fortress getFortressByObject(L2Object activeObject)
	{
		return getFortressByCoord(activeObject.getX(), activeObject.getY());
	}

	/**
	 * Если обьект находится в зоне какой-либо крепости, возвращает эту крепость.
	 * Иначе возвращает null.
	 */
	public Fortress getFortressByCoord(int x, int y)
	{
		for(Fortress fortress : getFortresses().values())
			if(fortress.checkIfInZone(x, y))
				return fortress;
		return null;
	}

	/**
	 * Если обьект находится в зоне какой-либо крепости, возвращает эту крепость.
	 * Иначе возвращает -1.
	 */
	public int getFortressIndex(L2Object activeObject)
	{
		return getFortressIndexByCoord(activeObject.getX(), activeObject.getY());
	}

	/**
	 * Если координаты принадлежат зоне какой-либо крепости, возвращает индекс этой крепости.
	 * Иначе возвращает -1.
	 */
	public int getFortressIndexByCoord(int x, int y)
	{
		for(Fortress fortress : getFortresses().values())
			if(fortress.checkIfInZone(x, y))
				return fortress.getId();
		return -1;
	}

	/**
	 * Находит крепость по имени, без учета регистра.
	 * Если не найден - возвращает -1.
	 */
	public int getFortressIndexByName(String name)
	{
		for(Fortress fortress : getFortresses().values())
			if(fortress.getName().equalsIgnoreCase(name.trim()))
				return fortress.getId();
		return -1;
	}

	/**
	 * Возвращает список, содержащий все крепости.
	 */
	public HashMap<Integer, Fortress> getFortresses()
	{
		if(_fortresses == null)
			_fortresses = new HashMap<Integer, Fortress>();
		return _fortresses;
	}

	public int findNearestFortressIndex(int x, int y, int offset)
	{
		int index = getFortressIndexByCoord(x, y);
		if(index < 0)
		{
			double closestDistance = offset;
			double distance;
			for(Fortress fortress : getFortresses().values())
			{
				distance = fortress.getZone().findDistanceToZone(x, y, 0, false);
				if(closestDistance > distance)
				{
					closestDistance = distance;
					index = fortress.getId();
				}
			}
		}
		return index;
	}

	public final Fortress getFortressByOwner(L2Clan clan)
	{
		if(clan == null)
			return null;
		for(Fortress fort : getFortresses().values())
			if(clan.getClanId() == fort.getOwnerId())
				return fort;
		return null;
	}

	public class FortCheckTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			try
			{
				for(Fortress fort : getFortresses().values())
					if(fort.getOwner() != null && !fort.getSiege().isInProgress() && System.currentTimeMillis() - fort.getOwnDate() * 1000L > 168 * 60 * 60 * 1000L)
					{
						for(L2Player player : fort.getOwner().getOnlineMembers(0))
							if(player != null)
							{
								player.sendPacket(Msg.THE_REBEL_ARMY_RECAPTURED_THE_FORTRESS);
								if(fort.checkIfInZone(player))
									player.teleToClosestTown();
							}
						for(L2DoorInstance door : fort.getDoors())
							door.closeMe();
						_log.warning("Fortress " + fort.getName() + " recaptured by NPC from clan " + ClanTable.getInstance().getClan(fort.getOwnerId()) + " (ownDate: " + fort.getOwnDate() + ")");
						fort.changeOwner(null);
					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	static Map<Integer, String> _music = new HashMap<Integer, String>();
	public Map<Integer, String> getMusic()
	{
		return _music;
	}
}