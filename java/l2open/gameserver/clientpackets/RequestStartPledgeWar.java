package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.ClanTable;

import java.util.logging.Logger;

public class RequestStartPledgeWar extends L2GameClientPacket
{
	//Format: cS
	private static Logger _log = Logger.getLogger(RequestStartPledgeWar.class.getName());

	String _pledgeName;
	L2Clan _clan;

	@Override
	public void readImpl()
	{
		_pledgeName = readS(32);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		_clan = activeChar.getClan();
		if(_clan == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!((activeChar.getClanPrivileges() & L2Clan.CP_CL_CLAN_WAR) == L2Clan.CP_CL_CLAN_WAR))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(_clan.getWarsCount() >= ConfigValue.ClanWarMaxCount)
		{
			activeChar.sendPacket(Msg.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME, Msg.ActionFail);
			return;
		}

		if(_clan.getLevel() < ConfigValue.ClanWarMinLevel || _clan.getMembersCount() < ConfigValue.ClanWarMinMember)
		{
			activeChar.sendPacket(Msg.A_CLAN_WAR_CAN_BE_DECLARED_ONLY_IF_THE_CLAN_IS_LEVEL_THREE_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER, Msg.ActionFail);
			return;
		}

		L2Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);
		if(clan == null)
		{
			activeChar.sendPacket(Msg.THE_DECLARATION_OF_WAR_CANT_BE_MADE_BECAUSE_THE_CLAN_DOES_NOT_EXIST_OR_ACT_FOR_A_LONG_PERIOD, Msg.ActionFail);
			return;
		}

		else if(_clan.equals(clan))
		{
			activeChar.sendPacket(Msg.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN, Msg.ActionFail);
			return;
		}

		else if(_clan.isAtWarWith(clan.getClanId()))
		{
			activeChar.sendPacket(Msg.THE_DECLARATION_OF_WAR_HAS_BEEN_ALREADY_MADE_TO_THE_CLAN, Msg.ActionFail);
			return;
		}

		else if(_clan.getAllyId() == clan.getAllyId() && _clan.getAllyId() != 0)
		{
			activeChar.sendPacket(Msg.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE, Msg.ActionFail);
			return;
		}

		else if(clan.getLevel() < ConfigValue.ClanWarMinLevel || clan.getMembersCount() < ConfigValue.ClanWarMinMember)
		{
			activeChar.sendPacket(Msg.A_CLAN_WAR_CAN_BE_DECLARED_ONLY_IF_THE_CLAN_IS_LEVEL_THREE_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER, Msg.ActionFail);
			return;
		}
		else if(activeChar.getClan().request_war_time > System.currentTimeMillis())
		{
			activeChar.sendMessage("Нельзя так часто давать приглашения на войну.");
			activeChar.sendPacket(Msg.ActionFail);
			return;
		}
		activeChar.getClan().request_war_time = System.currentTimeMillis() + ConfigValue.ClanRequestWarTime*1000L;
		//_log.info("RequestStartPledgeWar: By player: " + activeChar.getName() + " of clan: " + _clan.getName() + " to clan: " + _pledgeName);
		ClanTable.getInstance().startClanWar(activeChar.getClan(), clan);
	}
}