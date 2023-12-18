package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LastimperialtombSpawnlistResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "lastimperialtomb_spawnlist",
        primary_key = {NPC_TEMPLATEID,LOCX,LOCY,LOCZ},
        fields = {
                @Field(name = COUNT , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NPC_TEMPLATEID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LOCX , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LOCY , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LOCZ , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = HEADING , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = RESPAWN_DELAY , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public LastimperialtombSpawnlistResource(String npc_templateid,String locx,String locy,String locz){
        super(LastimperialtombSpawnlistResource.class);
        getSTAT_SET().set(NPC_TEMPLATEID, npc_templateid);
        getSTAT_SET().set(LOCX, locx);
        getSTAT_SET().set(LOCY, locy);
        getSTAT_SET().set(LOCZ, locz);
    }

    public Integer getCount() {
        return get(COUNT, Integer.class);
    }
    public Integer getNpcTemplateid() {
        return get(NPC_TEMPLATEID, Integer.class);
    }
    public Integer getLocx() {
        return get(LOCX, Integer.class);
    }
    public Integer getLocy() {
        return get(LOCY, Integer.class);
    }
    public Integer getLocz() {
        return get(LOCZ, Integer.class);
    }
    public Integer getHeading() {
        return get(HEADING, Integer.class);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY, Integer.class);
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
