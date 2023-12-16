package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.EpicBossSpawnTable.*;

@Table(
        name = "epic_boss_spawn",
        fields = {
                @Field(name = BOSS_ID),
                @Field(name = RESPAWN_DATE),
                @Field(name = STATE),
        }
)
public class EpicBossSpawnTable extends DataBaseTable<EpicBossSpawnTable> {

    public static final String BOSS_ID = "bossId";
    public static final String RESPAWN_DATE = "respawnDate";
    public static final String STATE = "state";

    public EpicBossSpawnTable() {
super(EpicBossSpawnTable.class);
}

    public Integer getBossId() {
        return get(BOSS_ID, Integer.class);
    }
    public Integer getRespawnDate() {
        return get(RESPAWN_DATE, Integer.class);
    }
    public Integer getState() {
        return get(STATE, Integer.class);
    }

    public void setBossId(Integer value) {
        set(BOSS_ID, value);
    }
    public void setRespawnDate(Integer value) {
        set(RESPAWN_DATE, value);
    }
    public void setState(Integer value) {
        set(STATE, value);
    }

}
