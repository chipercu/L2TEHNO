package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HellboundResource.*;

@Table(
        name = "hellbound",
        primary_key = {NAME,HB_POINTS,HB_LEVEL,UNLOCKED,DUMMY},
        fields = {
                @Field(name = NAME , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = HB_POINTS , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = HB_LEVEL , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = UNLOCKED , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = DUMMY , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class HellboundResource extends DataBaseTable<HellboundResource> {

    public static final String NAME = "name";
    public static final String HB_POINTS = "hb_points";
    public static final String HB_LEVEL = "hb_level";
    public static final String UNLOCKED = "unlocked";
    public static final String DUMMY = "dummy";

    public HellboundResource() {
super(HellboundResource.class);
}

    public Integer getName() {
        return get(NAME, Integer.class);
    }
    public Integer getHbPoints() {
        return get(HB_POINTS, Integer.class);
    }
    public Integer getHbLevel() {
        return get(HB_LEVEL, Integer.class);
    }
    public Integer getUnlocked() {
        return get(UNLOCKED, Integer.class);
    }
    public Integer getDummy() {
        return get(DUMMY, Integer.class);
    }

    public void setName(Integer value) {
        set(NAME, value);
    }
    public void setHbPoints(Integer value) {
        set(HB_POINTS, value);
    }
    public void setHbLevel(Integer value) {
        set(HB_LEVEL, value);
    }
    public void setUnlocked(Integer value) {
        set(UNLOCKED, value);
    }
    public void setDummy(Integer value) {
        set(DUMMY, value);
    }

}
