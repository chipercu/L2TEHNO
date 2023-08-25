package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.serverpackets.AskJoinAlliance;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestJoinAlly extends L2GameClientPacket
{
	// format: cd

	private int _id;

	@Override
	public void readImpl()
	{
		_id = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getClan() == null || activeChar.getAlliance() == null)
			return;

		if(activeChar.getAlliance().getMembersCount() >= ConfigValue.AltMaxAllySize)
		{
			activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
			return;
		}

		L2Player target = L2ObjectsStorage.getPlayer(_id);
		if(target == null)
		{
			activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_ONLINE);
			return;
		}
		if(target.getClan() == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		else if(!activeChar.can_create_party || !target.can_create_party)
		{
			activeChar.sendActionFailed();
			return;
		}
		if(!activeChar.isAllyLeader())
		{
			activeChar.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
			return;
		}
		if(target.getAlliance() != null || activeChar.getAlliance().isMember(target.getClan().getClanId()))
		{
			//same or another alliance - no need to invite
			SystemMessage sm = new SystemMessage(SystemMessage.S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE);
			sm.addString(target.getClan().getName());
			sm.addString(target.getAlliance().getAllyName());
			activeChar.sendPacket(sm);
			return;
		}
		if(!target.isClanLeader())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addString(target.getName()));
			return;
		}
		if(activeChar.isAtWarWith(target.getClanId()) > 0)
		{
			activeChar.sendPacket(Msg.YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_AT_BATTLE_WITH);
			return;
		}
		if(!target.getClan().canJoinAlly())
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_IT_LEFT_ANOTHER_ALLIANCE);
			sm.addString(target.getClan().getName());
			activeChar.sendPacket(sm);
			return;
		}
		if(!activeChar.getAlliance().canInvite())
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestJoinAlly.InvitePenalty", activeChar));
		if(activeChar.isInTransaction())
		{
			activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		if(target.isInTransaction())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
			return;
		}
		new Transaction(TransactionType.ALLY, activeChar, target, 10000);
		//leader of alliance request an alliance.
		SystemMessage sm = new SystemMessage(SystemMessage.S2_THE_LEADER_OF_S1_HAS_REQUESTED_AN_ALLIANCE);
		sm.addString(activeChar.getAlliance().getAllyName());
		sm.addString(activeChar.getName());
		target.sendPacket(sm, new AskJoinAlliance(activeChar.getObjectId(), activeChar.getName(), activeChar.getAlliance().getAllyName()));
		return;
	}
}