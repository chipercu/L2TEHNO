package l2open.gameserver.instancemanager;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.util.GArray;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CastleManager
{
	protected static Logger _log = Logger.getLogger(CastleManager.class.getName());

	private static CastleManager _instance;
	private HashMap<Integer, Castle> _castles;

	public static CastleManager getInstance()
	{
		if(_instance == null)
			_instance = new CastleManager();
		return _instance;
	}

	public CastleManager()
	{
		load();
	}

	private void load()
	{
		GArray<L2Zone> zones = ZoneManager.getInstance().getZoneByType(ZoneType.Castle);
		if(zones.size() == 0)
			_log.info("Not found zones for Castles!!!");
		else
			for(L2Zone zone : zones)
			{
				Castle castle = new Castle(zone.getIndex());
				castle.init();
				getCastles().put(zone.getIndex(), castle);
			}
		_log.info("Loaded: " + getCastles().size() + " castles.");
	}

	/**
	 * Возвращает замок, соответствующий индексу.
	 */
	public Castle getCastleByIndex(int index)
	{
		return getCastles().get(index);
	}

	/**
	 * Находит замок по имени. Если такого замка нет - возвращает null.
	 */
	public Castle getCastleByName(String name)
	{
		int index = getCastleIndexByName(name);
		if(index > 0)
			return getCastles().get(index);
		return null;
	}

	/**
	 * Если координаты принадлежат зоне какого-либо замка, возвращает этот замок.
	 * Иначе возвращает null.
	 */
	public Castle getCastleByObject(L2Object activeObject)
	{
		return getCastleByCoord(activeObject.getX(), activeObject.getY());
	}

	/**
	 * Если обьект находится в зоне какого-либо замка, возвращает этот замок.
	 * Иначе возвращает null.
	 */
	public Castle getCastleByCoord(int x, int y)
	{
		int index = getCastleIndexByCoord(x, y);
		if(index > 0)
			return getCastles().get(index);
		return null;
	}

	/**
	 * Если обьект находится в зоне какого-либо замка, возвращает индекс этого замка.
	 * Иначе возвращает -1.
	 */
	public int getCastleIndex(L2Object activeObject)
	{
		return getCastleIndexByCoord(activeObject.getX(), activeObject.getY());
	}

	/**
	 * Если координаты принадлежат зоне какого-либо замка, возвращает индекс этого замка.
	 * Иначе возвращает -1.
	 */
	public int getCastleIndexByCoord(int x, int y)
	{
		Residence castle;
		for(int i = 1; i <= getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if(castle != null && castle.checkIfInZone(x, y))
				return i;
		}
		return -1;
	}

	/**
	 * Находит замок по имени, без учета регистра.
	 * Если не найден - возвращает -1.
	 */
	public int getCastleIndexByName(String name)
	{
		Residence castle;
		for(int i = 1; i <= getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if(castle != null && castle.getName().equalsIgnoreCase(name.trim()))
				return i;
		}
		return -1;
	}

	/**
	 * Возвращает список, содержащий все замки.
	 */
	public HashMap<Integer, Castle> getCastles()
	{
		if(_castles == null)
			_castles = new HashMap<Integer, Castle>();
		return _castles;
	}

	public final Castle getCastleByOwner(L2Clan clan)
	{
		if(clan == null)
			return null;
		for(Castle castle : getCastles().values())
			if(clan.getClanId() == castle.getOwnerId())
				return castle;
		return null;
	}

	static Map<Integer, String> _music = new HashMap<Integer, String>();
	public Map<Integer, String> getMusic()
	{
		return _music;
	}
}