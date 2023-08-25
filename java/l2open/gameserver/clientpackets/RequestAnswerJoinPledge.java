package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.player.PlayerData;

public class RequestAnswerJoinPledge extends L2GameClientPacket
{
	//Format: cd
	private int _response;

	@Override
	public void readImpl()
	{
		_response = _buf.hasRemaining() ? readD() : 0;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Transaction transaction = activeChar.getTransaction();

		if(transaction == null)
			return;

		if(!transaction.isValid() || !transaction.isTypeOf(TransactionType.CLAN))
		{
			transaction.cancel();
			activeChar.sendPacket(Msg.TIME_EXPIRED, Msg.ActionFail);
			return;
		}

		L2Player requestor = transaction.getOtherPlayer(activeChar);

		transaction.cancel();

		if(requestor.getClan() == null)
			return;

		if(_response == 1)
		{
			if(activeChar.canJoinClan())
			{
				activeChar.sendPacket(new JoinPledge(requestor.getClanId()));

				L2Clan clan = requestor.getClan();

				clan.addClanMember(activeChar);
				activeChar.setClan(clan);
				activeChar.setVar("join_clan", String.valueOf(System.currentTimeMillis()));
				clan.getClanMember(activeChar.getName()).setPlayerInstance(activeChar, false);

				if(clan.isAcademy(activeChar.getPledgeType()))
					activeChar.setLvlJoinedAcademy(activeChar.getLevel());

				clan.getClanMember(activeChar.getName()).setPowerGrade(clan.getAffiliationRank(activeChar.getPledgeType()));

				clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(clan.getClanMember(activeChar.getName())), activeChar);
				clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.S1_HAS_JOINED_THE_CLAN).addString(activeChar.getName()), new PledgeShowInfoUpdate(clan));

				// this activates the clan tab on the new member
				activeChar.sendPacket(Msg.ENTERED_THE_CLAN, new PledgeShowMemberListAll(clan, activeChar));
				activeChar.setLeaveClanTime(0);
				activeChar.updatePledgeClass();
				clan.addAndShowSkillsToPlayer(activeChar);
				activeChar.broadcastUserInfo(true);
				activeChar.broadcastRelationChanged();

				PlayerData.getInstance().store(activeChar, false);
			}
			else
			{
				requestor.sendPacket(Msg.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
				activeChar.sendPacket(Msg.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
				activeChar.setPledgeType(0);
			}
		}
		else
		{
			requestor.sendPacket(new SystemMessage(SystemMessage.S1_REFUSED_TO_JOIN_THE_CLAN).addString(activeChar.getName()));
			activeChar.setPledgeType(0);
		}
	}
}