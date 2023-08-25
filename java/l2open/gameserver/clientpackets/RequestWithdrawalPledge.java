package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.barahlo.academ.*;
import l2open.gameserver.model.barahlo.academ.dao.AcademiciansDAO;
import l2open.gameserver.serverpackets.PledgeShowMemberListDelete;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.util.Log;

public class RequestWithdrawalPledge extends L2GameClientPacket
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
		if(System.currentTimeMillis() - activeChar.getLastRequestWithdrawalPledgePacket() < ConfigValue.RequestWithdrawalPledgePacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestWithdrawalPledgePacket();

		//is the guy in a clan  ?
		if(activeChar.getClanId() == 0 || activeChar.is_block)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInCombat())
		{
			activeChar.sendPacket(Msg.ONE_CANNOT_LEAVE_ONES_CLAN_DURING_COMBAT);
			return;
		}

		L2Clan clan = activeChar.getClan();
		if(clan == null)
			return;

		L2ClanMember member = clan.getClanMember(activeChar.getObjectId());
		if(member == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		else if(member.isClanLeader())
		{
			activeChar.sendMessage("A clan leader may not be dismissed.");
			return;
		}
		else if(activeChar.isTerritoryFlagEquipped())
		{
			activeChar.sendMessage("Нельзя выйти с клана пока флаг в руках.");
			return;
		}

		// this also updated the database
		clan.removeClanMember(activeChar.getObjectId());

		clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.S1_HAS_WITHDRAWN_FROM_THE_CLAN).addString(activeChar.getName()), new PledgeShowMemberListDelete(activeChar.getName()));

		if(activeChar.getPledgeType() == L2Clan.SUBUNIT_ACADEMY)
		{
			activeChar.setLvlJoinedAcademy(0);
			if(ConfigValue.AcademicEnable)
			{
				l2open.gameserver.model.barahlo.academ2.Academicians academic = l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().getAcademicMap().get(member.getObjectId());
				if(academic != null)
					l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().delAcademic(academic, false);
			}
			else if(ConfigValue.RecruitmentAllow)
			{
				Academicians academic = AcademiciansStorage.getInstance().get(member.getObjectId());

				if(academic != null)
				{
					AcademyRequest academy = AcademyStorage.getInstance().getReguest(academic.getClanId());
					AcademiciansDAO.getInstance().delete(academic);
					AcademiciansStorage.getInstance().get().remove(academic);
					AcademyStorage.getInstance().updateList();
					academy.updateSeats();
				}
				else
					_log.info("RequestWithdrawalPledge: Academicians ERROR 1.");
			}
		}
		activeChar.setClan(null);
		if(!activeChar.isNoble())
			activeChar.setTitle("");

		Log.add("CAME_OUT: clan="+clan.getName()+" member="+activeChar.getName(), "clan_info");

		activeChar.setLeaveClanCurTime();
		activeChar.broadcastUserInfo(true);
		activeChar.broadcastRelationChanged();

		activeChar.sendPacket(Msg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS, Msg.PledgeShowMemberListDeleteAll);
	}
}