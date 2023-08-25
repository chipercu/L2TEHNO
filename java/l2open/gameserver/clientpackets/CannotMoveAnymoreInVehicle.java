package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.StopMoveToLocationInVehicle;

// format: cddddd
public class CannotMoveAnymoreInVehicle extends L2GameClientPacket
{
	private int _boatid;

	private int _tx;
	private int _ty;
	private int _tz;
	private int _th;

	@Override
	public void readImpl()
	{
		_boatid = readD();
		_tx = readD();
		_ty = readD();
		_tz = readD();
		_th = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(activeChar.isInVehicle() && activeChar.getVehicle().getObjectId() == _boatid)
		{
			activeChar.setInVehiclePosition(_tx, _ty, _tz);
			activeChar.setHeading(_th);
			activeChar.broadcastPacket(new StopMoveToLocationInVehicle(activeChar, _boatid));
		}
	}
}