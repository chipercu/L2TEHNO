package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.FakePlayersTable;

public final class SendPingStatus extends L2GameServerPacket
{
	private long _time;
	private long _ping_time;
	private long _ping;
	private long _add_time;

    public SendPingStatus(long time, long ping_time)
	{
		_time = time;
		_ping_time = ping_time;
		_ping = ping_time-time;
		_add_time = System.currentTimeMillis();
    }

    @Override
    protected void writeImpl()
	{
        writeC(0x2E);
        writeQ(_time);
        writeQ(_ping_time);
        writeQ(_ping);
        writeQ(_add_time);
        writeQ(System.currentTimeMillis());
		
    }
}