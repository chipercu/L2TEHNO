package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.serverpackets.SSQStatus;

/**
 * Seven Signs Record Update Request
 * packet type id 0xc8
 * format: cc
 */
public class RequestSSQStatus extends L2GameClientPacket
{
	private int _page;

	@Override
	public void readImpl()
	{
		_page = readC();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if((SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) && _page == 4)
			return;

		sendPacket(new SSQStatus(activeChar, _page));
	}
}