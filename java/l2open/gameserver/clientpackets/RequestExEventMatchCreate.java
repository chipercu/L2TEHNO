package l2open.gameserver.clientpackets;

import l2open.gameserver.serverpackets.ExEventMatchObserver;

public class RequestExEventMatchCreate extends L2GameClientPacket
{
	private int unk, unk2;

	@Override
	public void runImpl()
	{
		_log.info(getType() + " :: " + unk + " :: " + unk2);
		//getClient().sendPacket(new ExEventMatchObserver(unk, unk2, 0, "", "", 0, 0, 0));
	}

	/**
	 * format: dd
	 */
	@Override
	public void readImpl()
	{
		unk = readD();
		unk2 = readD();
	}
}