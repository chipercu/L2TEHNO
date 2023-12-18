package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.DominionRewardsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "dominion_rewards",
        primary_key = {ID,OBJECT_ID},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = OBJECT_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = STATIC_BADGES , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = KILL_REWARD , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = ONLINE_REWARD , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
        }
)
public class DominionRewardsResource extends DataBaseTable<DominionRewardsResource> {

    public static final String ID = "id";
    public static final String OBJECT_ID = "object_id";
    public static final String STATIC_BADGES = "static_badges";
    public static final String KILL_REWARD = "kill_reward";
    public static final String ONLINE_REWARD = "online_reward";

    public DominionRewardsResource() {
super(DominionRewardsResource.class);
}


    public DominionRewardsResource(String id,String object_id){
        super(DominionRewardsResource.class);
        getSTAT_SET().set(ID, id);
        getSTAT_SET().set(OBJECT_ID, object_id);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getObjectId() {
        return get(OBJECT_ID, Integer.class);
    }
    public Integer getStaticBadges() {
        return get(STATIC_BADGES, Integer.class);
    }
    public Integer getKillReward() {
        return get(KILL_REWARD, Integer.class);
    }
    public Integer getOnlineReward() {
        return get(ONLINE_REWARD, Integer.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setObjectId(Integer value) {
        set(OBJECT_ID, value);
    }
    public void setStaticBadges(Integer value) {
        set(STATIC_BADGES, value);
    }
    public void setKillReward(Integer value) {
        set(KILL_REWARD, value);
    }
    public void setOnlineReward(Integer value) {
        set(ONLINE_REWARD, value);
    }

}
