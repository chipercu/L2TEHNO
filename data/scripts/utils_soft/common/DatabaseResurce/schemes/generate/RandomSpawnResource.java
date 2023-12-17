package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RandomSpawnResource.*;

@Table(
        name = "random_spawn",
        primary_key = {GROUP_ID},
        fields = {
                @Field(name = GROUP_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = NPC_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = COUNT , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = INITIAL_DELAY , data_type = "bigint" , defValue = @DefValue(Long = -1)),
                @Field(name = RESPAWN_DELAY , data_type = "bigint" , defValue = @DefValue(Long = -1)),
                @Field(name = DESPAWN_DELAY , data_type = "bigint" , defValue = @DefValue(Long = -1)),
                @Field(name = BROADCAST_SPAWN , data_type = "enum" , defValue = @DefValue(Boolean = false)),
                @Field(name = RANDOM_SPAWN , data_type = "enum" , defValue = @DefValue(Boolean = false)),
        }
)
public class RandomSpawnResource extends DataBaseTable<RandomSpawnResource> {

    public static final String GROUP_ID = "groupId";
    public static final String NPC_ID = "npcId";
    public static final String COUNT = "count";
    public static final String INITIAL_DELAY = "initialDelay";
    public static final String RESPAWN_DELAY = "respawnDelay";
    public static final String DESPAWN_DELAY = "despawnDelay";
    public static final String BROADCAST_SPAWN = "broadcastSpawn";
    public static final String RANDOM_SPAWN = "randomSpawn";

    public RandomSpawnResource() {
super(RandomSpawnResource.class);
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
