package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Random_spawnTable.*;

@Table(
        name = "random_spawn",
        fields = {
                @Field(name = GROUPID,
                @Field(name = NPCID,
                @Field(name = COUNT,
                @Field(name = INITIALDELAY,
                @Field(name = RESPAWNDELAY,
                @Field(name = DESPAWNDELAY,
                @Field(name = BROADCASTSPAWN,
                @Field(name = RANDOMSPAWN,
        }
)
public class Random_spawnTable extends DataBaseTable<Random_spawnTable> {

    public static final String GROUPID = "groupId";
    public static final String NPCID = "npcId";
    public static final String COUNT = "count";
    public static final String INITIALDELAY = "initialDelay";
    public static final String RESPAWNDELAY = "respawnDelay";
    public static final String DESPAWNDELAY = "despawnDelay";
    public static final String BROADCASTSPAWN = "broadcastSpawn";
    public static final String RANDOMSPAWN = "randomSpawn";
    public Random_spawnTable() {
super(Random_spawnTable.class);
}
    public IntegergetGroupId() {
        return get(GROUPID, Integer.class);
    }
    public IntegergetNpcId() {
        return get(NPCID, Integer.class);
    }
    public IntegergetCount() {
        return get(COUNT, Integer.class);
    }
    public ObjectgetInitialDelay() {
        return get(INITIALDELAY, Object.class);
    }
    public ObjectgetRespawnDelay() {
        return get(RESPAWNDELAY, Object.class);
    }
    public ObjectgetDespawnDelay() {
        return get(DESPAWNDELAY, Object.class);
    }
    public ObjectgetBroadcastSpawn() {
        return get(BROADCASTSPAWN, Object.class);
    }
    public ObjectgetRandomSpawn() {
        return get(RANDOMSPAWN, Object.class);
    }

    public void setGroupId() {
        set(GROUPID, value);
    }
    public void setNpcId() {
        set(NPCID, value);
    }
    public void setCount() {
        set(COUNT, value);
    }
    public void setInitialDelay() {
        set(INITIALDELAY, value);
    }
    public void setRespawnDelay() {
        set(RESPAWNDELAY, value);
    }
    public void setDespawnDelay() {
        set(DESPAWNDELAY, value);
    }
    public void setBroadcastSpawn() {
        set(BROADCASTSPAWN, value);
    }
    public void setRandomSpawn() {
        set(RANDOMSPAWN, value);
    }

}
