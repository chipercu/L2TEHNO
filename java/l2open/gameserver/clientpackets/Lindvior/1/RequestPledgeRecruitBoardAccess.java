package l2open.gameserver.clientpackets;

import l2open.gameserver.model.*;
import l2open.gameserver.model.clan_find.*;
import l2open.gameserver.serverpackets.*;

public class RequestPledgeRecruitBoardAccess extends L2GameClientPacket
{
	private int _applyType;
	private int _karma;
	private String _information;
	private String _datailedInformation;
	private int _applicationType=0;
	private int _recruitingType=0;

	@Override
	protected void readImpl()
	{
		_applyType = readD();
		_karma = readD();
		_information = readS();
		_datailedInformation = readS();
		//_applicationType = readD(); // 0 - Allow, 1 - Public
		//_recruitingType = readD(); // 0 - Main clan
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		
		if(activeChar == null)
			return;
		
		final L2Clan clan = activeChar.getClan();
		
		if (clan == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN));
			return;
		}

		if((activeChar.getClanPrivileges() & L2Clan.CP_CL_MANAGE_RANKS) != L2Clan.CP_CL_MANAGE_RANKS)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN));
			return;
		}
		final PledgeRecruitInfo pledgeRecruitInfo = new PledgeRecruitInfo(clan.getClanId(), _karma, _information, _datailedInformation, _applicationType, _recruitingType);
		
		switch (_applyType)
		{
			case 0: // remove
			{
				ClanEntryManager.getInstance().removeFromClanList(clan.getClanId());
				break;
			}
			case 1: // add
			{
				if (ClanEntryManager.getInstance().addToClanList(clan.getClanId(), pledgeRecruitInfo))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION_APPLICATION_IS_AUTOMATICALLY_CANCELLED_AFTER_30_DAYS_IF_YOU_CANCEL_APPLICATION_YOU_CANNOT_APPLY_AGAIN_FOR_5_MINUTES));
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessage.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
					sm.addLong(ClanEntryManager.getInstance().getClanLockTime(clan.getClanId()));
					activeChar.sendPacket(sm);
				}
				break;
			}
			case 2: // update
			{
				if (ClanEntryManager.getInstance().updateClanList(clan.getClanId(), pledgeRecruitInfo))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION_APPLICATION_IS_AUTOMATICALLY_CANCELLED_AFTER_30_DAYS_IF_YOU_CANCEL_APPLICATION_YOU_CANNOT_APPLY_AGAIN_FOR_5_MINUTES));
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessage.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
					sm.addLong(ClanEntryManager.getInstance().getClanLockTime(clan.getClanId()));
					activeChar.sendPacket(sm);
				}
				break;
			}
		}
	}
}