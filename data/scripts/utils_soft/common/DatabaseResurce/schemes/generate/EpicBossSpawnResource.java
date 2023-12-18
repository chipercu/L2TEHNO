package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.EpicBossSpawnResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "epic_boss_spawn",
        primary_key = {BOSS_ID},
        fields = {
                @Field(name = BOSS_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = RESPAWN_DATE , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = STATE , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
        }
)
public class EpicBossSpawnResource extends DataBaseTable<EpicBossSpawnResource> {

    public static final String BOSS_ID = "bossId";
    public static final String RESPAWN_DATE = "respawnDate";
    public static final String STATE = "state";

    public EpicBossSpawnResource() {
super(EpicBossSpawnResource.class);
}


    public EpicBossSpawnResource(String bossId){
        super(EpicBossSpawnResource.class);
        getSTAT_SET().set(BOSS_ID, bossId);
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
