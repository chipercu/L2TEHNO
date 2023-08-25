package l2open.gameserver.tables;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2RoundTerritory;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.L2World;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Logger;

public class TerritoryTable
{
	private static Logger _log = Logger.getLogger(TerritoryTable.class.getName());
	private static final TerritoryTable _instance = new TerritoryTable();
	private static HashMap<Integer, L2Territory> _locations;

	public static TerritoryTable getInstance()
	{
		return _instance;
	}

	private TerritoryTable()
	{
		reloadData();
	}

	public L2Territory getLocation(int terr)
	{
		L2Territory t = _locations.get(terr);
		if(t == null)
		{
			_log.warning("TerritoryTable.getLocation: territory " + terr + " not found.");
			l2open.util.Util.test();
		}
		return t;
	}

	public int[] getRandomPoint(int terr)
	{
		L2Territory t = _locations.get(terr);
		if(t == null)
		{
			_log.warning("TerritoryTable.getRandomPoint: territory " + terr + " not found.");
			return new int[3];
		}
		return t.getRandomPoint();
	}

	public int getMinZ(int terr)
	{
		L2Territory t = _locations.get(terr);
		if(t == null)
		{
			_log.warning("TerritoryTable.getMinZ: territory " + terr + " not found.");
			return 0;
		}
		return t.getZmin();
	}

	public int getMaxZ(int terr)
	{
		L2Territory t = _locations.get(terr);
		if(t == null)
		{
			_log.warning("TerritoryTable.getMaxZ: territory " + terr + " not found.");
			return 0;
		}
		return t.getZmax();
	}

	public void reloadData()
	{
		if(_locations != null)
			for(L2Territory terr : _locations.values())
				if(terr.isWorldTerritory())
					L2World.removeTerritory(terr);

		_locations = new HashMap<Integer, L2Territory>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT loc_id, loc_x, loc_y, loc_zmin, loc_zmax, radius FROM `locations`");
			rset = statement.executeQuery();
			while(rset.next())
			{
				int terr = rset.getInt("loc_id");
				if(rset.getInt("radius") > 0)
				{
					if(_locations.get(terr) == null)
					{
						L2RoundTerritory t = new L2RoundTerritory(terr, rset.getInt("loc_x"), rset.getInt("loc_y"), rset.getInt("radius"), rset.getInt("loc_zmin"), rset.getInt("loc_zmax"));
						_locations.put(terr, t);
					}
				}
				else
				{
					if(_locations.get(terr) == null)
					{
						L2Territory t = new L2Territory(terr);
						_locations.put(terr, t);
					}
					_locations.get(terr).add(rset.getInt("loc_x"), rset.getInt("loc_y"), rset.getInt("loc_zmin"), rset.getInt("loc_zmax"));
				}
			}
		}
		catch(Exception e1)
		{
			//problem with initializing spawn, go to next one
			_log.warning("locations couldnt be initialized:" + e1);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		for(L2Territory t : _locations.values())
			t.validate();

		_log.info("TerritoryTable: Loaded " + _locations.size() + " locations");
	}

	public void registerZones()
	{
		int registered = 0;
		for(L2Territory terr : _locations.values())
			if(terr.isWorldTerritory())
			{
				//_log.info("TerritoryTable: Add " + terr + " zone="+terr.getZone());
				L2World.addTerritory(terr);
				registered++;
			}

		_log.info("TerritoryTable: Added " + registered + " locations to L2World");
	}

	public HashMap<Integer, L2Territory> getLocations()
	{
		return _locations;
	}

	public static void unload()
	{
		_locations.clear();
	}
}