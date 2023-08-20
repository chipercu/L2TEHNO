package l2open.gameserver.serverpackets;

/**
 * @author : Ragnarok
 * @date : 19.12.10    13:19
 */
public class ExAskModifyPartyLooting extends L2GameServerPacket {
    String name;
    int mode;

    public ExAskModifyPartyLooting(String name, int mode) {
        this.name = name;
        this.mode = mode;
    }

    @Override
    protected void writeImpl()
	{
        writeC(0xFE);
        writeHG(0xBF);
        writeS(name);
        writeD(mode);
    }
}
