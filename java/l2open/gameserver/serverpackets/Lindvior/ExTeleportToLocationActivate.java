package l2open.gameserver.serverpackets;

public class ExTeleportToLocationActivate extends L2GameServerPacket
{
    @Override
    protected final void writeImpl()
	{
        writeEx(0x154);
    }
}
