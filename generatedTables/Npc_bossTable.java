package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Npc_bossTable.*;

@Table(
        name = "npc_boss",
        fields = {
                @Field(name = NPC_DB_NAME,
                @Field(name = ALIVE,
                @Field(name = HP,
                @Field(name = MP,
                @Field(name = POS_X,
                @Field(name = POS_Y,
                @Field(name = POS_Z,
                @Field(name = TIME_LOW,
                @Field(name = TIME_HIGH,
                @Field(name = I0,
        }
)
public class Npc_bossTable extends DataBaseTable<Npc_bossTable> {

    public static final String NPC_DB_NAME = "npc_db_name";
    public static final String ALIVE = "alive";
    public static final String HP = "hp";
    public static final String MP = "mp";
    public static final String POS_X = "pos_x";
    public static final String POS_Y = "pos_y";
    public static final String POS_Z = "pos_z";
    public static final String TIME_LOW = "time_low";
    public static final String TIME_HIGH = "time_high";
    public static final String I0 = "i0";
    public Npc_bossTable() {
super(Npc_bossTable.class);
}
    public StringgetNpc_db_name() {
        return get(NPC_DB_NAME, String.class);
    }
    public IntegergetAlive() {
        return get(ALIVE, Integer.class);
    }
    public IntegergetHp() {
        return get(HP, Integer.class);
    }
    public IntegergetMp() {
        return get(MP, Integer.class);
    }
    public IntegergetPos_x() {
        return get(POS_X, Integer.class);
    }
    public IntegergetPos_y() {
        return get(POS_Y, Integer.class);
    }
    public IntegergetPos_z() {
        return get(POS_Z, Integer.class);
    }
    public ObjectgetTime_low() {
        return get(TIME_LOW, Object.class);
    }
    public ObjectgetTime_high() {
        return get(TIME_HIGH, Object.class);
    }
    public IntegergetI0() {
        return get(I0, Integer.class);
    }

    public void setNpc_db_name() {
        set(NPC_DB_NAME, value);
    }
    public void setAlive() {
        set(ALIVE, value);
    }
    public void setHp() {
        set(HP, value);
    }
    public void setMp() {
        set(MP, value);
    }
    public void setPos_x() {
        set(POS_X, value);
    }
    public void setPos_y() {
        set(POS_Y, value);
    }
    public void setPos_z() {
        set(POS_Z, value);
    }
    public void setTime_low() {
        set(TIME_LOW, value);
    }
    public void setTime_high() {
        set(TIME_HIGH, value);
    }
    public void setI0() {
        set(I0, value);
    }

}
