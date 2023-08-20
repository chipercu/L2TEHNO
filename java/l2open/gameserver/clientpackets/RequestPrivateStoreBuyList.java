package l2open.gameserver.clientpackets;

public class RequestPrivateStoreBuyList extends L2GameClientPacket
{
	private int unk;

	@Override
	public void runImpl()
	{
		_log.info(getType() + " :: " + unk);
	}

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		unk = readD();
	}
}