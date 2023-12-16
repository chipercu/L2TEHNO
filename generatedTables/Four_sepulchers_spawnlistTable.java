package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Four_sepulchers_spawnlistTable.*;

@Table(
        name = "four_sepulchers_spawnlist",
        fields = {
                @Field(name = ID,
                @Field(name = LOCATION,
                @Field(name = COUNT,
                @Field(name = NPC_TEMPLATEID,
                @Field(name = LOCX,
                @Field(name = LOCY,
                @Field(name = LOCZ,
                @Field(name = RANDOMX,
                @Field(name = RANDOMY,
                @Field(name = HEADING,
                @Field(name = RESPAWN_DELAY,
                @Field(name = KEY_NPC_ID,
                @Field(name = SPAWNTYPE,
        }
)
public class Four_sepulchers_spawnlistTable extends DataBaseTable<Four_sepulchers_spawnlistTable> {

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
    public Four_sepulchers_spawnlistTable() {
super(Four_sepulchers_spawnlistTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
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
    public IntegergetRandomx() {
        return get(RANDOMX, Integer.class);
    }
    public IntegergetRandomy() {
        return get(RANDOMY, Integer.class);
    }
    public IntegergetHeading() {
        return get(HEADING, Integer.class);
    }
    public IntegergetRespawn_delay() {
        return get(RESPAWN_DELAY, Integer.class);
    }
    public IntegergetKey_npc_id() {
        return get(KEY_NPC_ID, Integer.class);
    }
    public IntegergetSpawntype() {
        return get(SPAWNTYPE, Integer.class);
    }

    public void setId() {
        set(ID, value);
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
    public void setRandomx() {
        set(RANDOMX, value);
    }
    public void setRandomy() {
        set(RANDOMY, value);
    }
    public void setHeading() {
        set(HEADING, value);
    }
    public void setRespawn_delay() {
        set(RESPAWN_DELAY, value);
    }
    public void setKey_npc_id() {
        set(KEY_NPC_ID, value);
    }
    public void setSpawntype() {
        set(SPAWNTYPE, value);
    }

}
