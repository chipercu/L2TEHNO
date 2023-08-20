package l2open.util;

import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2World;

import java.io.Serializable;

/**
int currX = 0;//Координата X точки А
int currY = 0;//Координата Y точки А

int targetX = 15;//Координата X точки B
int targetY = 15;//Координата Y точки B

long timeNow = Time.TimeNowInMs;
long timeNeedToCalculate = timeNow + 3 * 1000
int speed = 2; //Точек в секунду.

double complete = (timeNeedToCalculate  - timeNow) * (speed * 1000);//Расстояние, которое пройдет объект за  3 секунды.

double tgA = (targetY-currY)/(targetX-currX);
double atan = Math.atan(tgA);

int deltaX = (int) (complete * Math.cos(atan));//если deltaX зависит от косинуса
int deltaY = (int) (complete * Math.sin(atan));//а deltaY зависит от синуса 
//То от чего будет зависить дельта Z?

int newPlayerX = currX + deltaX;
int newPlayerY = currY + deltaY;
//-------------------------------------
		long timeNow = System.currentTimeMillis();
		double complete = getSpeed(actor, isWalk) * (timeNow - moveModel.getPrevUpdateTime());
		double divider = complete / moveModel.getEndMovePoint().getDistance3(actor.getLocation());
		moveModel.setPrevUpdateTime(timeNow);
		
		if(divider >= 1)
		{
			if(!this.moveModel.compareAndSet(moveModel, null))
				return;
			
			actor.set(moveModel.getEndMovePoint());
			completeMove();
			return;
		}
			
		int dx = moveModel.getEndMovePoint().getX() - actor.getX();
		int dy = moveModel.getEndMovePoint().getY() - actor.getY();
		int dz = moveModel.getEndMovePoint().getZ() - actor.getZ();
		
		int x = actor.getX() + (int)Math.round(divider * dx);
		int y = actor.getY() + (int)Math.round(divider * dy);
		int z = actor.getZ() + (int)Math.round(divider * dz);
		
		actor.setXYZ(x, y, z);
//-------------------------------------
private Location setupLongWay(Location start, Location dest)
{
	if(actor.isType(L2PcInstance.class))
	{
		originalEndPoint = dest;
	}
	return normalizeByDistance(dest, MovingConfig.MaxDistance);
}

...

	private boolean recalculateMove()
	{
		double distance2 = getDestiny().getDistance2(actor.getLocation());
		if(distance2 > MathUtil.DiscreteSize && distance2 < BL_DISTANCE)
		{
			Location originalEndPoint = this.originalEndPoint;
			if(originalEndPoint != null)
			{ //путь был обрезан из-за длины. продолжаем движение
				move(originalEndPoint);
				return true;
			}
		}
		
		return false;
	}
**/
@SuppressWarnings("serial")
public class Location implements Serializable
{
	public int x, y, z, h = 0;
	public int id = 0;
	public int type = 0;

	public Location()
	{
		x = 0;
		y = 0;
		z = 0;
		h = 0;
	}

	/**
	 * Позиция (x, y, z, heading, npcId)
	 */
	public Location(int locX, int locY, int locZ, int heading, int npcId)
	{
		x = locX;
		y = locY;
		z = locZ;
		h = heading;
		id = npcId;
	}

	/**
	 * Позиция (x, y, z, heading)
	 */
	public Location(int locX, int locY, int locZ, int heading)
	{
		x = locX;
		y = locY;
		z = locZ;
		h = heading;
	}

	/**
	 * Позиция (x, y, z)
	 */
	public Location(int locX, int locY, int locZ)
	{
		x = locX;
		y = locY;
		z = locZ;
		h = 0;
	}

	public Location(int locX, int locY, int locZ, boolean geo2world)
	{
		if(geo2world)
		{
			x = (locX << 4) + L2World.MAP_MIN_X + 8;
			y = (locY << 4) + L2World.MAP_MIN_Y + 8;
		}
		else
		{
			x = locX;
			y = locY;
		}
		z = locZ;
		h = 0;
		
	}

	public Location(L2Object obj)
	{
		x = obj.getX();
		y = obj.getY();
		z = obj.getZ();
		h = obj.getHeading();
	}

	public Location(int[] point)
	{
		x = point[0];
		y = point[1];
		z = point[2];
		try
		{
			h = point[3];
		}
		catch(Exception e)
		{
			h = 0;
		}
	}

	/**
	 * Парсит Location из строки, где коордтнаты разделены пробелами или запятыми
	 */
	public Location(String s) throws IllegalArgumentException
	{
		String[] xyzh = s.replaceAll(",", " ").replaceAll(";", " ").replaceAll("  ", " ").trim().split(" ");
		if(xyzh.length < 3)
			throw new IllegalArgumentException("Can't parse location from string: " + s);
		x = Integer.parseInt(xyzh[0]);
		y = Integer.parseInt(xyzh[1]);
		z = Integer.parseInt(xyzh[2]);
		h = xyzh.length < 4 ? 0 : Integer.parseInt(xyzh[3]);
	}

	public boolean equals(Location loc)
	{
		return loc.x == x && loc.y == y && loc.z == z;
	}

	public boolean equals(int _x, int _y, int _z)
	{
		return _x == x && _y == y && _z == z;
	}

	public boolean equals(int _x, int _y, int _z, int _h)
	{
		return _x == x && _y == y && _z == z && h == _h;
	}

	public Location changeZ(int zDiff)
	{
		z += zDiff;
		return this;
	}

	public Location correctGeoZ()
	{
		z = GeoEngine.getHeight(x, y, z, 0);
		return this;
	}

	public Location correctGeoZ(int refIndex)
	{
		z = GeoEngine.getHeight(x, y, z, refIndex);
		return this;
	}

	public Location setX(int _x)
	{
		x = _x;
		return this;
	}

	public Location setY(int _y)
	{
		y = _y;
		return this;
	}

	public Location setZ(int _z)
	{
		z = _z;
		return this;
	}

	public Location setH(int _h)
	{
		h = _h;
		return this;
	}

	public Location setId(int _id)
	{
		id = _id;
		return this;
	}

	public void set(int _x, int _y, int _z)
	{
		x = _x;
		y = _y;
		z = _z;
	}

	public void set(int _x, int _y, int _z, int _h)
	{
		x = _x;
		y = _y;
		z = _z;
		h = _h;
	}

	public void set(Location loc)
	{
		x = loc.x;
		y = loc.y;
		z = loc.z;
		h = loc.h;
	}

	public Location rnd(int min, int max, boolean change)
	{
		Location loc = Rnd.coordsRandomize(this, min, max);
		loc = GeoEngine.moveCheck(x, y, z, loc.x, loc.y, 0);
		if(change)
		{
			x = loc.x;
			y = loc.y;
			z = loc.z;
			return this;
		}
		return loc;
	}

	public static Location getAroundPosition(L2Object obj, L2Object obj2, int radius_min, int radius_max, int max_geo_checks)
	{
		Location pos = new Location(obj);
		if(radius_min < 0)
			radius_min = 0;
		if(radius_max < 0)
			radius_max = 0;

		float col_radius = obj.getColRadius() + obj2.getColRadius();
		int randomRadius, randomAngle, x, y, z;
		int min_angle = 0;
		int max_angle = 360;
		if(!obj.equals(obj2))
		{
			double perfect_angle = Util.calculateAngleFrom(obj, obj2);
			min_angle = (int) perfect_angle - 225;
			min_angle = (int) perfect_angle + 135;
		}

		while(true)
		{
			randomRadius = Rnd.get(radius_min, radius_max);
			randomAngle = Rnd.get(min_angle, max_angle);
			x = pos.x + (int) ((col_radius + randomRadius) * Math.cos(randomAngle));
			y = pos.y + (int) ((col_radius + randomRadius) * Math.sin(randomAngle));
			z = pos.z;
			if(max_geo_checks <= 0)
				break;
			z = GeoEngine.getHeight(x, y, z, obj.getReflection().getGeoIndex());
			if(Math.abs(pos.z - z) < 256 && GeoEngine.getNSWE(x, y, z, obj.getReflection().getGeoIndex()) == 15)
				break;
			max_geo_checks--;
		}

		pos.x = x;
		pos.y = y;
		pos.z = z;
		return pos;
	}

	public Location world2geo()
	{
		x = x - L2World.MAP_MIN_X >> 4;
		y = y - L2World.MAP_MIN_Y >> 4;
		return this;
	}

	public Location geo2world()
	{
		// размер одного блока 16*16 точек, +8*+8 это его средина
		x = (x << 4) + L2World.MAP_MIN_X + 8;
		y = (y << 4) + L2World.MAP_MIN_Y + 8;
		return this;
	}

	public double distance(Location loc)
	{
		return distance(loc.x, loc.y);
	}

	public double distance(int _x, int _y)
	{
		long dx = x - _x;
		long dy = y - _y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double distance3D(Location loc)
	{
		return distance3D(loc.x, loc.y, loc.z);
	}

	public double distance3D(int _x, int _y, int _z)
	{
		long dx = x - _x;
		long dy = y - _y;
		long dz = z - _z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@Override
	public Location clone()
	{
		return new Location(x, y, z, h, id);
	}

	@Override
	public final String toString()
	{
		return x + "," + y + "," + z + "," + h;
	}

	public boolean isNull()
	{
		return x == 0 || y == 0 || z == 0;
	}

	public final String toXYZString()
	{
		return x + "," + y + "," + z;
	}
	
	public static Location findNearest(L2Character creature, Location[] locs)
	{
		Location defloc = null;
		for(Location loc : locs)
		{
			if(defloc == null)
				defloc = loc;
			else if(creature.getDistance(loc) < creature.getDistance(defloc))
				defloc = loc;
		}
		return defloc;
	}

	public static Location findPointToStay(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex)
	{
		for(int i = 0; i < radiusmax; ++i)
		{
			if(radiusmin > i)
				radiusmin = radiusmin - i;
			else
				radiusmin = 0;

			Location pos = coordsRandomize(x, y, z, 0, radiusmin, radiusmax-i);
			int tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
			if(Math.abs(pos.z - tempz) >= 200 || GeoEngine.getNSWE(pos.x, pos.y, tempz, geoIndex) != 15)
				continue;
			pos.z = tempz;
			return pos;
		}

		return new Location(x, y, z);
	}

	public static Location findPointToStay(Location loc, int radius, int geoIndex)
	{
		return findPointToStay(loc.x, loc.y, loc.z, 0, radius, geoIndex);
	}

	public static Location findPointToStay(Location loc, int radiusmin, int radiusmax, int geoIndex)
	{
		return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
	}

	public static Location findPointToStay(L2Object obj, Location loc, int radiusmin, int radiusmax)
	{
		return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getReflection().getGeoIndex());
	}

	public static Location findPointToStay(L2Object obj, int radiusmin, int radiusmax)
	{
		return findPointToStay(obj, obj.getLoc(), radiusmin, radiusmax);
	}

	public static Location findPointToStay(L2Object obj, int radius)
	{
		return findPointToStay(obj, 0, radius);
	}

	public static Location coordsRandomize(Location loc, int radiusmin, int radiusmax)
	{
		return coordsRandomize(loc.x, loc.y, loc.z, loc.h, radiusmin, radiusmax);
	}

	public static Location coordsRandomize(int x, int y, int z, int heading, int radiusmin, int radiusmax)
	{
		if(radiusmax == 0 || radiusmax < radiusmin)
			return new Location(x, y, z, heading);
		int radius = Rnd.get(radiusmin, radiusmax);
		double angle = Rnd.nextDouble() * 2.0 * 3.141592653589793;
		return new Location((int)(x + radius * Math.cos(angle)), (int)(y + radius * Math.sin(angle)), z, heading);
	}

	public static double calculateAngleFrom(L2Object obj1, L2Object obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	public static double calculateAngleFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if(angleTarget < 0)
			angleTarget = 360 + angleTarget;
		return angleTarget;
	}

	public Location correctW()
	{
		//if(L2World.isWater(x, y, z))
		//	z+=8;
		return this;
	}

	public Location correctNworld(Location to)
	{
        x = (x << 4) + L2World.MAP_MIN_X + 8;
        y = (y << 4) + L2World.MAP_MIN_Y + 8;
        double radian = Math.toRadians(calculateAngleFrom(x, y, to.x, to.y));
        x += (int)(Math.cos(radian) * 8.0);
        y += (int)(Math.sin(radian) * 8.0);
        return this;
    }

    public Location correctNoffset(Location to, Location start, int offset)
	{
        int _y;
		x = (x << 4) + L2World.MAP_MIN_X + 8;
        y = (y << 4) + L2World.MAP_MIN_Y + 8;
        double set = Math.toRadians(calculateAngleFrom(start.x, start.y, to.x, to.y));
        double cut = start.distance(to) - (double)offset;
        int _x = start.x + (int)(Math.cos(set) * cut);
        double distance = distance(_x, _y = start.y + (int)(Math.sin(set) * cut));
        if (distance < 8.0)
		{
            x = _x;
            y = _y;
            return this;
        }
        double radius = Math.min(distance, 8.0);
        double radian = Math.toRadians(calculateAngleFrom(x, y, _x, _y));
        x += (int)(Math.cos(radian) * radius);
        y += y + (int)(Math.sin(radian) * radius);
        return this;
    }

    public Location correctNz(Location to)
	{
        x = to.x;
        y = to.y;
        return this;
    }

    public Location correctByPart(Location to, double doneSize)
	{
        if (to == null)
            return this;

        double part = doneSize % 1.0;
        x += (int)(part * (double)(to.x - x));
        y += (int)(part * (double)(to.y - y));
        z += (int)(part * (double)(to.z - z));
        return this;
    }
}