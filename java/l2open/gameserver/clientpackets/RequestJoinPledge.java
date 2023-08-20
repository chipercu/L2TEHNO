package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.serverpackets.AskJoinPledge;
import l2open.gameserver.serverpackets.SystemMessage;

import java.util.logging.Logger;

public class RequestJoinPledge extends L2GameClientPacket
{
	//Format: cdd
	static Logger _log = Logger.getLogger(RequestJoinPledge.class.getName());

	private int _target;
	private int _pledgeType;

	@Override
	public void readImpl()
	{
		_target = readD();
		_pledgeType = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();

		if(clan == null || !clan.canInvite())
		{
			activeChar.sendPacket(Msg.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
			return;
		}
		else if(activeChar.isInTransaction())
		{
			activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		else if(_target == activeChar.getObjectId())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
			return;
		}
		//is the activeChar have privilege to invite players
		else if((activeChar.getClanPrivileges() & L2Clan.CP_CL_INVITE_CLAN) != L2Clan.CP_CL_INVITE_CLAN)
		{
			activeChar.sendPacket(Msg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}

		L2Object object = activeChar.getVisibleObject(_target);
		if(object == null || !object.isPlayer())
			return;
		L2Player member = (L2Player) object;

		if(!activeChar.getPlayerAccess().CanJoinClan)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_HE_SHE_LEFT_ANOTHER_CLAN).addString(member.getName()));
			member.sendPacket(Msg.FAILED_TO_JOIN_THE_CLAN);
			return;
		}
		else if(!member.getPlayerAccess().CanJoinClan)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_HE_SHE_LEFT_ANOTHER_CLAN).addString(member.getName()));
			member.sendPacket(Msg.FAILED_TO_JOIN_THE_CLAN);
			return;
		}
		else if(member.getClanId() != 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_WORKING_WITH_ANOTHER_CLAN).addString(member.getName()));
			return;
		}
		else if(member.isInTransaction())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(member.getName()));
			return;
		}
		else if(_pledgeType == -1 && (member.getLevel() > 40 || member.getClassId().getLevel() > 2))
		{
			activeChar.sendPacket(Msg.TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER);
			return;
		}
		else if(clan.getSubPledgeMembersCount(_pledgeType) >= clan.getSubPledgeLimit(_pledgeType))
		{
			if(_pledgeType == 0)
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME).addString(clan.getName()));
			else
				activeChar.sendPacket(Msg.THE_ACADEMY_ROYAL_GUARD_ORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
			return;
		}
		else if(member.isInOlympiadMode() || member.getOlympiadGame() != null)
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return;
		}
		else if(!activeChar.can_create_party || !member.can_create_party)
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return;
		}
		new Transaction(TransactionType.CLAN, activeChar, member, 10000);
		member.setPledgeType(_pledgeType);

		member.sendPacket(new AskJoinPledge(activeChar.getObjectId(), activeChar.getClan().getName()));
	}
}