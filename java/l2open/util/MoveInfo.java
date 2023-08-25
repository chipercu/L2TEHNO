package l2open.util;

import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2World;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MoveInfo implements Serializable
{
	public int x, y, z, h = 0;

	/**
	 * Позиция (x, y, z, heading)
	 */
	public MoveInfo(int locX, int locY, int locZ, int heading)
	{
		x = locX;
		y = locY;
		z = locZ;
		h = heading;
	}

	/**
	 * Позиция (x, y, z)
	 */
	public MoveInfo(int locX, int locY, int locZ)
	{
		x = locX;
		y = locY;
		z = locZ;
		h = 0;
	}

	public MoveInfo(int locX, int locY, int locZ, boolean geo2world)
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

	public boolean equals(MoveInfo loc)
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

	public MoveInfo correctGeoZ()
	{
		z = GeoEngine.getHeight(x, y, z, 0);
		return this;
	}

	public MoveInfo geo2world()
	{
		// размер одного блока 16*16 точек, +8*+8 это его средина
		x = (x << 4) + L2World.MAP_MIN_X + 8;
		y = (y << 4) + L2World.MAP_MIN_Y + 8;
		return this;
	}

	public double distance(MoveInfo loc)
	{
		return distance(loc.x, loc.y);
	}

	public double distance(int _x, int _y)
	{
		long dx = x - _x;
		long dy = y - _y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double distance3D(MoveInfo loc)
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
}