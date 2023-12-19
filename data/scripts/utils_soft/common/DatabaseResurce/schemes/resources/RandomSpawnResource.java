package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.RandomSpawnResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "random_spawn",
        primary_key = {GROUP_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = GROUP_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = INITIAL_DELAY , defValue = "-1"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = RESPAWN_DELAY , defValue = "-1"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = DESPAWN_DELAY , defValue = "-1"),
                @Column(is_null = NO , type = ENUM , type_size = 5 , name = BROADCAST_SPAWN , defValue = "false"),
                @Column(is_null = NO , type = ENUM , type_size = 5 , name = RANDOM_SPAWN , defValue = "true"),
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
        return get(GROUP_ID);
    }
    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public Integer getCount() {
        return get(COUNT);
    }
    public Long getInitialDelay() {
        return get(INITIAL_DELAY);
    }
    public Long getRespawnDelay() {
        return get(RESPAWN_DELAY);
    }
    public Long getDespawnDelay() {
        return get(DESPAWN_DELAY);
    }
    public Boolean getBroadcastSpawn() {
        return get(BROADCAST_SPAWN);
    }
    public Boolean getRandomSpawn() {
        return get(RANDOM_SPAWN);
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
