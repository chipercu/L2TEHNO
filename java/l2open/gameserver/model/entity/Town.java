package l2open.gameserver.model.entity;

import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.util.Location;

import java.util.logging.Logger;

public class Town
{
	protected static Logger _log = Logger.getLogger(Town.class.getName());

	// =========================================================
	// Data Field
	private int _CastleIndex = 0; // This is the index of the castle controling over this town
	private String _Name = "";
	private int _RedirectToTownId = 0; // This is the id of the town to redirect players to
	//private double _TaxRate = 0;    // This is the town's local tax rate used by merchant
	private int _TownId = 0;
	private L2Zone _Zone;

	// =========================================================
	// Constructor
	public Town(int townId)
	{
		_TownId = townId;
		loadData();
	}

	// =========================================================
	// Method - Public
	/** Return true if object is inside the zone */
	public boolean checkIfInZone(L2Object obj)
	{
		return _Zone.checkIfInZone(obj);
	}

	/** Return true if object is inside the zone */
	public boolean checkIfInZone(int x, int y)
	{
		return _Zone.checkIfInZone(x, y);
	}

	// =========================================================
	// Method - Private
	private void loadData()
	{
		_Zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.Town, _TownId, true);

		if(_Zone != null)
		{
			_CastleIndex = _Zone.getTaxById();
			_Name = _Zone.getName();
		}

		switch(_TownId)
		{
			case 6:
				_RedirectToTownId = 7; // Gludio => Gludin
				break;
			case 8:
				_RedirectToTownId = 8; // Dion => Gludio
				break;
			case 9:
				_RedirectToTownId = 19; // Giran => Floran (should be Giran Harbor, but its not a zone town "yet")
				break;
			case 10:
				_RedirectToTownId = 12; // Oren => HV
				break;
			case 11:
				_RedirectToTownId = 10; // Aden => Oren
				break;
			case 13:
				_RedirectToTownId = 19; // Heine => Floran (should be Giran Harbor, but its not a zone town "yet")
				break;
			case 15:
				_RedirectToTownId = 14; // Goddard => Rune
				break;
			case 14:
				_RedirectToTownId = 16; // Rune => Shuttgart
				break;
			case 16:
				_RedirectToTownId = 4; // Shuttgart => Floran
				break;
			default:
				_RedirectToTownId = getTownId();
				break;
		}
	}

	// =========================================================
	// Property
	public final Castle getCastle()
	{
		return CastleManager.getInstance().getCastleByIndex(_CastleIndex);
	}

	public final int getCastleIndex()
	{
		return _CastleIndex;
	}

	public final String getName()
	{
		return _Name;
	}

	public final Location getSpawn()
	{
		// Если печатью владеют лорды Рассвета (Dawn), и в данном городе идет осада, то телепортирует во 2-й по счету город.
		if(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN && _RedirectToTownId != getTownId() && getCastle() != null && getCastle().getSiege().isInProgress())
			return TownManager.getInstance().getTown(_RedirectToTownId).getSpawn();
		return _Zone.getSpawn();
	}

	public final Location getPKSpawn()
	{
		// Если печатью владеют лорды Рассвета (Dawn), и в данном городе идет осада, то телепортирует во 2-й по счету город.
		if(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN && _RedirectToTownId != getTownId() && getCastle() != null && getCastle().getSiege().isInProgress())
			return TownManager.getInstance().getTown(_RedirectToTownId).getPKSpawn();
		return _Zone.getPKSpawn();
	}

	public final int getRedirectToTownId()
	{
		return _RedirectToTownId;
	}

	public final int getTownId()
	{
		return _TownId;
	}

	public final L2Zone getZone()
	{
		return _Zone;
	}
}