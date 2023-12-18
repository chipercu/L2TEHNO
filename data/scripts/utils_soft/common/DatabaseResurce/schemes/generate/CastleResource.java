package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CastleResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "castle",
        primary_key = {NAME},
        fields = {
                @Field(name = ID , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , type = VARCHAR , type_size = 25 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = TAX_PERCENT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 15)),
                @Field(name = TREASURY , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = SIEGE_DATE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SIEGE_DAY_OF_WEEK , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = SIEGE_HOUR_OF_DAY , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 16)),
                @Field(name = TOWN_ID , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILLS , type = VARCHAR , type_size = 32 , nullable = false , defValue = @DefValue(String = "0;0")),
                @Field(name = FLAGS , type = VARCHAR , type_size = 32 , nullable = false , defValue = @DefValue(String = "0;0")),
                @Field(name = OWN_DATE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DOMINION_LORD , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SET_SIEGE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 1)),
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


    public CastleResource(String name){
        super(CastleResource.class);
        getSTAT_SET().set(NAME, name);
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
