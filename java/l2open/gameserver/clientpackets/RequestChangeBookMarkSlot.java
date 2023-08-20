package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestChangeBookMarkSlot extends L2GameClientPacket
{
	private int slot_old, slot_new;

	@Override
	public void readImpl()
	{
		slot_old = readD();
		slot_new = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		_log.info(getType() + "@" + activeChar + "::" + slot_old + "::" + slot_new);
	}
}