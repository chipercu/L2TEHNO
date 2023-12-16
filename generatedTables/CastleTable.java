package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.CastleTable.*;

@Table(
        name = "castle",
        fields = {
                @Field(name = ID,
                @Field(name = NAME,
                @Field(name = TAXPERCENT,
                @Field(name = TREASURY,
                @Field(name = SIEGEDATE,
                @Field(name = SIEGEDAYOFWEEK,
                @Field(name = SIEGEHOUROFDAY,
                @Field(name = TOWNID,
                @Field(name = SKILLS,
                @Field(name = FLAGS,
                @Field(name = OWNDATE,
                @Field(name = DOMINIONLORD,
                @Field(name = SETSIEGE,
        }
)
public class CastleTable extends DataBaseTable<CastleTable> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TAXPERCENT = "taxPercent";
    public static final String TREASURY = "treasury";
    public static final String SIEGEDATE = "siegeDate";
    public static final String SIEGEDAYOFWEEK = "siegeDayOfWeek";
    public static final String SIEGEHOUROFDAY = "siegeHourOfDay";
    public static final String TOWNID = "townId";
    public static final String SKILLS = "skills";
    public static final String FLAGS = "flags";
    public static final String OWNDATE = "ownDate";
    public static final String DOMINIONLORD = "dominionLord";
    public static final String SETSIEGE = "setSiege";
    public CastleTable() {
super(CastleTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetTaxPercent() {
        return get(TAXPERCENT, Integer.class);
    }
    public ObjectgetTreasury() {
        return get(TREASURY, Object.class);
    }
    public IntegergetSiegeDate() {
        return get(SIEGEDATE, Integer.class);
    }
    public IntegergetSiegeDayOfWeek() {
        return get(SIEGEDAYOFWEEK, Integer.class);
    }
    public IntegergetSiegeHourOfDay() {
        return get(SIEGEHOUROFDAY, Integer.class);
    }
    public IntegergetTownId() {
        return get(TOWNID, Integer.class);
    }
    public StringgetSkills() {
        return get(SKILLS, String.class);
    }
    public StringgetFlags() {
        return get(FLAGS, String.class);
    }
    public IntegergetOwnDate() {
        return get(OWNDATE, Integer.class);
    }
    public IntegergetDominionLord() {
        return get(DOMINIONLORD, Integer.class);
    }
    public IntegergetSetSiege() {
        return get(SETSIEGE, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setTaxPercent() {
        set(TAXPERCENT, value);
    }
    public void setTreasury() {
        set(TREASURY, value);
    }
    public void setSiegeDate() {
        set(SIEGEDATE, value);
    }
    public void setSiegeDayOfWeek() {
        set(SIEGEDAYOFWEEK, value);
    }
    public void setSiegeHourOfDay() {
        set(SIEGEHOUROFDAY, value);
    }
    public void setTownId() {
        set(TOWNID, value);
    }
    public void setSkills() {
        set(SKILLS, value);
    }
    public void setFlags() {
        set(FLAGS, value);
    }
    public void setOwnDate() {
        set(OWNDATE, value);
    }
    public void setDominionLord() {
        set(DOMINIONLORD, value);
    }
    public void setSetSiege() {
        set(SETSIEGE, value);
    }

}
