package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2VehicleManager;
import l2open.gameserver.serverpackets.ExGetOnAirShip;

public class RequestExGetOnAirShip extends L2GameClientPacket
{
	private int _shipId;
	
	private int _tx;
	private int _ty;
	private int _tz;

	@Override
	protected void readImpl()
	{
		_tx = readD();
		_ty = readD();
		_tz = readD();
		_shipId = readD();
	}

	@Override
	protected void runImpl()
	{
		//_log.info("[T1:ExGetOnAirShip] loc: " + _tx + " "+_ty+" "+_tz);
		//_log.info("[T1:ExGetOnAirShip] ship ID: " + _shipId);

		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		L2AirShip boat = (L2AirShip) L2VehicleManager.getInstance().getBoat(_shipId);
		if(boat == null)
			return;
		activeChar.stopMove();
		activeChar.setVehicle(boat);
		activeChar.setInVehiclePosition(_tx, _ty, _tz);
		activeChar.setLoc(boat.getLoc());
		activeChar.broadcastPacket(new ExGetOnAirShip(activeChar, boat, _tx, _ty, _tz));
	}
}