package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.PledgeShowMemberListAll;

public class RequestPledgeMemberList extends L2GameClientPacket
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
		L2Clan clan = activeChar.getClan();
		if(clan != null)
			activeChar.sendPacket(new PledgeShowMemberListAll(clan, activeChar));
	}
}