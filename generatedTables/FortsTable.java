package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.FortsTable.*;

@Table(
        name = "forts",
        fields = {
                @Field(name = ID,
                @Field(name = NAME,
                @Field(name = LASTSIEGEDATE,
                @Field(name = SIEGEDATE,
                @Field(name = SKILLS,
                @Field(name = OWNDATE,
                @Field(name = STATE,
                @Field(name = CASTLEID,
                @Field(name = MERCENARYID,
                @Field(name = MERCENARYLOC,
        }
)
public class FortsTable extends DataBaseTable<FortsTable> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String LASTSIEGEDATE = "lastSiegeDate";
    public static final String SIEGEDATE = "siegeDate";
    public static final String SKILLS = "skills";
    public static final String OWNDATE = "ownDate";
    public static final String STATE = "state";
    public static final String CASTLEID = "castleId";
    public static final String MERCENARYID = "mercenaryId";
    public static final String MERCENARYLOC = "mercenaryLoc";
    public FortsTable() {
super(FortsTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetLastSiegeDate() {
        return get(LASTSIEGEDATE, Integer.class);
    }
    public IntegergetSiegeDate() {
        return get(SIEGEDATE, Integer.class);
    }
    public StringgetSkills() {
        return get(SKILLS, String.class);
    }
    public IntegergetOwnDate() {
        return get(OWNDATE, Integer.class);
    }
    public IntegergetState() {
        return get(STATE, Integer.class);
    }
    public IntegergetCastleId() {
        return get(CASTLEID, Integer.class);
    }
    public IntegergetMercenaryId() {
        return get(MERCENARYID, Integer.class);
    }
    public StringgetMercenaryLoc() {
        return get(MERCENARYLOC, String.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setLastSiegeDate() {
        set(LASTSIEGEDATE, value);
    }
    public void setSiegeDate() {
        set(SIEGEDATE, value);
    }
    public void setSkills() {
        set(SKILLS, value);
    }
    public void setOwnDate() {
        set(OWNDATE, value);
    }
    public void setState() {
        set(STATE, value);
    }
    public void setCastleId() {
        set(CASTLEID, value);
    }
    public void setMercenaryId() {
        set(MERCENARYID, value);
    }
    public void setMercenaryLoc() {
        set(MERCENARYLOC, value);
    }

}
