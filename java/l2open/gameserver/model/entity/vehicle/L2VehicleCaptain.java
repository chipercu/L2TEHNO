package l2open.gameserver.model.entity.vehicle;

import l2open.common.ThreadPoolManager;

class L2VehicleCaptain extends l2open.common.RunnableImpl
{
	private final L2Vehicle _vehicle;
	private int _state;

	public L2VehicleCaptain(L2Vehicle vehicle, int i)
	{
		_vehicle = vehicle;
		_state = i;
	}

	public void runImpl()
	{
		if(_vehicle.isClanAirShip() || _vehicle.getId() == 8 || _vehicle.getId() == 9)
			switch(_state)
			{
				case 1:
					_vehicle.say(-1);
					_vehicle.begin();
					break;
			}
		else
			switch(_state)
			{
				case 1:
					_vehicle.say(3);
					_vehicle._vehicleCaptainTask = ThreadPoolManager.getInstance().schedule(new L2VehicleCaptain(_vehicle, 2), 120000);
					break;
				case 2:
					_vehicle.say(1);
					_vehicle._vehicleCaptainTask = ThreadPoolManager.getInstance().schedule(new L2VehicleCaptain(_vehicle, 3), 40000);
					break;
				case 3:
					_vehicle.say(0);
					_vehicle._vehicleCaptainTask = ThreadPoolManager.getInstance().schedule(new L2VehicleCaptain(_vehicle, 4), 1000);
					break;
				case 4:
					_vehicle.say(-1);
					_vehicle.begin();
					break;
			}
	}
}