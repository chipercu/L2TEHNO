package l2open.gameserver.geodata;

import l2open.gameserver.model.L2Territory;

public abstract interface GeoCollision
{
	public abstract L2Territory getGeoPos();

	public abstract void setGeoPos(L2Territory value);

	public byte[][] getGeoAround();

	public void setGeoAround(byte[][] geo);

	// создаем или убираем преграду в геодате...
	public boolean isConcrete();
}
