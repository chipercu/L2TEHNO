package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.serverpackets.ExPledgeRecruitInfo;
import l2open.gameserver.tables.ClanTable;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitInfo extends L2GameClientPacket
{
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_clanId = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		final L2Clan clan = ClanTable.getInstance().getClan(_clanId);
		if(clan == null)
			return;

		getClient().sendPacket(new ExPledgeRecruitInfo(_clanId));
	}
}