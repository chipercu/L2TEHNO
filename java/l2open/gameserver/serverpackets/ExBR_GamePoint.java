package l2open.gameserver.serverpackets;

public class ExBR_GamePoint extends L2GameServerPacket
{
    int objectId;
    int points;
    public ExBR_GamePoint(int objectId, int points)
	{
        this.objectId = objectId;
        this.points = points;
    }

    @Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeHG(0xD5);
        writeD(objectId);
        writeQ(points);
        writeD(0x00);
	}
}