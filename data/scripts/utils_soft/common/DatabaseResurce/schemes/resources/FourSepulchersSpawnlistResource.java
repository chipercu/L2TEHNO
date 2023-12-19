package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.FourSepulchersSpawnlistResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "four_sepulchers_spawnlist",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 19 , name = LOCATION , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPC_TEMPLATEID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOCX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOCY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOCZ , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RANDOMX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RANDOMY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HEADING , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RESPAWN_DELAY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = KEY_NPC_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SPAWNTYPE , defValue = "0"),
        }
)
public class FourSepulchersSpawnlistResource extends DataBaseTable<FourSepulchersSpawnlistResource> {

    public static final String ID = "id";
    public static final String LOCATION = "location";
    public static final String COUNT = "count";
    public static final String NPC_TEMPLATEID = "npc_templateid";
    public static final String LOCX = "locx";
    public static final String LOCY = "locy";
    public static final String LOCZ = "locz";
    public static final String RANDOMX = "randomx";
    public static final String RANDOMY = "randomy";
    public static final String HEADING = "heading";
    public static final String RESPAWN_DELAY = "respawn_delay";
    public static final String KEY_NPC_ID = "key_npc_id";
    public static final String SPAWNTYPE = "spawntype";

    public FourSepulchersSpawnlistResource() {
        super(FourSepulchersSpawnlistResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getLocation() {
        return get(LOCATION);
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
    public Integer getRandomx() {
        return get(RANDOMX);
    }
    public Integer getRandomy() {
        return get(RANDOMY);
    }
    public Integer getHeading() {
        return get(HEADING);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY);
    }
    public Integer getKeyNpcId() {
        return get(KEY_NPC_ID);
    }
    public Integer getSpawntype() {
        return get(SPAWNTYPE);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setLocation(String value) {
        set(LOCATION, value);
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
    public void setRandomx(Integer value) {
        set(RANDOMX, value);
    }
    public void setRandomy(Integer value) {
        set(RANDOMY, value);
    }
    public void setHeading(Integer value) {
        set(HEADING, value);
    }
    public void setRespawnDelay(Integer value) {
        set(RESPAWN_DELAY, value);
    }
    public void setKeyNpcId(Integer value) {
        set(KEY_NPC_ID, value);
    }
    public void setSpawntype(Integer value) {
        set(SPAWNTYPE, value);
    }

}
