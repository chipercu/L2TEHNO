package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.DominionRewardsResource.*;

@Table(
        name = "dominion_rewards",
        primary_key = {ID,OBJECT_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJECT_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = STATIC_BADGES , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = KILL_REWARD , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ONLINE_REWARD , defValue = "null"),
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

    public Integer getId() {
        return get(ID);
    }
    public Integer getObjectId() {
        return get(OBJECT_ID);
    }
    public Integer getStaticBadges() {
        return get(STATIC_BADGES);
    }
    public Integer getKillReward() {
        return get(KILL_REWARD);
    }
    public Integer getOnlineReward() {
        return get(ONLINE_REWARD);
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
