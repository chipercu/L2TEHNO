package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanDataResource.*;

@Table(
        name = "clan_data",
        primary_key = {CLAN_ID},
        fields = {
                @Field(name = CLAN_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CLAN_NAME , data_type = "varchar"),
                @Field(name = CLAN_LEVEL , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = HAS_CASTLE , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = HAS_FORTRESS , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = HAS_HIDEOUT , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = ALLY_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LEADER_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CREST , data_type = "varbinary"),
                @Field(name = LARGECREST , data_type = "varbinary"),
                @Field(name = REPUTATION_SCORE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = WAREHOUSE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = EXPELLED_MEMBER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LEAVED_ALLY , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = DISSOLVED_ALLY , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = AUCTION_BID_AT , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = AIRSHIP , data_type = "smallint" , defValue = @DefValue(Integer = -1)),
                @Field(name = POINT , data_type = "int" , defValue = @DefValue(Integer = 20)),
        }
)
public class ClanDataResource extends DataBaseTable<ClanDataResource> {

    public static final String CLAN_ID = "clan_id";
    public static final String CLAN_NAME = "clan_name";
    public static final String CLAN_LEVEL = "clan_level";
    public static final String HAS_CASTLE = "hasCastle";
    public static final String HAS_FORTRESS = "hasFortress";
    public static final String HAS_HIDEOUT = "hasHideout";
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

    public ClanDataResource() {
super(ClanDataResource.class);
}

    public Integer getClanId() {
        return get(CLAN_ID, Integer.class);
    }
    public String getClanName() {
        return get(CLAN_NAME, String.class);
    }
    public Integer getClanLevel() {
        return get(CLAN_LEVEL, Integer.class);
    }
    public Integer getHasCastle() {
        return get(HAS_CASTLE, Integer.class);
    }
    public Integer getHasFortress() {
        return get(HAS_FORTRESS, Integer.class);
    }
    public Integer getHasHideout() {
        return get(HAS_HIDEOUT, Integer.class);
    }
    public Integer getAllyId() {
        return get(ALLY_ID, Integer.class);
    }
    public Integer getLeaderId() {
        return get(LEADER_ID, Integer.class);
    }
    public String getCrest() {
        return get(CREST, String.class);
    }
    public String getLargecrest() {
        return get(LARGECREST, String.class);
    }
    public Integer getReputationScore() {
        return get(REPUTATION_SCORE, Integer.class);
    }
    public Integer getWarehouse() {
        return get(WAREHOUSE, Integer.class);
    }
    public Integer getExpelledMember() {
        return get(EXPELLED_MEMBER, Integer.class);
    }
    public Integer getLeavedAlly() {
        return get(LEAVED_ALLY, Integer.class);
    }
    public Integer getDissolvedAlly() {
        return get(DISSOLVED_ALLY, Integer.class);
    }
    public Integer getAuctionBidAt() {
        return get(AUCTION_BID_AT, Integer.class);
    }
    public Integer getAirship() {
        return get(AIRSHIP, Integer.class);
    }
    public Integer getPoint() {
        return get(POINT, Integer.class);
    }

    public void setClanId(Integer value) {
        set(CLAN_ID, value);
    }
    public void setClanName(String value) {
        set(CLAN_NAME, value);
    }
    public void setClanLevel(Integer value) {
        set(CLAN_LEVEL, value);
    }
    public void setHasCastle(Integer value) {
        set(HAS_CASTLE, value);
    }
    public void setHasFortress(Integer value) {
        set(HAS_FORTRESS, value);
    }
    public void setHasHideout(Integer value) {
        set(HAS_HIDEOUT, value);
    }
    public void setAllyId(Integer value) {
        set(ALLY_ID, value);
    }
    public void setLeaderId(Integer value) {
        set(LEADER_ID, value);
    }
    public void setCrest(String value) {
        set(CREST, value);
    }
    public void setLargecrest(String value) {
        set(LARGECREST, value);
    }
    public void setReputationScore(Integer value) {
        set(REPUTATION_SCORE, value);
    }
    public void setWarehouse(Integer value) {
        set(WAREHOUSE, value);
    }
    public void setExpelledMember(Integer value) {
        set(EXPELLED_MEMBER, value);
    }
    public void setLeavedAlly(Integer value) {
        set(LEAVED_ALLY, value);
    }
    public void setDissolvedAlly(Integer value) {
        set(DISSOLVED_ALLY, value);
    }
    public void setAuctionBidAt(Integer value) {
        set(AUCTION_BID_AT, value);
    }
    public void setAirship(Integer value) {
        set(AIRSHIP, value);
    }
    public void setPoint(Integer value) {
        set(POINT, value);
    }

}
