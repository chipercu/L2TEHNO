package l2open.gameserver.instancemanager;

import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.Coliseum;
import l2open.util.GArray;

import java.util.HashMap;
import java.util.logging.Logger;

public class UnderGroundColliseumManager
{
	protected static Logger _log = Logger.getLogger(UnderGroundColliseumManager.class.getName());

	private static UnderGroundColliseumManager _instance;

	private HashMap<Integer, Coliseum> _coliseums;

	public static UnderGroundColliseumManager getInstance()
	{
		if(_instance == null)
			_instance = new UnderGroundColliseumManager();
		return _instance;
	}

	public UnderGroundColliseumManager()
	{
		GArray<L2Zone> zones = ZoneManager.getInstance().getZoneByType(ZoneType.UnderGroundColiseum);
		if(zones.size() == 0)
			_log.info("Not found zones for UnderGround Colliseum!!!");
		else
			for(L2Zone zone : zones)
				getColiseums().put(zone.getIndex(), new Coliseum(zone.getIndex()));
		_log.info("Loaded: " + getColiseums().size() + " UnderGround Colliseums.");
	}

	public HashMap<Integer, Coliseum> getColiseums()
	{
		if(_coliseums == null)
			_coliseums = new HashMap<Integer, Coliseum>();
		return _coliseums;
	}

	public Coliseum getColiseumByLevelLimit(final int limit)
	{
		for(Coliseum coliseum : _coliseums.values())
			if(coliseum.getMaxLevel() == limit)
				return coliseum;
		return null;
	}
}