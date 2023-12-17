package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RaidbossStatusResource.*;

@Table(
        name = "raidboss_status",
        primary_key = {ID},
        fields = {
                @Field(name = ID , data_type = "int"),
                @Field(name = CURRENT_HP , data_type = "int"),
                @Field(name = CURRENT_MP , data_type = "int"),
                @Field(name = RESPAWN_DELAY , data_type = "int" , defValue = @DefValue(Integer = 0)),
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
        return get(ID, Integer.class);
    }
    public Integer getCurrentHp() {
        return get(CURRENT_HP, Integer.class);
    }
    public Integer getCurrentMp() {
        return get(CURRENT_MP, Integer.class);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY, Integer.class);
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
