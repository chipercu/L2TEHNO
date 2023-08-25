package l2open.gameserver.model.entity.olympiad;

import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.util.GArray;

public class Stadia
{
	private int _olympiadStadiaId = 0;
	private L2Zone _Zone;

	public Stadia(int olympiadStadiaId)
	{
		_olympiadStadiaId = olympiadStadiaId;
	}

	public final int getOlympiadStadiaId()
	{
		return _olympiadStadiaId;
	}

	public final L2Zone getZone()
	{
		if(_Zone == null)
			_Zone = ZoneManager.getInstance().getZoneByIndex(L2Zone.ZoneType.OlympiadStadia, _olympiadStadiaId, true);
		return _Zone;
	}
}