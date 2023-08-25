package l2open.gameserver.model.entity.vehicle;

class L2VehicleArrived extends l2open.common.RunnableImpl
{
	private final L2Vehicle _vehicle;

	public L2VehicleArrived(L2Vehicle vehicle)
	{
		_vehicle = vehicle;
	}

	public void runImpl()
	{
		_vehicle.updatePeopleInTheBoat(_vehicle.getX(), _vehicle.getY(), _vehicle.getZ());
		_vehicle.VehicleArrived();
	}
}