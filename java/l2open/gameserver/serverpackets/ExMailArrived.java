package l2open.gameserver.serverpackets;

/**
 * Fromat: (ch)
 * (just a trigger)
 */
public class ExMailArrived extends L2GameServerPacket {
    private static String _S__FE_2E_EXMAILARRIVED = "[S] FE:2e ExMailArrived []";
    public static ExMailArrived STATIC_PACKET = new ExMailArrived();

    @Override
    protected final void writeImpl() {
        writeC(EXTENDED_PACKET);
        writeH(0x2e);
        writeD(0x01);
    }

    @Override
    public String getType() {
        return _S__FE_2E_EXMAILARRIVED;
    }
}