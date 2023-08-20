package l2open.gameserver.clientpackets;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.util.Log;

public class RequestOustAlly extends L2GameClientPacket
{
	private String _clanName;

	@Override
	public void readImpl()
	{
		_clanName = readS(32);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		L2Clan leaderClan = activeChar.getClan();
		if(leaderClan == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		L2Alliance alliance = leaderClan.getAlliance();
		if(alliance == null)
		{
			activeChar.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
			return;
		}

		L2Clan clan;

		if(!activeChar.isAllyLeader())
		{
			activeChar.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
			return;
		}

		if(_clanName == null)
			return;

		clan = ClanTable.getInstance().getClanByName(_clanName);

		if(clan != null)
		{
			if(!alliance.isMember(clan.getClanId()))
			{
				activeChar.sendActionFailed();
				return;
			}

			if(alliance.getLeader().equals(clan))
			{
				activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE);
				return;
			}
			
			Log.add("EXPELLED_ALLY: ally_name="+clan.getAlliance().getAllyName()+" clan="+clan.getName()+" char="+activeChar.getName(), "alli_info");

			clan.broadcastToOnlineMembers(new SystemMessage("Your clan has been expelled from " + alliance.getAllyName() + " alliance."), new SystemMessage(SystemMessage.A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION));
			clan.setAllyId(0);
			clan.setLeavedAlly();
			alliance.broadcastAllyStatus(true);
			alliance.removeAllyMember(clan.getClanId());
			alliance.setExpelledMember();
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestOustAlly.ClanDismissed", activeChar).addString(clan.getName()).addString(alliance.getAllyName()));
		}
	}
}