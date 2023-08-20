package l2open.gameserver.clientpackets;

public class RequestSEKCustom extends L2GameClientPacket
{
	private int SlotNum, Direction;

	@Override
	public void runImpl()
	{
		_log.info(getType() + " :: SlotNum " + SlotNum + " :: Direction " + Direction);
	}

	/**
	 * format: dd
	 */
	@Override
	public void readImpl()
	{
		SlotNum = readD();
		Direction = readD();
	}
}