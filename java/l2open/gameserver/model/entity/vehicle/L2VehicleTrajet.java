package l2open.gameserver.model.entity.vehicle;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2WorldRegion;
import l2open.gameserver.serverpackets.ExShowTrace;
import l2open.util.GArray;
import l2open.util.Location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

public class L2VehicleTrajet
{
	private final L2Vehicle _boat;
	private Map<Integer, L2VehiclePoint> _path;
	public int _idWaypoint;
	public int _ticketId;
	public Location _return;
	public int _max;
	public String[] _msgs;

	public L2VehicleTrajet(L2Vehicle boat, int idWaypoint1, int idWTicket1, Location ret_loc, String[] msgs)
	{
		_boat = boat;
		_idWaypoint = idWaypoint1;
		_ticketId = idWTicket1;
		_return = ret_loc;
		_msgs = msgs;

		loadBoatPath();
	}

	private void parseLine(String line)
	{
		_path = new FastMap<Integer, L2VehiclePoint>().setShared(true);
		StringTokenizer st = new StringTokenizer(line, ";");
		st.nextToken(); // skip idWaypoint
		_max = Integer.parseInt(st.nextToken());
		for(int i = 0; i < _max; i++)
		{
			L2VehiclePoint bp = new L2VehiclePoint();
			bp.speed1 = Integer.parseInt(st.nextToken());
			bp.speed2 = Integer.parseInt(st.nextToken());
			bp.x = Integer.parseInt(st.nextToken());
			bp.y = Integer.parseInt(st.nextToken());
			bp.z = Integer.parseInt(st.nextToken());
			bp.teleport = Integer.parseInt(st.nextToken());
			if(i == 0 && _boat.isClanAirShip())
				((L2AirShip) _boat).setClanAirshipSpawnLoc(new Location(bp.x, bp.y, bp.z, bp.teleport));
			_path.put(i, bp);
		}
	}

	public void addPathPoint(L2VehiclePoint bp)
	{
		_path.put(_max, bp);
		_max++;
	}

	public void loadBoatPath()
	{
		LineNumberReader lnr = null;
		try
		{
			File doorData = new File(ConfigValue.DatapackRoot, "/data/csv/vehiclepath.csv");
			lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData)));
			String line = null;
			while((line = lnr.readLine()) != null)
			{
				if(line.trim().length() == 0 || !line.startsWith(_idWaypoint + ";"))
					continue;
				parseLine(line);
				return;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(lnr != null)
					lnr.close();
			}
			catch(Exception e1)
			{ /* ignore problems */}
		}
	}

	public void moveNext()
	{
		_boat.setIsArrived(false);

		if(_boat._runstate >= _max) // Приехали
		{
			_boat.setIsDocked();
			_boat.setIsArrived(true);
			if(!_boat.isClanAirShip())
				_boat._cycle = _boat._cycle == 1 ? 2 : 1;
			_boat.say(1);
			_boat.broadcastVehicleStart(0);
			_boat.broadcastVehicleCheckLocation();
			_boat.broadcastVehicleInfo();
			_boat.broadcastStopMove();
			if(!_boat.isClanAirShip())
			{
				_boat.oustPlayers(); // Выгоняем пассажиров, если требуется
				_boat._vehicleCaptainTask = ThreadPoolManager.getInstance().schedule(new L2VehicleCaptain(_boat, 3), 60000);
			}
			return;
		}

		// При свободном движении
		if(_boat._runstate < 0)
		{
			_boat.setIsArrived(true);
			return;
		}

		final L2VehiclePoint bp = _path.get(_boat._runstate);

		if(!_boat.isDocked())
		{
			L2WorldRegion region = L2World.getRegion(bp.x, bp.y, bp.z);
			if(region != null)
				for(L2Character cha : region.getCharactersList(new GArray<L2Character>(), _boat.getObjectId(), _boat.getReflection().getId()))
					if(cha instanceof L2Vehicle && cha != _boat)
					{
						L2Vehicle otherBoat = (L2Vehicle) cha;
						if(otherBoat.isDocked() && canConflict(_boat.getName(), otherBoat.getName()))
						{
							_boat.SayAndSound(_msgs[0], "The other ship is blocking the port, please wait...", null);
							_boat.broadcastVehicleStart(0);
							_boat.broadcastVehicleInfo();
							_boat.broadcastStopMove();
							ThreadPoolManager.getInstance().schedule(new ContinueMoving(), 30000);
							return;
						}
					}
		}

		_boat._speed1 = bp.speed1;
		_boat._speed2 = bp.speed2;

		if(_boat.isDocked())
			_boat.broadcastVehicleInfo();

		if(bp.teleport > 0 && _boat.isAirShip())
			_boat.teleportShip(bp.x, bp.y, bp.z, bp.teleport);
		else
			ThreadPoolManager.getInstance().execute(new l2open.common.RunnableImpl(){
				public void runImpl()
				{
					_boat.moveToLocation(bp.x, bp.y, bp.z, 0, false);
					/* для дебага
					for(L2Player player : _players)
						if(player != null && player.isGM() && player.getVehicle() == _boat)
						{
							ArrayList<Location> points = new ArrayList<Location>();
							points.add(_boat.getLoc());
							points.add(_boat.getDestination());
							player.sendPacket(Points2Trace(player, points, 50, 60000));
						}
					*/
				}
			});

		_boat._runstate++;
	}

	private boolean canConflict(String name1, String name2)
	{
		if((name1.equalsIgnoreCase("Ship_Gludin_Rune_Gludin") || name2.equalsIgnoreCase("Ship_Gludin_Rune_Gludin")) && (name1.equalsIgnoreCase("Ship_Rune_Gludin_Rune") || name2.equalsIgnoreCase("Ship_Rune_Gludin_Rune")))
			return true;
		if((name1.equalsIgnoreCase("Ship_Gludin_Rune_Gludin") || name2.equalsIgnoreCase("Ship_Gludin_Rune_Gludin")) && (name1.equalsIgnoreCase("Ship_TI_Gludin_TI") || name2.equalsIgnoreCase("Ship_TI_Gludin_TI")))
			return true;
		if((name1.equalsIgnoreCase("Ship_Rune_Primeval_Rune") || name2.equalsIgnoreCase("Ship_Rune_Primeval_Rune")) && (name1.equalsIgnoreCase("Ship_Primeval_Rune_Primeval") || name2.equalsIgnoreCase("Ship_Primeval_Rune_Primeval")))
			return true;
		if((name1.equalsIgnoreCase("Ship_TI_Gludin_TI") || name2.equalsIgnoreCase("Ship_TI_Gludin_TI")) && (name1.equalsIgnoreCase("Ship_Gludin_TI_Gludin") || name2.equalsIgnoreCase("Ship_Gludin_TI_Gludin")))
			return true;
		if((name1.equalsIgnoreCase("Ship_TI_Giran_TI") || name2.equalsIgnoreCase("Ship_TI_Giran_TI")) && (name1.equalsIgnoreCase("Ship_TI_Gludin_TI") || name2.equalsIgnoreCase("Ship_TI_Gludin_TI")))
			return true;
		if((name1.equalsIgnoreCase("Ship_TI_Giran_TI") || name2.equalsIgnoreCase("Ship_TI_Giran_TI")) && (name1.equalsIgnoreCase("Ship_Gludin_TI_Gludin") || name2.equalsIgnoreCase("Ship_Gludin_TI_Gludin")))
			return true;
		if(name1.equalsIgnoreCase("Ship_Innadril_Tour") && name2.equalsIgnoreCase("Ship_Innadril_Tour"))
			return true;
		return false;
	}

	private class ContinueMoving extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			moveNext();
		}
	}

	public static ExShowTrace Points2Trace(L2Player player, ArrayList<Location> points, int step, int time)
	{
		ExShowTrace result = new ExShowTrace(time);
		Location prev = null;
		int i = 0;
		for(Location p : points)
		{
			i++;
			if(player.isGM())
				player.sendMessage(p.toString());
			if(prev != null)
				result.addLine(prev, p, step);
			prev = p;
		}
		return result;
	}
}