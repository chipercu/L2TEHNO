package l2open.gameserver.clientpackets;

public class RequestExBlockGameVote extends L2GameClientPacket
{
	private int unk, unk2;

	@Override
	public void runImpl()
	{
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