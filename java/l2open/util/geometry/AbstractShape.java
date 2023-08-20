package l2open.util.geometry;

public abstract class AbstractShape implements Shape
{
	protected final Point3D max;
	protected final Point3D min;

	public AbstractShape()
	{
		max = new Point3D();
		min = new Point3D();
	}

	public boolean isInside(int x, int y, int z)
	{
		return (min.z <= z) && (max.z >= z) && (isInside(x, y));
	}

	public int getXmax()
	{
		return max.x;
	}

	public int getXmin()
	{
		return min.x;
	}

	public int getYmax()
	{
		return max.y;
	}

	public int getYmin()
	{
		return min.y;
	}

	public AbstractShape setZmax(int z)
	{
		max.z = z;
		return this;
	}

	public AbstractShape setZmin(int z)
	{
		min.z = z;
		return this;
	}

	public int getZmax()
	{
		return max.z;
	}

	public int getZmin()
	{
		return min.z;
	}
}