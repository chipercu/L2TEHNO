package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.LastimperialtombSpawnlistResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "lastimperialtomb_spawnlist",
        primary_key = {NPC_TEMPLATEID,LOCX,LOCY,LOCZ},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPC_TEMPLATEID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOCX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOCY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOCZ , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HEADING , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RESPAWN_DELAY , defValue = "0"),
        }
)
public class LastimperialtombSpawnlistResource extends DataBaseTable<LastimperialtombSpawnlistResource> {

    public static final String COUNT = "count";
    public static final String NPC_TEMPLATEID = "npc_templateid";
    public static final String LOCX = "locx";
    public static final String LOCY = "locy";
    public static final String LOCZ = "locz";
    public static final String HEADING = "heading";
    public static final String RESPAWN_DELAY = "respawn_delay";

    public LastimperialtombSpawnlistResource() {
        super(LastimperialtombSpawnlistResource.class);
}

    public Integer getCount() {
        return get(COUNT);
    }
    public Integer getNpcTemplateid() {
        return get(NPC_TEMPLATEID);
    }
    public Integer getLocx() {
        return get(LOCX);
    }
    public Integer getLocy() {
        return get(LOCY);
    }
    public Integer getLocz() {
        return get(LOCZ);
    }
    public Integer getHeading() {
        return get(HEADING);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY);
    }

    public void setCount(Integer value) {
        set(COUNT, value);
    }
    public void setNpcTemplateid(Integer value) {
        set(NPC_TEMPLATEID, value);
    }
    public void setLocx(Integer value) {
        set(LOCX, value);
    }
    public void setLocy(Integer value) {
        set(LOCY, value);
    }
    public void setLocz(Integer value) {
        set(LOCZ, value);
    }
    public void setHeading(Integer value) {
        set(HEADING, value);
    }
    public void setRespawnDelay(Integer value) {
        set(RESPAWN_DELAY, value);
    }

}
