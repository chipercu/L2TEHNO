package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CastleResource.*;

@Table(
        name = "castle",
        primary_key = {NAME},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 25 , name = NAME , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = TAX_PERCENT , defValue = "15"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = TREASURY , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SIEGE_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SIEGE_DAY_OF_WEEK , defValue = "1"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SIEGE_HOUR_OF_DAY , defValue = "16"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = TOWN_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = SKILLS , defValue = "0;0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = FLAGS , defValue = "0;0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OWN_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = DOMINION_LORD , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SET_SIEGE , defValue = "1"),
        }
)
public class CastleResource extends DataBaseTable<CastleResource> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TAX_PERCENT = "taxPercent";
    public static final String TREASURY = "treasury";
    public static final String SIEGE_DATE = "siegeDate";
    public static final String SIEGE_DAY_OF_WEEK = "siegeDayOfWeek";
    public static final String SIEGE_HOUR_OF_DAY = "siegeHourOfDay";
    public static final String TOWN_ID = "townId";
    public static final String SKILLS = "skills";
    public static final String FLAGS = "flags";
    public static final String OWN_DATE = "ownDate";
    public static final String DOMINION_LORD = "dominionLord";
    public static final String SET_SIEGE = "setSiege";

    public CastleResource() {
        super(CastleResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getTaxPercent() {
        return get(TAX_PERCENT);
    }
    public Long getTreasury() {
        return get(TREASURY);
    }
    public Integer getSiegeDate() {
        return get(SIEGE_DATE);
    }
    public Integer getSiegeDayOfWeek() {
        return get(SIEGE_DAY_OF_WEEK);
    }
    public Integer getSiegeHourOfDay() {
        return get(SIEGE_HOUR_OF_DAY);
    }
    public Integer getTownId() {
        return get(TOWN_ID);
    }
    public String getSkills() {
        return get(SKILLS);
    }
    public String getFlags() {
        return get(FLAGS);
    }
    public Integer getOwnDate() {
        return get(OWN_DATE);
    }
    public Integer getDominionLord() {
        return get(DOMINION_LORD);
    }
    public Integer getSetSiege() {
        return get(SET_SIEGE);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setTaxPercent(Integer value) {
        set(TAX_PERCENT, value);
    }
    public void setTreasury(Long value) {
        set(TREASURY, value);
    }
    public void setSiegeDate(Integer value) {
        set(SIEGE_DATE, value);
    }
    public void setSiegeDayOfWeek(Integer value) {
        set(SIEGE_DAY_OF_WEEK, value);
    }
    public void setSiegeHourOfDay(Integer value) {
        set(SIEGE_HOUR_OF_DAY, value);
    }
    public void setTownId(Integer value) {
        set(TOWN_ID, value);
    }
    public void setSkills(String value) {
        set(SKILLS, value);
    }
    public void setFlags(String value) {
        set(FLAGS, value);
    }
    public void setOwnDate(Integer value) {
        set(OWN_DATE, value);
    }
    public void setDominionLord(Integer value) {
        set(DOMINION_LORD, value);
    }
    public void setSetSiege(Integer value) {
        set(SET_SIEGE, value);
    }

}
