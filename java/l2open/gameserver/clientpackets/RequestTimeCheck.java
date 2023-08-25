package l2open.gameserver.clientpackets;

public class RequestTimeCheck extends L2GameClientPacket
{
	private int unk, unk2;

	@Override
	public void runImpl()
	{
		_log.info(getType() + " :: " + unk + " :: " + unk2 + " :: ");
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