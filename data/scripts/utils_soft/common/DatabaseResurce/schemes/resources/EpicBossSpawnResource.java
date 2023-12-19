package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.EpicBossSpawnResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "epic_boss_spawn",
        primary_key = {BOSS_ID},
        fields = {
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = BOSS_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RESPAWN_DATE , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = STATE , defValue = "null"),
        }
)
public class EpicBossSpawnResource extends DataBaseTable<EpicBossSpawnResource> {

    public static final String BOSS_ID = "bossId";
    public static final String RESPAWN_DATE = "respawnDate";
    public static final String STATE = "state";

    public EpicBossSpawnResource() {
        super(EpicBossSpawnResource.class);
}

    public Integer getBossId() {
        return get(BOSS_ID);
    }
    public Integer getRespawnDate() {
        return get(RESPAWN_DATE);
    }
    public Integer getState() {
        return get(STATE);
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
