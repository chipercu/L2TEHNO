package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.FourSepulchersSpawnlistTable.*;

@Table(
        name = "four_sepulchers_spawnlist",
        fields = {
                @Field(name = ID),
                @Field(name = LOCATION),
                @Field(name = COUNT),
                @Field(name = NPC_TEMPLATEID),
                @Field(name = LOCX),
                @Field(name = LOCY),
                @Field(name = LOCZ),
                @Field(name = RANDOMX),
                @Field(name = RANDOMY),
                @Field(name = HEADING),
                @Field(name = RESPAWN_DELAY),
                @Field(name = KEY_NPC_ID),
                @Field(name = SPAWNTYPE),
        }
)
public class FourSepulchersSpawnlistTable extends DataBaseTable<FourSepulchersSpawnlistTable> {

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

    public FourSepulchersSpawnlistTable() {
super(FourSepulchersSpawnlistTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getLocation() {
        return get(LOCATION, String.class);
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
    public Integer getRandomx() {
        return get(RANDOMX, Integer.class);
    }
    public Integer getRandomy() {
        return get(RANDOMY, Integer.class);
    }
    public Integer getHeading() {
        return get(HEADING, Integer.class);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY, Integer.class);
    }
    public Integer getKeyNpcId() {
        return get(KEY_NPC_ID, Integer.class);
    }
    public Integer getSpawntype() {
        return get(SPAWNTYPE, Integer.class);
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
