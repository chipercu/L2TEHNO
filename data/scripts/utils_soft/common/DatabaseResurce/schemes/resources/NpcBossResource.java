package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.NpcBossResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "npc_boss",
        primary_key = {NPC_DB_NAME},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 50 , name = NPC_DB_NAME , defValue = "null"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = ALIVE , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HP , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MP , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POS_X , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POS_Y , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POS_Z , defValue = "null"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = TIME_LOW , defValue = "null"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = TIME_HIGH , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = I0 , defValue = "null"),
        }
)
public class NpcBossResource extends DataBaseTable<NpcBossResource> {

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

    public NpcBossResource() {
        super(NpcBossResource.class);
}

    public String getNpcDbName() {
        return get(NPC_DB_NAME);
    }
    public Integer getAlive() {
        return get(ALIVE);
    }
    public Integer getHp() {
        return get(HP);
    }
    public Integer getMp() {
        return get(MP);
    }
    public Integer getPosX() {
        return get(POS_X);
    }
    public Integer getPosY() {
        return get(POS_Y);
    }
    public Integer getPosZ() {
        return get(POS_Z);
    }
    public Long getTimeLow() {
        return get(TIME_LOW);
    }
    public Long getTimeHigh() {
        return get(TIME_HIGH);
    }
    public Integer getI0() {
        return get(I0);
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
