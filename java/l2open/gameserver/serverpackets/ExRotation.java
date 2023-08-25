package l2open.gameserver.serverpackets;

/**
 * @author : Ragnarok
 * @date : 19.12.10    13:15
 */
public class ExRotation extends L2GameServerPacket{
    private int objId, heading;

    public ExRotation(int objId, int heading) {
        this.objId = objId;
        this.heading = heading;
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeHG(0xC1);
        writeD(objId);
        writeD(heading);
    }
}
