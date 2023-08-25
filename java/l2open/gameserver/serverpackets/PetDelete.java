package l2open.gameserver.serverpackets;

public class PetDelete extends L2GameServerPacket {
    private int _objId;
    private int _petType;

    public PetDelete(int objId, int petType) {
        _objId = objId;
        _petType = petType;
    }


    @Override
    protected final void writeImpl() {
        writeC(0xb7);
        writeD(_petType);
        writeD(_objId);
    }
}