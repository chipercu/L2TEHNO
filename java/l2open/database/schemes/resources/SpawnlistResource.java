package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SpawnlistResource.*;

@Table(
        name = "spawnlist",
        primary_key = {NPC_TEMPLATEID,LOCX,LOCY,LOCZ,LOC_ID,REFLECTION},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 35 , name = LOCATION , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = NPC_TEMPLATEID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOCX , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOCY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOCZ , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = HEADING , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = RESPAWN_DELAY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = RESPAWN_DELAY_RND , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 325 , name = LOC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 325 , name = BANED_LOC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = PERIOD_OF_DAY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = REFLECTION , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 500 , name = STAT_PARAM , defValue = "-1"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.TEXT , type_size = 65535 , name = AI_PARAM , defValue = "-1"),
        }
)
public class SpawnlistResource extends DataBaseTable<SpawnlistResource> {

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

    public SpawnlistResource() {
        super(SpawnlistResource.class);
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
    public Integer getHeading() {
        return get(HEADING);
    }
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY);
    }
    public Integer getRespawnDelayRnd() {
        return get(RESPAWN_DELAY_RND);
    }
    public String getLocId() {
        return get(LOC_ID);
    }
    public String getBanedLocId() {
        return get(BANED_LOC_ID);
    }
    public Integer getPeriodOfDay() {
        return get(PERIOD_OF_DAY);
    }
    public Integer getReflection() {
        return get(REFLECTION);
    }
    public String getStatParam() {
        return get(STAT_PARAM);
    }
    public String getAiParam() {
        return get(AI_PARAM);
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
