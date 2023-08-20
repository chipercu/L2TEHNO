package l2open.gameserver.clientpackets;

public class RequestSiegeInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		_log.info(getType());
	}
}