package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Lastimperialtomb_spawnlistTable.*;

@Table(
        name = "lastimperialtomb_spawnlist",
        fields = {
                @Field(name = COUNT,
                @Field(name = NPC_TEMPLATEID,
                @Field(name = LOCX,
                @Field(name = LOCY,
                @Field(name = LOCZ,
                @Field(name = HEADING,
                @Field(name = RESPAWN_DELAY,
        }
)
public class Lastimperialtomb_spawnlistTable extends DataBaseTable<Lastimperialtomb_spawnlistTable> {

    public static final String COUNT = "count";
    public static final String NPC_TEMPLATEID = "npc_templateid";
    public static final String LOCX = "locx";
    public static final String LOCY = "locy";
    public static final String LOCZ = "locz";
    public static final String HEADING = "heading";
    public static final String RESPAWN_DELAY = "respawn_delay";
    public Lastimperialtomb_spawnlistTable() {
super(Lastimperialtomb_spawnlistTable.class);
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

}
