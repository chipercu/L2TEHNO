package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.RankPrivs;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.PledgePowerGradeList;

public class RequestPledgePowerGradeList extends L2GameClientPacket
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
		{
			RankPrivs[] privs = clan.getAllRankPrivs();
			activeChar.sendPacket(new PledgePowerGradeList(privs));
		}
	}
}