package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;

public class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket
{
	// format: (ch)Sd
	private int _powerGrade;
	private String _name;

	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
		_powerGrade = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		L2Clan clan = activeChar.getClan();
		if(clan == null)
			return;

		if((activeChar.getClanPrivileges() & L2Clan.CP_CL_MANAGE_RANKS) == L2Clan.CP_CL_MANAGE_RANKS)
		{
			L2ClanMember member = activeChar.getClan().getClanMember(_name);
			if(member != null)
			{
				if(clan.isAcademy(member.getPledgeType()))
				{
					activeChar.sendMessage("You cannot change academy member grade.");
					return;
				}
				if(_powerGrade > 5)
					member.setPowerGrade(clan.getAffiliationRank(member.getPledgeType()));
				else
					member.setPowerGrade(_powerGrade);
				if(member.isOnline())
					member.getPlayer().sendUserInfo(false);
			}
			else
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.NotBelongClan", activeChar));
		}
		else
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.HaveNotAuthority", activeChar));
	}
}