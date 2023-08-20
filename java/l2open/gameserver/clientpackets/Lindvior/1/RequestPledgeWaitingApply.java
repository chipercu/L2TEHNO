package l2open.gameserver.clientpackets;

import l2open.gameserver.model.*;
import l2open.gameserver.model.clan_find.*;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.ClanTable;

public class RequestPledgeWaitingApply extends L2GameClientPacket
{
	private int _karma;
	private int _clanId;
	private String _message;

	@Override
	protected void readImpl()
	{
		_karma = readD();
		_clanId = readD();
		_message = readS();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (activeChar.getClan() != null))
		{
			return;
		}
		
		final L2Clan clan = ClanTable.getInstance().getClan(_clanId);
		if (clan == null)
		{
			return;
		}
		
		final PledgeApplicantInfo info = new PledgeApplicantInfo(activeChar.getObjectId(), activeChar.getName(), activeChar.getLevel(), _karma, _clanId, _message);
		if (ClanEntryManager.getInstance().addPlayerApplicationToClan(_clanId, info))
		{
			sendPacket(new ExPledgeRecruitApplyInfo(ClanEntryStatus.WAITING));
			
			final L2Player clanLeader = L2ObjectsStorage.getPlayer(clan.getLeaderId());
			if (clanLeader != null)
			{
				clanLeader.sendPacket(new ExPledgeWaitingListAlarm());
			}
		}
		else
		{
			final SystemMessage sm = new SystemMessage(SystemMessage.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
			sm.addLong(ClanEntryManager.getInstance().getPlayerLockTime(activeChar.getObjectId()));
			sendPacket(sm);
		}
	}
}