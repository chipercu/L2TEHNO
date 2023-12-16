package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.FortsTable.*;

@Table(
        name = "forts",
        fields = {
                @Field(name = ID),
                @Field(name = NAME),
                @Field(name = LAST_SIEGE_DATE),
                @Field(name = SIEGE_DATE),
                @Field(name = SKILLS),
                @Field(name = OWN_DATE),
                @Field(name = STATE),
                @Field(name = CASTLE_ID),
                @Field(name = MERCENARY_ID),
                @Field(name = MERCENARY_LOC),
        }
)
public class FortsTable extends DataBaseTable<FortsTable> {

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

    public FortsTable() {
super(FortsTable.class);
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
