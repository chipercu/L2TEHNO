package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CastleTable.*;

@Table(
        name = "castle",
        fields = {
                @Field(name = ID),
                @Field(name = NAME),
                @Field(name = TAX_PERCENT),
                @Field(name = TREASURY),
                @Field(name = SIEGE_DATE),
                @Field(name = SIEGE_DAY_OF_WEEK),
                @Field(name = SIEGE_HOUR_OF_DAY),
                @Field(name = TOWN_ID),
                @Field(name = SKILLS),
                @Field(name = FLAGS),
                @Field(name = OWN_DATE),
                @Field(name = DOMINION_LORD),
                @Field(name = SET_SIEGE),
        }
)
public class CastleTable extends DataBaseTable<CastleTable> {

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

    public CastleTable() {
super(CastleTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getTaxPercent() {
        return get(TAX_PERCENT, Integer.class);
    }
    public Long getTreasury() {
        return get(TREASURY, Long.class);
    }
    public Integer getSiegeDate() {
        return get(SIEGE_DATE, Integer.class);
    }
    public Integer getSiegeDayOfWeek() {
        return get(SIEGE_DAY_OF_WEEK, Integer.class);
    }
    public Integer getSiegeHourOfDay() {
        return get(SIEGE_HOUR_OF_DAY, Integer.class);
    }
    public Integer getTownId() {
        return get(TOWN_ID, Integer.class);
    }
    public String getSkills() {
        return get(SKILLS, String.class);
    }
    public String getFlags() {
        return get(FLAGS, String.class);
    }
    public Integer getOwnDate() {
        return get(OWN_DATE, Integer.class);
    }
    public Integer getDominionLord() {
        return get(DOMINION_LORD, Integer.class);
    }
    public Integer getSetSiege() {
        return get(SET_SIEGE, Integer.class);
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
