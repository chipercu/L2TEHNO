package l2open.gameserver.tables;

import l2open.config.ConfigValue;
import l2open.util.GArray;
import l2open.util.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.logging.Logger;

public class AirShipDocksTable
{
	private static Logger _log = Logger.getLogger(AirShipDocksTable.class.getName());
	private static AirShipDocksTable _instance;

	private GArray<AirShipDock> _list;

	public static AirShipDocksTable getInstance()
	{
		if(_instance == null)
			_instance = new AirShipDocksTable();
		return _instance;
	}

	public static void reload()
	{
		_instance = new AirShipDocksTable();
	}

	private AirShipDocksTable()
	{
		_list = new GArray<AirShipDock>();

		try
		{
			File file = new File(ConfigValue.DatapackRoot + "/data/xml/airship_docks.xml");
			DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
			factory1.setValidating(false);
			factory1.setIgnoringComments(true);
			Document doc1 = factory1.newDocumentBuilder().parse(file);

			int counter = 0;
			for(Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
				if("list".equalsIgnoreCase(n1.getNodeName()))
					for(Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
						if("dock".equalsIgnoreCase(d1.getNodeName()))
						{
							counter++;
							int id = Integer.parseInt(d1.getAttributes().getNamedItem("id").getNodeValue());
							Location loc = new Location(d1.getAttributes().getNamedItem("loc").getNodeValue());
							//int fuel = Integer.parseInt(d1.getAttributes().getNamedItem("fuel").getNodeValue());
							int fuel = 100;
							int airshipNpcId = Integer.parseInt(d1.getAttributes().getNamedItem("npcId").getNodeValue());
							AirShipDock ad = new AirShipDock(id, loc, fuel, airshipNpcId);
							ad.setArrivalTrajetId(Integer.parseInt(d1.getAttributes().getNamedItem("arrivalTrajetId").getNodeValue()));
							ad.setDepartureTrajetId(Integer.parseInt(d1.getAttributes().getNamedItem("departureTrajetId").getNodeValue()));
							ad.setDepartureMovieId(Integer.parseInt(d1.getAttributes().getNamedItem("movieId").getNodeValue()));
							ad.setIsTeleportPoint(Boolean.parseBoolean(d1.getAttributes().getNamedItem("isTeleportPoint").getNodeValue()));
							Location upset = new Location(d1.getAttributes().getNamedItem("upsetLoc").getNodeValue());
							ad.setUpsetLoc(upset);
							_list.add(ad);
						}
			_log.info("AirShipDocksTable: Loaded " + counter + " docks.");
		}
		catch(Exception e)
		{
			_log.warning("AirShipDocksTable: Lists could not be initialized.");
			e.printStackTrace();
		}
	}

	public GArray<AirShipDock> getAirShipDocks()
	{
		return _list;
	}

	public GArray<AirShipDock> getAirShipDocksForTeleports(int currentDockNpcId)
	{
		GArray<AirShipDock> docks = new GArray<AirShipDock>();
		for(AirShipDock ad : _list)
			if(ad.isTeleportPoint())
			{
				AirShipDock modifiedAD = ad.clone();
				// Вылет из текущего дока бесплатный
				if(modifiedAD.getAirshipNpcId() == currentDockNpcId)
					modifiedAD.setFuel(0);
				docks.add(modifiedAD);
			}
		return docks;
	}

	/**
	 * Возвращает док по его id
	 */
	public AirShipDock getAirShipDock(int id)
	{
		for(AirShipDock ad : _list)
			if(ad.getId() == id)
				return ad;
		return null;
	}

	/**
	 * Возвращает док по npcId контроллера
	 */
	public AirShipDock getAirShipDockByNpcId(int npcId)
	{
		for(AirShipDock ad : _list)
			if(ad.getAirshipNpcId() == npcId)
				return ad;
		return null;
	}

	public class AirShipDock
	{
		private int _id;
		private Location _loc;
		private int _fuel;
		private boolean _isTeleportPoint;
		private int _airshipNpcId;
		private int _arrivalTrajetId;
		private int _departureTrajetId;
		private int _departureMovieId;
		/** Точка высадки пассажиров **/
		private Location _upsetLoc;

		public AirShipDock(int id, Location loc, int fuel, int airshipNpcId)
		{
			_id = id;
			_loc = loc;
			_fuel = fuel;
			_airshipNpcId = airshipNpcId;
		}

		public int getId()
		{
			return _id;
		}

		public Location getLoc()
		{
			return _loc;
		}

		public int getFuel()
		{
			return _fuel;
		}

		public void setFuel(int fuel)
		{
			_fuel = fuel;
		}

		public int getAirshipNpcId()
		{
			return _airshipNpcId;
		}

		public void setIsTeleportPoint(boolean val)
		{
			_isTeleportPoint = val;
		}

		public boolean isTeleportPoint()
		{
			return _isTeleportPoint;
		}

		public void setArrivalTrajetId(int trajetId)
		{
			_arrivalTrajetId = trajetId;
		}

		public int getArrivalTrajetId()
		{
			return _arrivalTrajetId;
		}

		public void setDepartureTrajetId(int trajetId)
		{
			_departureTrajetId = trajetId;
		}

		public int getDepartureTrajetId()
		{
			return _departureTrajetId;
		}

		public void setDepartureMovieId(int movieId)
		{
			_departureMovieId = movieId;
		}

		public int getDepartureMovieId()
		{
			return _departureMovieId;
		}

		public void setUpsetLoc(Location loc)
		{
			_upsetLoc = loc;
		}

		public Location getUpsetLoc()
		{
			return _upsetLoc;
		}

		@Override
		public AirShipDock clone()
		{
			AirShipDock ad = new AirShipDock(_id, _loc, _fuel, _airshipNpcId);
			ad.setIsTeleportPoint(_isTeleportPoint);
			ad.setArrivalTrajetId(_arrivalTrajetId);
			ad.setDepartureTrajetId(_departureTrajetId);
			ad.setDepartureMovieId(_departureMovieId);
			ad.setUpsetLoc(_upsetLoc);
			return ad;
		}
	}
}