package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.SpawnlistTable.*;

@Table(
        name = "spawnlist",
        fields = {
                @Field(name = LOCATION,
                @Field(name = COUNT,
                @Field(name = NPC_TEMPLATEID,
                @Field(name = LOCX,
                @Field(name = LOCY,
                @Field(name = LOCZ,
                @Field(name = HEADING,
                @Field(name = RESPAWN_DELAY,
                @Field(name = RESPAWN_DELAY_RND,
                @Field(name = LOC_ID,
                @Field(name = BANED_LOC_ID,
                @Field(name = PERIODOFDAY,
                @Field(name = REFLECTION,
                @Field(name = STATPARAM,
                @Field(name = AIPARAM,
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
    public static final String PERIODOFDAY = "periodOfDay";
    public static final String REFLECTION = "reflection";
    public static final String STATPARAM = "statParam";
    public static final String AIPARAM = "aiParam";
    public SpawnlistTable() {
super(SpawnlistTable.class);
}
    public StringgetLocation() {
        return get(LOCATION, String.class);
    }
    public IntegergetCount() {
        return get(COUNT, Integer.class);
    }
    public IntegergetNpc_templateid() {
        return get(NPC_TEMPLATEID, Integer.class);
    }
    public IntegergetLocx() {
        return get(LOCX, Integer.class);
    }
    public IntegergetLocy() {
        return get(LOCY, Integer.class);
    }
    public IntegergetLocz() {
        return get(LOCZ, Integer.class);
    }
    public IntegergetHeading() {
        return get(HEADING, Integer.class);
    }
    public IntegergetRespawn_delay() {
        return get(RESPAWN_DELAY, Integer.class);
    }
    public IntegergetRespawn_delay_rnd() {
        return get(RESPAWN_DELAY_RND, Integer.class);
    }
    public StringgetLoc_id() {
        return get(LOC_ID, String.class);
    }
    public StringgetBaned_loc_id() {
        return get(BANED_LOC_ID, String.class);
    }
    public IntegergetPeriodOfDay() {
        return get(PERIODOFDAY, Integer.class);
    }
    public IntegergetReflection() {
        return get(REFLECTION, Integer.class);
    }
    public StringgetStatParam() {
        return get(STATPARAM, String.class);
    }
    public StringgetAiParam() {
        return get(AIPARAM, String.class);
    }

    public void setLocation() {
        set(LOCATION, value);
    }
    public void setCount() {
        set(COUNT, value);
    }
    public void setNpc_templateid() {
        set(NPC_TEMPLATEID, value);
    }
    public void setLocx() {
        set(LOCX, value);
    }
    public void setLocy() {
        set(LOCY, value);
    }
    public void setLocz() {
        set(LOCZ, value);
    }
    public void setHeading() {
        set(HEADING, value);
    }
    public void setRespawn_delay() {
        set(RESPAWN_DELAY, value);
    }
    public void setRespawn_delay_rnd() {
        set(RESPAWN_DELAY_RND, value);
    }
    public void setLoc_id() {
        set(LOC_ID, value);
    }
    public void setBaned_loc_id() {
        set(BANED_LOC_ID, value);
    }
    public void setPeriodOfDay() {
        set(PERIODOFDAY, value);
    }
    public void setReflection() {
        set(REFLECTION, value);
    }
    public void setStatParam() {
        set(STATPARAM, value);
    }
    public void setAiParam() {
        set(AIPARAM, value);
    }

}
