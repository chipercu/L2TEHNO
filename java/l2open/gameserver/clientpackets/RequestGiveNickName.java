package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.util.Util;

import java.util.logging.Logger;

public class RequestGiveNickName extends L2GameClientPacket
{
	//Format: cSS
	static Logger _log = Logger.getLogger(RequestGiveNickName.class.getName());

	private String _target;
	private String _title;

	@Override
	public void readImpl()
	{
		_target = readS(ConfigValue.cNameMaxLen);
		_title = readS();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(!_title.isEmpty() && !Util.isMatchingRegexp(_title, ConfigValue.ClanTitleTemplate))
		{
			activeChar.sendMessage("Incorrect title.");
			return;
		}

		if(activeChar.getClan() != null && activeChar.getClan().getLevel() < 3 && !activeChar.isNoble())
		{
			activeChar.sendPacket(Msg.TITLE_ENDOWMENT_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3);
			return;
		}

		L2ClanMember member;
		if(activeChar.getClan() != null && (member = activeChar.getClan().getClanMember(_target)) != null && (activeChar.getClanPrivileges() & L2Clan.CP_CL_MANAGE_TITLES) == L2Clan.CP_CL_MANAGE_TITLES)
		{
			member.setTitle(_title);
			if(member.isOnline())
			{
				member.getPlayer().sendPacket(Msg.TITLE_HAS_CHANGED);
				member.getPlayer().sendChanges();
			}
		}
		else if(activeChar.isNoble() && _target.equals(activeChar.getName()))
		{
			activeChar.setTitle(_title, true);
			activeChar.sendPacket(Msg.TITLE_HAS_CHANGED);
			activeChar.sendChanges();
			return;
		}
		else
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestGiveNickName.NotInClan", activeChar));

	}
}