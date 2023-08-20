package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.serverpackets.*;

public class RequestSetCastleSiegeTime extends L2GameClientPacket
{
	private int _id;
	private int _time;

	@Override
	public void readImpl()
	{
		_id = readD();
		_time = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Castle castle = CastleManager.getInstance().getCastleByIndex(_id);
		if(castle == null)
			return;

		if(player.getClan().getHasCastle() != castle.getId())
			return;

		if((player.getClanPrivileges() & L2Clan.CP_CS_MANAGE_SIEGE) != L2Clan.CP_CS_MANAGE_SIEGE)
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME));
			return;
		}

		castle.getSiege().setNextSiegeTime(_time);

		player.sendPacket(new SiegeInfo(castle));
	}
}