package l2open.gameserver.clientpackets;

import l2open.gameserver.model.clan_find.*;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.player.PlayerData;

public class RequestPledgeWaitingUserAccept extends L2GameClientPacket
{
	private boolean _acceptRequest;
	private int _playerId;
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_acceptRequest = readD() == 1;
		_playerId = readD();
		_clanId = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (activeChar.getClan() == null))
		{
			return;
		}

		if (_acceptRequest)
		{
			final L2Player player = L2ObjectsStorage.getPlayer(_playerId);
			if (player != null && player.canJoinClan())
			{
				player.sendPacket(new JoinPledge(_clanId));

				L2Clan clan = activeChar.getClan();
				
				if(clan == null || !clan.canInvite())
				{
					activeChar.sendPacket(Msg.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
					return;
				}
				else if(clan.getSubPledgeMembersCount(0) >= clan.getSubPledgeLimit(0))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME).addString(clan.getName()));
					return;
				}
				else if(player.isInOlympiadMode() || player.getOlympiadGame() != null)
				{
					activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
					return;
				}
				else if(!activeChar.can_create_party || !player.can_create_party)
				{
					activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
					return;
				}

				player.setPledgeType(0);
				clan.addClanMember(player);
				player.setClan(clan);
				player.setVar("join_clan", String.valueOf(System.currentTimeMillis()));
				clan.getClanMember(player.getName()).setPlayerInstance(player, false);

				if(clan.isAcademy(player.getPledgeType()))
					player.setLvlJoinedAcademy(player.getLevel());

				clan.getClanMember(player.getName()).setPowerGrade(clan.getAffiliationRank(player.getPledgeType()));

				clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(clan.getClanMember(player.getName())), player);
				clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.S1_HAS_JOINED_THE_CLAN).addString(player.getName()), new PledgeShowInfoUpdate(clan));

				// this activates the clan tab on the new member
				player.sendPacket(Msg.ENTERED_THE_CLAN, new PledgeShowMemberListAll(clan, player));
				player.setLeaveClanTime(0);
				player.updatePledgeClass();
				clan.addAndShowSkillsToPlayer(player);
				player.broadcastUserInfo(true);
				player.broadcastRelationChanged();

				PlayerData.getInstance().store(player, false);

				ClanEntryManager.getInstance().removePlayerApplication(clan.getClanId(), _playerId);
			}
		}
		else
		{
			ClanEntryManager.getInstance().removePlayerApplication(activeChar.getClanId(), _playerId);
		}
	}
}