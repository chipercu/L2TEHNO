package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2VehicleManager;
import l2open.gameserver.serverpackets.ExMoveToLocationInAirShip;
import l2open.util.Location;

public class RequestMoveToLocationInAirShip extends L2GameClientPacket
{
	private Location _originPos = new Location();
	private int _boatId;

	private int _tx;
	private int _ty;
	private int _tz;
	private int _ox;
	private int _oy;
	private int _oz;

	/**
	 * format: ddddddd
	 */
	@Override
	public void readImpl()
	{
		_boatId = readD(); // objectId of boat
		_tx = readD();
		_ty = readD();
		_tz = readD();
		_ox = readD();
		_oy = readD();
		_oz = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getPet() != null)
		{
			activeChar.sendPacket(Msg.BECAUSE_PET_OR_SERVITOR_MAY_BE_DROWNED_WHILE_THE_BOAT_MOVES_PLEASE_RELEASE_THE_SUMMON_BEFORE_DEPARTURE, Msg.ActionFail);
			return;
		}

		if(activeChar.getTransformation() != 0)
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED, Msg.ActionFail);
			return;
		}

		if(activeChar.isMovementDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2AirShip boat = (L2AirShip) L2VehicleManager.getInstance().getBoat(_boatId);
		if(boat == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		// FIXME Возможно, стоит убрать
		if(!activeChar.isInVehicle() || activeChar.getVehicle() != boat)
			activeChar.setVehicle(boat);

		activeChar.setInVehiclePosition(_tx, _ty, _tz);
		activeChar.broadcastPacket(new ExMoveToLocationInAirShip(activeChar, boat, _ox, _oy, _oz, _tx, _ty, _tz));
	}
}