package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.clan_find.*;
import l2open.gameserver.serverpackets.ExPledgeRecruitBoardDetail;

public class RequestPledgeRecruitBoardDetail extends L2GameClientPacket
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
		
		final PledgeRecruitInfo pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(_clanId);
		if (pledgeRecruitInfo == null)
			return;

		getClient().sendPacket(new ExPledgeRecruitBoardDetail(pledgeRecruitInfo));
	}
}