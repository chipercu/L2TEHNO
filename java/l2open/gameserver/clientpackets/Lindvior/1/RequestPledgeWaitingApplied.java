package l2open.gameserver.clientpackets;

import l2open.gameserver.model.clan_find.ClanEntryManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExPledgeWaitingListApplied;

public class RequestPledgeWaitingApplied extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getClan() != null)
			return;
		
		int clanId = ClanEntryManager.getInstance().getClanIdForPlayerApplication(activeChar.getObjectId());
		if(clanId > 0)
			activeChar.sendPacket(new ExPledgeWaitingListApplied(clanId, activeChar.getObjectId()));
	}
}