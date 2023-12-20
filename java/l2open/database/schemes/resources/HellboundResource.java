package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.HellboundResource.*;

@Table(
        name = "hellbound",
        primary_key = {NAME,HB_POINTS,HB_LEVEL,UNLOCKED,DUMMY},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = NAME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = HB_POINTS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = HB_LEVEL , defValue = "1"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = UNLOCKED , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = DUMMY , defValue = "0"),
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
        return get(NAME);
    }
    public Integer getHbPoints() {
        return get(HB_POINTS);
    }
    public Integer getHbLevel() {
        return get(HB_LEVEL);
    }
    public Integer getUnlocked() {
        return get(UNLOCKED);
    }
    public Integer getDummy() {
        return get(DUMMY);
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
