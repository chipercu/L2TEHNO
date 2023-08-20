package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.serverpackets.SiegeDefenderList;
import l2open.gameserver.tables.ClanTable;

public class RequestConfirmSiegeWaitingList extends L2GameClientPacket
{
	// format: cddd

	private int _Approved;
	private int _UnitId;
	private int _ClanId;

	@Override
	public void readImpl()
	{
		_UnitId = readD();
		_ClanId = readD();
		_Approved = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		// Check if the player has a clan
		if(activeChar.getClan() == null)
			return;

		Residence unit = CastleManager.getInstance().getCastleByIndex(_UnitId);
		if(unit == null)
			unit = FortressManager.getInstance().getFortressByIndex(_UnitId);

		if(unit == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		// Check if leader of the clan who owns the castle?
		if(unit.getOwnerId() != activeChar.getClanId() || !activeChar.isClanLeader())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2Clan clan = ClanTable.getInstance().getClan(_ClanId);
		if(clan == null)
			return;

		if(!unit.getSiege().isRegistrationOver())
			if(_Approved == 1)
			{
				if(unit.getSiege().checkIsDefenderWaiting(clan) || unit.getSiege().checkIsDefenderRefused(clan))
					unit.getSiege().approveSiegeDefenderClan(_ClanId);
				else
					return;
			}
			else if(unit.getSiege().checkIsDefenderWaiting(clan) || unit.getSiege().checkIsDefender(clan))
				unit.getSiege().refuseSiegeDefenderClan(_ClanId);

		// Update the defender list
		activeChar.sendPacket(new SiegeDefenderList(unit));
	}
}