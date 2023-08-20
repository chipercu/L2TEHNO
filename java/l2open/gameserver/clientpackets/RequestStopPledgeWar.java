package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.ClanTable;

import java.util.logging.Logger;

public class RequestStopPledgeWar extends L2GameClientPacket
{
	//Format: cS
	private static Logger _log = Logger.getLogger(RequestStopPledgeWar.class.getName());

	String _pledgeName;

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

		L2Clan playerClan = activeChar.getClan();
		if(playerClan == null)
			return;

		if(!((activeChar.getClanPrivileges() & L2Clan.CP_CL_CLAN_WAR) == L2Clan.CP_CL_CLAN_WAR))
		{
			activeChar.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT, Msg.ActionFail);
			return;
		}

		L2Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);

		if(clan == null)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestStopPledgeWar.NoSuchClan", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if(!playerClan.isAtWarWith(clan.getClanId()))
		{
			activeChar.sendPacket(Msg.YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_TO_S1_CLAN, Msg.ActionFail);
			return;
		}

		for(L2ClanMember mbr : playerClan.getMembers())
			if(mbr.isOnline() && mbr.getPlayer().isInCombat())
			{
				activeChar.sendPacket(Msg.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE, Msg.ActionFail);
				return;
			}
		if(activeChar.getClan().getReputationScore() < ConfigValue.CancelWarClanRep)
		{
			activeChar.sendMessage(new CustomMessage("CancelWarClanRep", activeChar));
			return;
		}
		activeChar.getClan().incReputation(-ConfigValue.CancelWarClanRep, false, "RequestStopPledgeWar");
		//_log.info("RequestStopPledgeWar: By player: " + activeChar.getName() + " of clan: " + playerClan.getName() + " to clan: " + _pledgeName);

		ClanTable.getInstance().stopClanWar(playerClan, clan);
	}
}