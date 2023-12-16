package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SpawnlistTable.*;

@Table(
        name = "spawnlist",
        fields = {
                @Field(name = LOCATION),
                @Field(name = COUNT),
                @Field(name = NPC_TEMPLATEID),
                @Field(name = LOCX),
                @Field(name = LOCY),
                @Field(name = LOCZ),
                @Field(name = HEADING),
                @Field(name = RESPAWN_DELAY),
                @Field(name = RESPAWN_DELAY_RND),
                @Field(name = LOC_ID),
                @Field(name = BANED_LOC_ID),
                @Field(name = PERIOD_OF_DAY),
                @Field(name = REFLECTION),
                @Field(name = STAT_PARAM),
                @Field(name = AI_PARAM),
        }
)
public class SpawnlistTable extends DataBaseTable<SpawnlistTable> {

    public static final String LOCATION = "location";
    public static final String COUNT = "count";
    public static final String NPC_TEMPLATEID = "npc_templateid";
    public static final String LOCX = "locx";
    public static final String LOCY = "locy";
    public static final String LOCZ = "locz";
    public static final String HEADING = "heading";
    public static final String RESPAWN_DELAY = "respawn_delay";
    public static final String RESPAWN_DELAY_RND = "respawn_delay_rnd";
    public static final String LOC_ID = "loc_id";
    public static final String BANED_LOC_ID = "baned_loc_id";
    public static final String PERIOD_OF_DAY = "periodOfDay";
    public static final String REFLECTION = "reflection";
    public static final String STAT_PARAM = "statParam";
    public static final String AI_PARAM = "aiParam";

    public SpawnlistTable() {
super(SpawnlistTable.class);
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
    public Integer getHeading() {
        return get(HEADING, Integer.class);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY, Integer.class);
    }
    public Integer getRespawnDelayRnd() {
        return get(RESPAWN_DELAY_RND, Integer.class);
    }
    public String getLocId() {
        return get(LOC_ID, String.class);
    }
    public String getBanedLocId() {
        return get(BANED_LOC_ID, String.class);
    }
    public Integer getPeriodOfDay() {
        return get(PERIOD_OF_DAY, Integer.class);
    }
    public Integer getReflection() {
        return get(REFLECTION, Integer.class);
    }
    public String getStatParam() {
        return get(STAT_PARAM, String.class);
    }
    public String getAiParam() {
        return get(AI_PARAM, String.class);
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
    public void setHeading(Integer value) {
        set(HEADING, value);
    }
    public void setRespawnDelay(Integer value) {
        set(RESPAWN_DELAY, value);
    }
    public void setRespawnDelayRnd(Integer value) {
        set(RESPAWN_DELAY_RND, value);
    }
    public void setLocId(String value) {
        set(LOC_ID, value);
    }
    public void setBanedLocId(String value) {
        set(BANED_LOC_ID, value);
    }
    public void setPeriodOfDay(Integer value) {
        set(PERIOD_OF_DAY, value);
    }
    public void setReflection(Integer value) {
        set(REFLECTION, value);
    }
    public void setStatParam(String value) {
        set(STAT_PARAM, value);
    }
    public void setAiParam(String value) {
        set(AI_PARAM, value);
    }

}
