package l2open.gameserver.model.entity.vehicle;

public class L2VehiclePoint
{
	public int x, y, z, speed1, speed2, teleport;

	public String toXYZString()
	{
		return "(" + x + ", " + y + ", " + z + ", " + teleport + ")";
	}
}