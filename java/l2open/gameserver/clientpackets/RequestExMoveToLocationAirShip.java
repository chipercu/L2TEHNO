package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2VehiclePoint;
import l2open.gameserver.tables.AirShipDocksTable;
import l2open.gameserver.tables.AirShipDocksTable.AirShipDock;

/**
 * Format: d d|dd
 */
public class RequestExMoveToLocationAirShip extends L2GameClientPacket
{
	@Override
	protected void runImpl()
	{}

	@Override
	protected void readImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getVehicle() == null || !activeChar.getVehicle().isAirShip())
			return;

		int MoveType = readD();
		L2AirShip airship = (L2AirShip) activeChar.getVehicle();
		if(airship.getDriver() == activeChar)
			switch(MoveType)
			{
				case 4: // AirShipTeleport
					int dockID = readD();
					int currentDockNpcId = airship.getCurrentDockNpcId();
					AirShipDock curAD = AirShipDocksTable.getInstance().getAirShipDockByNpcId(currentDockNpcId);
					if(curAD.getId() == dockID)
					{
						airship.SetTrajet1(curAD.getDepartureTrajetId(), 0, null, null);
						airship._cycle = 1;
						airship.begin();
					}
					else
					{
						airship.SetTrajet1(curAD.getDepartureTrajetId(), 0, null, null);

						L2VehiclePoint bp = new L2VehiclePoint();
						bp.speed1 = airship._speed1;
						bp.speed2 = airship._speed2;
						AirShipDock destAD = AirShipDocksTable.getInstance().getAirShipDock(dockID);
						bp.x = destAD.getLoc().x;
						bp.y = destAD.getLoc().y;
						bp.z = destAD.getLoc().z;
						bp.teleport = 1;

						if(airship.getFuel() < destAD.getFuel())
						{
							activeChar.sendMessage("Not enough EP."); // TODO правильное сообщение
							return;
						}

						airship.getTrajet1().addPathPoint(bp);
						airship._cycle = 1;
						airship.begin();
						airship.setFuel(airship.getFuel() - destAD.getFuel());
					}
					break;
				case 0: // Free move
					if(airship.isDocked() || !airship.isArrived())
						break;
					airship.moveToLocation(airship.getLoc().setX(readD()).setY(readD()), 0, false);
					break;
				case 2: // Up
					if(airship.isDocked() || !airship.isArrived())
						break;
					readD(); //?
					readD(); //?
					airship.moveToLocation(airship.getLoc().changeZ(100), 0, false);
					break;
				case 3: // Down
					if(airship.isDocked() || !airship.isArrived())
						break;
					readD(); //?
					readD(); //?
					airship.moveToLocation(airship.getLoc().changeZ(-100), 0, false);
					break;
			}
	}
}