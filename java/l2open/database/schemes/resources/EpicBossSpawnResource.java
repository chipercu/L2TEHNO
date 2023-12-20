package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.EpicBossSpawnResource.*;

@Table(
        name = "epic_boss_spawn",
        primary_key = {BOSS_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = BOSS_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = RESPAWN_DATE , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = STATE , defValue = "null"),
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
