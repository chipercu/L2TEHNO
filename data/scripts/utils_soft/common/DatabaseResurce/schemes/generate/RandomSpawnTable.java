package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RandomSpawnTable.*;

@Table(
        name = "random_spawn",
        fields = {
                @Field(name = GROUP_ID),
                @Field(name = NPC_ID),
                @Field(name = COUNT),
                @Field(name = INITIAL_DELAY),
                @Field(name = RESPAWN_DELAY),
                @Field(name = DESPAWN_DELAY),
                @Field(name = BROADCAST_SPAWN),
                @Field(name = RANDOM_SPAWN),
        }
)
public class RandomSpawnTable extends DataBaseTable<RandomSpawnTable> {

    public static final String GROUP_ID = "groupId";
    public static final String NPC_ID = "npcId";
    public static final String COUNT = "count";
    public static final String INITIAL_DELAY = "initialDelay";
    public static final String RESPAWN_DELAY = "respawnDelay";
    public static final String DESPAWN_DELAY = "despawnDelay";
    public static final String BROADCAST_SPAWN = "broadcastSpawn";
    public static final String RANDOM_SPAWN = "randomSpawn";

    public RandomSpawnTable() {
super(RandomSpawnTable.class);
}

    public Integer getGroupId() {
        return get(GROUP_ID, Integer.class);
    }
    public Integer getNpcId() {
        return get(NPC_ID, Integer.class);
    }
    public Integer getCount() {
        return get(COUNT, Integer.class);
    }
    public Long getInitialDelay() {
        return get(INITIAL_DELAY, Long.class);
    }
    public Long getRespawnDelay() {
        return get(RESPAWN_DELAY, Long.class);
    }
    public Long getDespawnDelay() {
        return get(DESPAWN_DELAY, Long.class);
    }
    public Boolean getBroadcastSpawn() {
        return get(BROADCAST_SPAWN, Boolean.class);
    }
    public Boolean getRandomSpawn() {
        return get(RANDOM_SPAWN, Boolean.class);
    }

    public void setGroupId(Integer value) {
        set(GROUP_ID, value);
    }
    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setCount(Integer value) {
        set(COUNT, value);
    }
    public void setInitialDelay(Long value) {
        set(INITIAL_DELAY, value);
    }
    public void setRespawnDelay(Long value) {
        set(RESPAWN_DELAY, value);
    }
    public void setDespawnDelay(Long value) {
        set(DESPAWN_DELAY, value);
    }
    public void setBroadcastSpawn(Boolean value) {
        set(BROADCAST_SPAWN, value);
    }
    public void setRandomSpawn(Boolean value) {
        set(RANDOM_SPAWN, value);
    }

}
