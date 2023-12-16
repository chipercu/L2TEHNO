package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Epic_boss_spawnTable.*;

@Table(
        name = "epic_boss_spawn",
        fields = {
                @Field(name = BOSSID,
                @Field(name = RESPAWNDATE,
                @Field(name = STATE,
        }
)
public class Epic_boss_spawnTable extends DataBaseTable<Epic_boss_spawnTable> {

    public static final String BOSSID = "bossId";
    public static final String RESPAWNDATE = "respawnDate";
    public static final String STATE = "state";
    public Epic_boss_spawnTable() {
super(Epic_boss_spawnTable.class);
}
    public IntegergetBossId() {
        return get(BOSSID, Integer.class);
    }
    public IntegergetRespawnDate() {
        return get(RESPAWNDATE, Integer.class);
    }
    public IntegergetState() {
        return get(STATE, Integer.class);
    }

    public void setBossId() {
        set(BOSSID, value);
    }
    public void setRespawnDate() {
        set(RESPAWNDATE, value);
    }
    public void setState() {
        set(STATE, value);
    }

}
