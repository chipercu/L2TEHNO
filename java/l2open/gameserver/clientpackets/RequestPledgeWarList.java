package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.PledgeReceiveWarList;

public class RequestPledgeWarList extends L2GameClientPacket
{
	// format: (ch)dd
	static int _type;
	private int _page;

	@Override
	public void readImpl()
	{
		_page = readD();
		_type = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();
		if(clan != null)
			activeChar.sendPacket(new PledgeReceiveWarList(clan, _type, _page));
	}
}