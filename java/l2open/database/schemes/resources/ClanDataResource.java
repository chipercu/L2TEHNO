package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ClanDataResource.*;

@Table(
        name = "clan_data",
        primary_key = {CLAN_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLAN_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 45 , name = CLAN_NAME , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = CLAN_LEVEL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = HAS_CASTLE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = HAS_FORTRESS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = HAS_HIDEOUT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ALLY_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEADER_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARBINARY , type_size = 256 , name = CREST , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARBINARY , type_size = 8192 , name = LARGECREST , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = REPUTATION_SCORE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = WAREHOUSE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = EXPELLED_MEMBER , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEAVED_ALLY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = DISSOLVED_ALLY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = AUCTION_BID_AT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = AIRSHIP , defValue = "-1"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = POINT , defValue = "20"),
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
        return get(CLAN_ID);
    }
    public String getClanName() {
        return get(CLAN_NAME);
    }
    public Integer getClanLevel() {
        return get(CLAN_LEVEL);
    }
    public Integer getHasCastle() {
        return get(HAS_CASTLE);
    }
    public Integer getHasFortress() {
        return get(HAS_FORTRESS);
    }
    public Integer getHasHideout() {
        return get(HAS_HIDEOUT);
    }
    public Integer getAllyId() {
        return get(ALLY_ID);
    }
    public Integer getLeaderId() {
        return get(LEADER_ID);
    }
    public String getCrest() {
        return get(CREST);
    }
    public String getLargecrest() {
        return get(LARGECREST);
    }
    public Integer getReputationScore() {
        return get(REPUTATION_SCORE);
    }
    public Integer getWarehouse() {
        return get(WAREHOUSE);
    }
    public Integer getExpelledMember() {
        return get(EXPELLED_MEMBER);
    }
    public Integer getLeavedAlly() {
        return get(LEAVED_ALLY);
    }
    public Integer getDissolvedAlly() {
        return get(DISSOLVED_ALLY);
    }
    public Integer getAuctionBidAt() {
        return get(AUCTION_BID_AT);
    }
    public Integer getAirship() {
        return get(AIRSHIP);
    }
    public Integer getPoint() {
        return get(POINT);
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
