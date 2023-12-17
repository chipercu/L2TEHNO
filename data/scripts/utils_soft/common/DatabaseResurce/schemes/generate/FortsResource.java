package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.FortsResource.*;

@Table(
        name = "forts",
        primary_key = {ID},
        fields = {
                @Field(name = ID , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , data_type = "varchar"),
                @Field(name = LAST_SIEGE_DATE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = SIEGE_DATE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILLS , data_type = "varchar" , defValue = @DefValue(String = "0;0")),
                @Field(name = OWN_DATE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = STATE , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = CASTLE_ID , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = MERCENARY_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = MERCENARY_LOC , data_type = "varchar"),
        }
)
public class FortsResource extends DataBaseTable<FortsResource> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String LAST_SIEGE_DATE = "lastSiegeDate";
    public static final String SIEGE_DATE = "siegeDate";
    public static final String SKILLS = "skills";
    public static final String OWN_DATE = "ownDate";
    public static final String STATE = "state";
    public static final String CASTLE_ID = "castleId";
    public static final String MERCENARY_ID = "mercenaryId";
    public static final String MERCENARY_LOC = "mercenaryLoc";

    public FortsResource() {
super(FortsResource.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getLastSiegeDate() {
        return get(LAST_SIEGE_DATE, Integer.class);
    }
    public Integer getSiegeDate() {
        return get(SIEGE_DATE, Integer.class);
    }
    public String getSkills() {
        return get(SKILLS, String.class);
    }
    public Integer getOwnDate() {
        return get(OWN_DATE, Integer.class);
    }
    public Integer getState() {
        return get(STATE, Integer.class);
    }
    public Integer getCastleId() {
        return get(CASTLE_ID, Integer.class);
    }
    public Integer getMercenaryId() {
        return get(MERCENARY_ID, Integer.class);
    }
    public String getMercenaryLoc() {
        return get(MERCENARY_LOC, String.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setLastSiegeDate(Integer value) {
        set(LAST_SIEGE_DATE, value);
    }
    public void setSiegeDate(Integer value) {
        set(SIEGE_DATE, value);
    }
    public void setSkills(String value) {
        set(SKILLS, value);
    }
    public void setOwnDate(Integer value) {
        set(OWN_DATE, value);
    }
    public void setState(Integer value) {
        set(STATE, value);
    }
    public void setCastleId(Integer value) {
        set(CASTLE_ID, value);
    }
    public void setMercenaryId(Integer value) {
        set(MERCENARY_ID, value);
    }
    public void setMercenaryLoc(String value) {
        set(MERCENARY_LOC, value);
    }

}
