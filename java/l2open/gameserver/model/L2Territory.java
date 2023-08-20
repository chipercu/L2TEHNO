package l2open.gameserver.model;

import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.geometry.Point3D; 
import l2open.util.geometry.Shape; 

import java.awt.*;
import java.util.ArrayList; 
import java.util.List; 
import java.util.logging.Logger;

import java.awt.geom.Line2D;

public class L2Territory
{
	private static Logger _log = Logger.getLogger(L2Territory.class.getName());

	static class Point
	{
		protected int x, y, zmin, zmax;

		Point(int _x, int _y, int _zmin, int _zmax)
		{
			x = _x;
			y = _y;
			zmin = _zmin;
			zmax = _zmax;
		}
	}

	protected L2Zone _zone;
	private Polygon poly;
	private Point[] _points;
	protected int _x_min;
	protected int _x_max;
	protected int _y_min;
	protected int _y_max;
	protected int _z_min;
	protected int _z_max;

	private int _id;

	public L2Territory(int id)
	{
		poly = new Polygon();
		_points = new Point[0];
		_x_min = 999999;
		_x_max = -999999;
		_y_min = 999999;
		_y_max = -999999;
		_z_min = 999999;
		_z_max = -999999;
		_id = id;
	}

	public void add(Location loc)
	{
		add(loc.x, loc.y, loc.z, loc.h);
	}

	public void add(int x, int y, int zmin, int zmax)
	{
		if(zmax == -1)
		{
			zmin = zmin - 50;
			zmax = zmin + 100;
		}
		Point[] newPoints = new Point[_points.length + 1];
		System.arraycopy(_points, 0, newPoints, 0, _points.length);
		newPoints[_points.length] = new Point(x, y, zmin, zmax);
		_points = newPoints;

		poly.addPoint(x, y);

		if(x < _x_min)
			_x_min = x;
		if(y < _y_min)
			_y_min = y;
		if(x > _x_max)
			_x_max = x;
		if(y > _y_max)
			_y_max = y;
		if(zmin < _z_min)
			_z_min = zmin;
		if(zmax > _z_max)
			_z_max = zmax;
	}

	public L2Territory addR(int x, int y, int zmin, int zmax)
	{
		if(zmax == -1)
		{
			zmin = zmin - 50;
			zmax = zmin + 100;
		}
		Point[] newPoints = new Point[_points.length + 1];
		System.arraycopy(_points, 0, newPoints, 0, _points.length);
		newPoints[_points.length] = new Point(x, y, zmin, zmax);
		_points = newPoints;

		poly.addPoint(x, y);

		if(x < _x_min)
			_x_min = x;
		if(y < _y_min)
			_y_min = y;
		if(x > _x_max)
			_x_max = x;
		if(y > _y_max)
			_y_max = y;
		if(zmin < _z_min)
			_z_min = zmin;
		if(zmax > _z_max)
			_z_max = zmax;
		return this;
	}

	/**
	 * Проверяет территорию на самопересечение.
	 */
	public void validate()
	{
		// треугольник не может быть самопересекающимся
		if(_points.length > 3)
			// внешний цикл - перебираем все грани многоугольника
			for(int i = 1; i < _points.length; i++)
			{
				int ii = i + 1 < _points.length ? i + 1 : 0; // вторая точка первой линии
				// внутренний цикл - перебираем все грани многоугольниках кроме той, что во внешнем цикле и соседних 
				for(int n = i; n < _points.length; n++)
					if(Math.abs(n - i) > 1)
					{
						int nn = n + 1 < _points.length ? n + 1 : 0; // вторая точка второй линии
						//if(Line2D.linesIntersect(_points[i].x, _points[i].y, _points[ii].x, _points[ii].y, _points[n].x, _points[n].y, _points[nn].x, _points[nn].y))
						//	_log.warning(this + "["+_zone+"] is self-intersecting in lines " + i + "-" + ii + " and " + n + "-" + nn); // Клиентам это не нужно
					}
			}
	}

	public void print()
	{
		for(Point p : _points)
			System.out.println("(" + p.x + "," + p.y + ")");
	}

	public boolean isInside(int x, int y)
	{
		return poly.contains(x, y);
	}

	public boolean isInside(int x, int y, int z)
	{
	//	if(_zone != null && _zone.getId() == 2132)
	//		_log.info("Zone("+_zone.getName()+") isInside("+(z >= _z_min && z <= _z_max && poly.contains(x, y))+"): x="+x+" y="+y+" z("+_z_min+":"+_z_max+")="+z);
		return z >= _z_min && z <= _z_max && poly.contains(x, y);
	}

	public boolean isInside(Location loc)
	{
		return isInside(loc.x, loc.y, loc.z);
	}

	public boolean isInside(L2Object obj)
	{
		return obj.getZ() >= _z_min && obj.getZ() <= _z_max && poly.contains(obj.getX(), obj.getY()) && (_zone != null ? (_zone.reflection == -1 || _zone.reflection == obj.getReflectionId() || _zone.reflection == 111 && obj.getReflectionId() > 0) : true);
	}

	public boolean isNoInside(L2Territory[] territory, int x, int y)
	{
		for(L2Territory terr: territory)
			if(terr.isInside(x, y))
				return false;
		return true;
	}

	public int[] getRandomPoint()
	{
		return getRandomPoint(null);
	}

	public int[] getRandomPoint(L2Territory[] baned)
	{
		int i;
		int[] p = new int[3];

		mainloop: for(i = 0; i < 100; i++)
		{
			p[0] = Rnd.get(_x_min, _x_max);
			p[1] = Rnd.get(_y_min, _y_max);
			p[2] = _z_min + (_z_max - _z_min) / 2;

			/** Для отлова проблемных территорий, вызывающих сильную нагрузку
			if(i == 40)
				_log.warning("Heavy territory: " + this + ", need manual correction"); Клиентам это не нужно*/

			if((baned == null || isNoInside(baned, p[0], p[1])) && poly.contains(p[0], p[1]))
			{
				// Не спаунить в зоны, запрещенные для спауна
				if(ZoneManager.getInstance().checkIfInZone(ZoneType.no_spawn, p[0], p[1], 0))
					continue;

				// Не спаунить в колонны, стены и прочее.
				int tempz = GeoEngine.getHeight(p[0], p[1], p[2], 0);
				if(_z_min != _z_max)
				{
					if(tempz < _z_min || tempz > _z_max || _z_min > _z_max)
						continue;
				}
				else if(tempz < _z_min - 200 || tempz > _z_min + 200)
					continue;

				p[2] = tempz;

				int geoX = p[0] - L2World.MAP_MIN_X >> 4;
				int geoY = p[1] - L2World.MAP_MIN_Y >> 4;

				// Если местность подозрительная - пропускаем
				for(int x = geoX - 1; x <= geoX + 1; x++)
					for(int y = geoY - 1; y <= geoY + 1; y++)
						if(GeoEngine.NgetNSWE(x, y, p[2], 0) != GeoEngine.NSWE_ALL)
							continue mainloop;

				return p;
			}
		}
		//_log.warning("Can't make point for " + this); Клиентам это не нужно.
		return p;
	}

	public void doEnter(L2Object object)
	{
		//if(object.isPlayer())
		//	_log.info("L2Territory: doEnter["+object.getName()+"]["+_zone+"]["+_zone.instance_only+"]["+(_zone.reflection == object.getReflectionId())+"("+_zone.reflection+"|"+object.getReflectionId()+")]");
		if(_zone != null && (!_zone.instance_only || _zone.reflection == object.getReflectionId() || _zone.reflection == 111 && object.getReflectionId() > 0))
		{
			if(object.isPlayable())
				_zone.doEnter(object);
			else if(_zone.getZoneTarget() == L2Zone.ZoneTarget.npc && object.isNpc())
				_zone.doEnter(object);
		}
	}

	public void doLeave(L2Object object, boolean notify)
	{
		//if(object.isPlayer())
		//	_log.info("L2Territory: doLeave["+object.getName()+"]["+_zone+"]["+_zone.instance_only+"]["+(_zone.reflection == object.getReflectionId())+"("+_zone.reflection+"|"+object.getReflectionId()+")]");
		if(_zone != null /*&& (!_zone.instance_only || _zone.reflection == object.getReflectionId() || _zone.reflection == 111 && obj.getReflectionId() > 0)*/)
		{
			if(object.isPlayable())
				_zone.doLeave(object, notify);
			else if(_zone.getZoneTarget() == L2Zone.ZoneTarget.npc && object.isNpc())
				_zone.doLeave(object, notify);
		}
	}

	public final int getId()
	{
		return _id;
	}

	@Override
	public final String toString()
	{
		return "territory '" + _id + "'";
	}

	public int getZmin()
	{
		return _z_min;
	}

	public int getZmax()
	{
		return _z_max;
	}

	public int getXmax()
	{
		return _x_max;
	}

	public int getXmin()
	{
		return _x_min;
	}

	public int getYmax()
	{
		return _y_max;
	}

	public int getYmin()
	{
		return _y_min;
	}

	public void setZone(L2Zone zone)
	{
		_zone = zone;
	}

	public L2Zone getZone()
	{
		return _zone;
	}

	public GArray<int[]> getCoords()
	{
		GArray<int[]> result = new GArray<int[]>();
		for(Point point : _points)
			result.add(new int[] { point.x, point.y, point.zmin, point.zmax });
		return result;
	}

	public Location getCenter()
	{
		return new Location(_x_min + (_x_max - _x_min) / 2, _y_min + (_y_max - _y_min) / 2, _z_min + (_z_max - _z_min) / 2);
	}

	/**
	 * Проверяет валидность территории.
	 * Если у нее нет зоны, или она является спавном - мы ее в L2World не добавляем.
	 * @return нужно ли добавить территорию в L2World
	 */
	public boolean isWorldTerritory()
	{
		return getZone() != null && getZone().getLoc() == this;
	}

	public int getTReflectionId()
	{
		if(getZone() == null)
			return -1;
		else if(_zone.instance_only && getZone().reflection == -1)
			return 100;
		return getZone().reflection;
	}

	public boolean equalsa(L2Territory terr)
	{
		if(getCoords().size() != terr.getCoords().size())
			return false;
		
		for(int i=0;i<getCoords().size();i++)
			if(getCoords().get(i)[0] == terr.getCoords().get(i)[0] && getCoords().get(i)[1] == terr.getCoords().get(i)[1] && getCoords().get(i)[2] == terr.getCoords().get(i)[2] && getCoords().get(i)[3] == terr.getCoords().get(i)[3])
				return true;
		return false;
	}
}