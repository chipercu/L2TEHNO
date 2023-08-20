package l2open.util.geometry;

public abstract interface Shape
{
	public abstract boolean isInside(int paramInt1, int paramInt2);

	public abstract boolean isInside(int paramInt1, int paramInt2, int paramInt3);

	public abstract int getXmax();

	public abstract int getXmin();

	public abstract int getYmax();

	public abstract int getYmin();

	public abstract int getZmax();

	public abstract int getZmin();
}