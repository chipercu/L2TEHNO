package l2open.gameserver.serverpackets;

/**
 * @author : Ragnarok
 * @date : 19.12.10    15:40
 */
public class ExChangeNpcState extends L2GameServerPacket {
    public static final int NO_STATE = 0;//Ничего нет
    public static final int STATE1 = 1;//Красное пламя для npc 18928
    public static final int STATE2 = 2;//Зеленое пламя
    public static final int STATE3 = 3;//Синее пламя

    int objId;
    int state;

    public ExChangeNpcState(int id, int _state) {
        objId = id;
        state = _state;
    }


    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeH(getClient().isLindvior() ? 0xBF : 0xBE);
        writeD(objId);
        writeD(state);
    }
}
