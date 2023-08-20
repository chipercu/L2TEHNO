package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.PledgeShowMemberListUpdate;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestPledgeReorganizeMember extends L2GameClientPacket
{
	// format: (ch)dSdS
	int _replace;
	String _subjectName;
	int _targetUnit;
	String _replaceName;

	@Override
	public void readImpl()
	{
		_replace = readD();
		_subjectName = readS(ConfigValue.cNameMaxLen);
		_targetUnit = readD();
		if(_replace > 0)
			_replaceName = readS();
	}

	@Override
	public void runImpl()
	{
		//_log.warning("Received RequestPledgeReorganizeMember("+_arg1+","+_arg2+","+_arg3+","+_arg4+") from player "+getClient().getActiveChar().getName());

		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();
		if(clan == null || activeChar.is_block)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.isClanLeader())
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.ChangeAffiliations", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		L2ClanMember subject = clan.getClanMember(_subjectName);
		if(subject == null)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.NotInYourClan", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if(subject.getPledgeType() == _targetUnit)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.AlreadyInThatCombatUnit", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if(_targetUnit != 0 && clan.getSubPledge(_targetUnit) == null)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.NoSuchCombatUnit", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if(clan.isAcademy(_targetUnit))
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.AcademyViaInvitation", activeChar));
			activeChar.sendActionFailed();
			return;
		}
		/*
		 * unsure for next check, but anyway as workaround before academy refactoring
		 * (needs LvlJoinedAcademy to be put on L2ClanMember if so, to be able relocate from academy correctly)
		 */
		if(clan.isAcademy(subject.getPledgeType()))
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.CantMoveAcademyMember", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		L2ClanMember replacement = null;

		if(_replace > 0)
		{
			replacement = clan.getClanMember(_replaceName);
			if(replacement == null)
			{
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongClan", activeChar));
				activeChar.sendActionFailed();
				return;
			}
			if(replacement.getPledgeType() != _targetUnit)
			{
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongCombatUnit", activeChar));
				activeChar.sendActionFailed();
				return;
			}
			if(replacement.isSubLeader() != 0)
			{
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterLeaderAnotherCombatUnit", activeChar));
				activeChar.sendActionFailed();
				return;
			}
		}
		else
		{
			if(clan.getSubPledgeMembersCount(_targetUnit) >= clan.getSubPledgeLimit(_targetUnit))
			{
				if(_targetUnit == 0)
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME).addString(clan.getName()));
				else
					activeChar.sendPacket(Msg.THE_ACADEMY_ROYAL_GUARD_ORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
				activeChar.sendActionFailed();
				return;
			}
			if(subject.isSubLeader() != 0)
			{
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeReorganizeMember.MemberLeaderAnotherUnit", activeChar));
				activeChar.sendActionFailed();
				return;
			}

		}

		if(replacement != null)
		{
			replacement.setPledgeType(subject.getPledgeType());
			if(replacement.getPowerGrade() > 5)
				replacement.setPowerGrade(clan.getAffiliationRank(replacement.getPledgeType()));
			clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(replacement));
			if(replacement.isOnline())
			{
				replacement.getPlayer().updatePledgeClass();
				replacement.getPlayer().broadcastUserInfo(true);
			}
		}

		subject.setPledgeType(_targetUnit);
		if(subject.getPowerGrade() > 5)
			subject.setPowerGrade(clan.getAffiliationRank(subject.getPledgeType()));
		clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(subject));
		if(subject.isOnline())
		{
			subject.getPlayer().updatePledgeClass();
			subject.getPlayer().broadcastUserInfo(true);
		}
	}
}