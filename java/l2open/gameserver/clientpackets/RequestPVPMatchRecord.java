package l2open.gameserver.clientpackets;

public class RequestPVPMatchRecord extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
		_log.info("Unimplemented packet: " + getType() + " | size: " + _buf.remaining());
	}

	@Override
	public void runImpl()
	{}
}