package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Raidboss_statusTable.*;

@Table(
        name = "raidboss_status",
        fields = {
                @Field(name = ID,
                @Field(name = CURRENT_HP,
                @Field(name = CURRENT_MP,
                @Field(name = RESPAWN_DELAY,
        }
)
public class Raidboss_statusTable extends DataBaseTable<Raidboss_statusTable> {

    public static final String ID = "id";
    public static final String CURRENT_HP = "current_hp";
    public static final String CURRENT_MP = "current_mp";
    public static final String RESPAWN_DELAY = "respawn_delay";
    public Raidboss_statusTable() {
super(Raidboss_statusTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetCurrent_hp() {
        return get(CURRENT_HP, Integer.class);
    }
    public IntegergetCurrent_mp() {
        return get(CURRENT_MP, Integer.class);
    }
    public IntegergetRespawn_delay() {
        return get(RESPAWN_DELAY, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setCurrent_hp() {
        set(CURRENT_HP, value);
    }
    public void setCurrent_mp() {
        set(CURRENT_MP, value);
    }
    public void setRespawn_delay() {
        set(RESPAWN_DELAY, value);
    }

}
