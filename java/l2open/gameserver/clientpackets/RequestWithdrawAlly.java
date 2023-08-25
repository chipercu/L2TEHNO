package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.util.Log;

/**
 * format: c
 */
public class RequestWithdrawAlly extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();
		if(clan == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.isClanLeader())
		{
			activeChar.sendPacket(Msg.ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE);
			return;
		}

		if(clan.getAlliance() == null)
		{
			activeChar.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
			return;
		}

		if(clan.equals(clan.getAlliance().getLeader()))
		{
			activeChar.sendPacket(Msg.ALLIANCE_LEADERS_CANNOT_WITHDRAW);
			return;
		}
		
		Log.add("CAME_OUT_ALLY: ally_name="+clan.getAlliance().getAllyName()+" clan="+activeChar.getClan().getName()+" char="+activeChar.getName(), "alli_info");

		clan.broadcastToOnlineMembers(Msg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE, Msg.A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION);
		L2Alliance alliance = clan.getAlliance();
		clan.setAllyId(0);
		clan.setLeavedAlly();
		alliance.broadcastAllyStatus(true);
		alliance.removeAllyMember(clan.getClanId());
	}
}