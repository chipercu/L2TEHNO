package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Dominion_rewardsTable.*;

@Table(
        name = "dominion_rewards",
        fields = {
                @Field(name = ID,
                @Field(name = OBJECT_ID,
                @Field(name = STATIC_BADGES,
                @Field(name = KILL_REWARD,
                @Field(name = ONLINE_REWARD,
        }
)
public class Dominion_rewardsTable extends DataBaseTable<Dominion_rewardsTable> {

    public static final String ID = "id";
    public static final String OBJECT_ID = "object_id";
    public static final String STATIC_BADGES = "static_badges";
    public static final String KILL_REWARD = "kill_reward";
    public static final String ONLINE_REWARD = "online_reward";
    public Dominion_rewardsTable() {
super(Dominion_rewardsTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetObject_id() {
        return get(OBJECT_ID, Integer.class);
    }
    public IntegergetStatic_badges() {
        return get(STATIC_BADGES, Integer.class);
    }
    public IntegergetKill_reward() {
        return get(KILL_REWARD, Integer.class);
    }
    public IntegergetOnline_reward() {
        return get(ONLINE_REWARD, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setObject_id() {
        set(OBJECT_ID, value);
    }
    public void setStatic_badges() {
        set(STATIC_BADGES, value);
    }
    public void setKill_reward() {
        set(KILL_REWARD, value);
    }
    public void setOnline_reward() {
        set(ONLINE_REWARD, value);
    }

}
