package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.clan_find.*;
import l2open.gameserver.serverpackets.*;

public class RequestPledgeWaitingUser extends L2GameClientPacket
{
	private int _clanId;
	private int _playerId;

	@Override
	protected void readImpl()
	{
		_clanId = readD();
		_playerId = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (activeChar.getClanId() != _clanId))
		{
			return;
		}
		
		final PledgeApplicantInfo infos = ClanEntryManager.getInstance().getPlayerApplication(_clanId, _playerId);
		if (infos == null)
		{
			sendPacket(new ExPledgeWaitingList(_clanId));
		}
		else
		{
			sendPacket(new ExPledgeWaitingUser(infos));
		}
	}
}