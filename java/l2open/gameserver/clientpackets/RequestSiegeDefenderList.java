package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.serverpackets.SiegeDefenderList;

public class RequestSiegeDefenderList extends L2GameClientPacket
{
	private int _unitId;

	@Override
	public void readImpl()
	{
		_unitId = readD();
	}

	@Override
	public void runImpl()
	{
		Residence unit = CastleManager.getInstance().getCastleByIndex(_unitId);
		if(unit == null)
			unit = FortressManager.getInstance().getFortressByIndex(_unitId);
		if(unit == null)
			unit = ClanHallManager.getInstance().getClanHall(_unitId);
		if(unit != null)
			sendPacket(new SiegeDefenderList(unit));
	}
}