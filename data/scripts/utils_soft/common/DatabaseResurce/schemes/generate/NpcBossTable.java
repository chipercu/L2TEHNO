package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.NpcBossTable.*;

@Table(
        name = "npc_boss",
        fields = {
                @Field(name = NPC_DB_NAME),
                @Field(name = ALIVE),
                @Field(name = HP),
                @Field(name = MP),
                @Field(name = POS_X),
                @Field(name = POS_Y),
                @Field(name = POS_Z),
                @Field(name = TIME_LOW),
                @Field(name = TIME_HIGH),
                @Field(name = I0),
        }
)
public class NpcBossTable extends DataBaseTable<NpcBossTable> {

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

    public NpcBossTable() {
super(NpcBossTable.class);
}

    public String getNpcDbName() {
        return get(NPC_DB_NAME, String.class);
    }
    public Integer getAlive() {
        return get(ALIVE, Integer.class);
    }
    public Integer getHp() {
        return get(HP, Integer.class);
    }
    public Integer getMp() {
        return get(MP, Integer.class);
    }
    public Integer getPosX() {
        return get(POS_X, Integer.class);
    }
    public Integer getPosY() {
        return get(POS_Y, Integer.class);
    }
    public Integer getPosZ() {
        return get(POS_Z, Integer.class);
    }
    public Long getTimeLow() {
        return get(TIME_LOW, Long.class);
    }
    public Long getTimeHigh() {
        return get(TIME_HIGH, Long.class);
    }
    public Integer getI0() {
        return get(I0, Integer.class);
    }

    public void setNpcDbName(String value) {
        set(NPC_DB_NAME, value);
    }
    public void setAlive(Integer value) {
        set(ALIVE, value);
    }
    public void setHp(Integer value) {
        set(HP, value);
    }
    public void setMp(Integer value) {
        set(MP, value);
    }
    public void setPosX(Integer value) {
        set(POS_X, value);
    }
    public void setPosY(Integer value) {
        set(POS_Y, value);
    }
    public void setPosZ(Integer value) {
        set(POS_Z, value);
    }
    public void setTimeLow(Long value) {
        set(TIME_LOW, value);
    }
    public void setTimeHigh(Long value) {
        set(TIME_HIGH, value);
    }
    public void setI0(Integer value) {
        set(I0, value);
    }

}
