package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.RaidbossStatusResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "raidboss_status",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = CURRENT_HP , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = CURRENT_MP , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RESPAWN_DELAY , defValue = "0"),
        }
)
public class RaidbossStatusResource extends DataBaseTable<RaidbossStatusResource> {

    public static final String ID = "id";
    public static final String CURRENT_HP = "current_hp";
    public static final String CURRENT_MP = "current_mp";
    public static final String RESPAWN_DELAY = "respawn_delay";

    public RaidbossStatusResource() {
        super(RaidbossStatusResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getCurrentHp() {
        return get(CURRENT_HP);
    }
    public Integer getCurrentMp() {
        return get(CURRENT_MP);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setCurrentHp(Integer value) {
        set(CURRENT_HP, value);
    }
    public void setCurrentMp(Integer value) {
        set(CURRENT_MP, value);
    }
    public void setRespawnDelay(Integer value) {
        set(RESPAWN_DELAY, value);
    }

}
