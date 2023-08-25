package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExGetBookMarkInfo;

/**
 * dSdS
 */
public class RequestModifyBookMarkSlot extends L2GameClientPacket
{
	private String name, acronym;
	private int icon, slot;

	@Override
	public void readImpl()
	{
		slot = readD();
		name = readS(32);
		icon = readD();
		acronym = readS(4);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar != null && !activeChar.is_block)
		{
			activeChar.bookmarks.get(slot).setName(name).setIcon(icon).setAcronym(acronym);
			activeChar.sendPacket(new ExGetBookMarkInfo(activeChar));
		}
	}
}