package l2open.gameserver.clientpackets;

import l2open.gameserver.serverpackets.SendPingStatus;

import java.util.logging.Logger;

public final class RequestPingStatus extends L2GameClientPacket
{
	static Logger _log = Logger.getLogger(RequestPingStatus.class.getName());

	private long ping;

	@Override
	public void runImpl()
	{
		long time = System.currentTimeMillis();
		getClient().close(new SendPingStatus(time, ping));
	}

	@Override
	public void readImpl()
	{
		ping = readQ();
	}
}