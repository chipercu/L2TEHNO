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

import java.util.logging.Logger;

public class RequestOustPledgeMember extends L2GameClientPacket
{
	//Format: cS
	static Logger _log = Logger.getLogger(RequestOustPledgeMember.class.getName());

	private String _target;

	@Override
	public void readImpl()
	{
		_target = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null || !((activeChar.getClanPrivileges() & L2Clan.CP_CL_DISMISS) == L2Clan.CP_CL_DISMISS) || activeChar.is_block)
			return;

		L2Clan clan = activeChar.getClan();
		L2ClanMember member = clan.getClanMember(_target);
		if(member == null)
		{
			activeChar.sendPacket(Msg.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
			return;
		}
		else if(member.isOnline() && member.getPlayer().isInCombat())
		{
			activeChar.sendPacket(Msg.A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT);
			return;
		}
		else if(member.isClanLeader())
		{
			activeChar.sendMessage("A clan leader may not be dismissed.");
			return;
		}
		else if(member.isOnline() && member.getPlayer().isTerritoryFlagEquipped())
		{
			activeChar.sendMessage("Нельзя изгнать чара с флагом в руках.");
			return;
		}

		boolean clanPenalty = member.getPledgeType() != L2Clan.SUBUNIT_ACADEMY;
		clan.removeClanMember(member.getObjectId());
		clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED).addString(_target), new PledgeShowMemberListDelete(_target));
		if(clanPenalty)
			clan.setExpelledMember();
		else if(ConfigValue.AcademicEnable)
		{
			l2open.gameserver.model.barahlo.academ2.Academicians academic = l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().getAcademicMap().get(member.getObjectId());
			if(academic != null)
				l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().delAcademic(academic, true);
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

		L2Player player = member.getPlayer();
		if(member.isOnline() || player != null && player.isInOfflineMode())
		{
			if(player.getPledgeType() == L2Clan.SUBUNIT_ACADEMY)
				player.setLvlJoinedAcademy(0);
			player.setClan(null);
			if(!player.isNoble())
				player.setTitle("");
			player.setLeaveClanCurTime();
			Log.add("EXPELLED: clan="+clan.getName()+" member="+player.getName(), "clan_info");

			player.broadcastUserInfo(true);
			player.broadcastRelationChanged();

			player.sendPacket(Msg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS, Msg.PledgeShowMemberListDeleteAll);
		}
	}
}