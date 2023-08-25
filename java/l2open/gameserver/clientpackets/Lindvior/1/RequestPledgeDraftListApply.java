package l2open.gameserver.clientpackets;

import l2open.gameserver.model.*;
import l2open.gameserver.model.clan_find.*;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestPledgeDraftListApply extends L2GameClientPacket
{
	private int _applyType;
	private int _karma;

	@Override
	protected void readImpl()
	{
		_applyType = readD();
		_karma = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		
		if ((activeChar == null) || (activeChar.getClan() != null))
		{
			return;
		}
		
		if (activeChar.getClan() != null)
		{
			sendPacket(new SystemMessage(SystemMessage.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN));
			return;
		}
		
		switch (_applyType)
		{
			case 0: // remove
			{
				if (ClanEntryManager.getInstance().removeFromWaitingList(activeChar.getObjectId()))
				{
					sendPacket(new SystemMessage(SystemMessage.ENTRY_APPLICATION_CANCELLED_YOU_MAY_APPLY_TO_A_NEW_CLAN_AFTER_5_MINUTES));
				}
				break;
			}
			case 1: // add
			{
				final PledgeWaitingInfo pledgeDraftList = new PledgeWaitingInfo(activeChar.getObjectId(), activeChar.getLevel(), _karma, activeChar.getClassId().getId(), activeChar.getName());
				if (ClanEntryManager.getInstance().addToWaitingList(activeChar.getObjectId(), pledgeDraftList))
				{
					sendPacket(new SystemMessage(SystemMessage.ENTERED_INTO_WAITING_LIST_NAME_IS_AUTOMATICALLY_DELETED_AFTER_30_DAYS_IF_DELETE_FROM_WAITING_LIST_IS_USED_YOU_CANNOT_ENTER_NAMES_INTO_THE_WAITING_LIST_FOR_5_MINUTES));
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessage.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
					sm.addLong(ClanEntryManager.getInstance().getPlayerLockTime(activeChar.getObjectId()));
					sendPacket(sm);
				}
				break;
			}
		}
	}
}