package l2open.gameserver.clientpackets;

import l2open.gameserver.model.clan_find.ClanEntryStatus;
import l2open.gameserver.model.clan_find.ClanEntryManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExPledgeRecruitApplyInfo;

public class RequestPledgeRecruitApplyInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		ClanEntryStatus status = ClanEntryStatus.DEFAULT;

		if(activeChar.getClan() != null && activeChar.isClanLeader() && ClanEntryManager.getInstance().isClanRegistred(activeChar.getClanId()))
			status = ClanEntryStatus.ORDERED;
		else if(activeChar.getClan() == null && ClanEntryManager.getInstance().isPlayerRegistred(activeChar.getObjectId()))
			status = ClanEntryStatus.WAITING;

		activeChar.sendPacket(new ExPledgeRecruitApplyInfo(status));
	}
}