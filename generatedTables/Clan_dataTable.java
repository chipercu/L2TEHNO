package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Clan_dataTable.*;

@Table(
        name = "clan_data",
        fields = {
                @Field(name = CLAN_ID,
                @Field(name = CLAN_NAME,
                @Field(name = CLAN_LEVEL,
                @Field(name = HASCASTLE,
                @Field(name = HASFORTRESS,
                @Field(name = HASHIDEOUT,
                @Field(name = ALLY_ID,
                @Field(name = LEADER_ID,
                @Field(name = CREST,
                @Field(name = LARGECREST,
                @Field(name = REPUTATION_SCORE,
                @Field(name = WAREHOUSE,
                @Field(name = EXPELLED_MEMBER,
                @Field(name = LEAVED_ALLY,
                @Field(name = DISSOLVED_ALLY,
                @Field(name = AUCTION_BID_AT,
                @Field(name = AIRSHIP,
                @Field(name = POINT,
        }
)
public class Clan_dataTable extends DataBaseTable<Clan_dataTable> {

    public static final String CLAN_ID = "clan_id";
    public static final String CLAN_NAME = "clan_name";
    public static final String CLAN_LEVEL = "clan_level";
    public static final String HASCASTLE = "hasCastle";
    public static final String HASFORTRESS = "hasFortress";
    public static final String HASHIDEOUT = "hasHideout";
    public static final String ALLY_ID = "ally_id";
    public static final String LEADER_ID = "leader_id";
    public static final String CREST = "crest";
    public static final String LARGECREST = "largecrest";
    public static final String REPUTATION_SCORE = "reputation_score";
    public static final String WAREHOUSE = "warehouse";
    public static final String EXPELLED_MEMBER = "expelled_member";
    public static final String LEAVED_ALLY = "leaved_ally";
    public static final String DISSOLVED_ALLY = "dissolved_ally";
    public static final String AUCTION_BID_AT = "auction_bid_at";
    public static final String AIRSHIP = "airship";
    public static final String POINT = "point";
    public Clan_dataTable() {
super(Clan_dataTable.class);
}
    public IntegergetClan_id() {
        return get(CLAN_ID, Integer.class);
    }
    public StringgetClan_name() {
        return get(CLAN_NAME, String.class);
    }
    public IntegergetClan_level() {
        return get(CLAN_LEVEL, Integer.class);
    }
    public IntegergetHasCastle() {
        return get(HASCASTLE, Integer.class);
    }
    public IntegergetHasFortress() {
        return get(HASFORTRESS, Integer.class);
    }
    public IntegergetHasHideout() {
        return get(HASHIDEOUT, Integer.class);
    }
    public IntegergetAlly_id() {
        return get(ALLY_ID, Integer.class);
    }
    public IntegergetLeader_id() {
        return get(LEADER_ID, Integer.class);
    }
    public ObjectgetCrest() {
        return get(CREST, Object.class);
    }
    public ObjectgetLargecrest() {
        return get(LARGECREST, Object.class);
    }
    public IntegergetReputation_score() {
        return get(REPUTATION_SCORE, Integer.class);
    }
    public IntegergetWarehouse() {
        return get(WAREHOUSE, Integer.class);
    }
    public IntegergetExpelled_member() {
        return get(EXPELLED_MEMBER, Integer.class);
    }
    public IntegergetLeaved_ally() {
        return get(LEAVED_ALLY, Integer.class);
    }
    public IntegergetDissolved_ally() {
        return get(DISSOLVED_ALLY, Integer.class);
    }
    public IntegergetAuction_bid_at() {
        return get(AUCTION_BID_AT, Integer.class);
    }
    public IntegergetAirship() {
        return get(AIRSHIP, Integer.class);
    }
    public IntegergetPoint() {
        return get(POINT, Integer.class);
    }

    public void setClan_id() {
        set(CLAN_ID, value);
    }
    public void setClan_name() {
        set(CLAN_NAME, value);
    }
    public void setClan_level() {
        set(CLAN_LEVEL, value);
    }
    public void setHasCastle() {
        set(HASCASTLE, value);
    }
    public void setHasFortress() {
        set(HASFORTRESS, value);
    }
    public void setHasHideout() {
        set(HASHIDEOUT, value);
    }
    public void setAlly_id() {
        set(ALLY_ID, value);
    }
    public void setLeader_id() {
        set(LEADER_ID, value);
    }
    public void setCrest() {
        set(CREST, value);
    }
    public void setLargecrest() {
        set(LARGECREST, value);
    }
    public void setReputation_score() {
        set(REPUTATION_SCORE, value);
    }
    public void setWarehouse() {
        set(WAREHOUSE, value);
    }
    public void setExpelled_member() {
        set(EXPELLED_MEMBER, value);
    }
    public void setLeaved_ally() {
        set(LEAVED_ALLY, value);
    }
    public void setDissolved_ally() {
        set(DISSOLVED_ALLY, value);
    }
    public void setAuction_bid_at() {
        set(AUCTION_BID_AT, value);
    }
    public void setAirship() {
        set(AIRSHIP, value);
    }
    public void setPoint() {
        set(POINT, value);
    }

}
