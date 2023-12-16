package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LevelRewardsTable.*;

@Table(
        name = "level_rewards",
        fields = {
                @Field(name = OBJECT_ID),
                @Field(name = CLASS_ID),
                @Field(name = CLASS_LEVEL),
        }
)
public class LevelRewardsTable extends DataBaseTable<LevelRewardsTable> {

    public static final String OBJECT_ID = "objectId";
    public static final String CLASS_ID = "classId";
    public static final String CLASS_LEVEL = "classLevel";

    public LevelRewardsTable() {
super(LevelRewardsTable.class);
}

    public Integer getObjectId() {
        return get(OBJECT_ID, Integer.class);
    }
    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public Integer getClassLevel() {
        return get(CLASS_LEVEL, Integer.class);
    }

    public void setObjectId(Integer value) {
        set(OBJECT_ID, value);
    }
    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setClassLevel(Integer value) {
        set(CLASS_LEVEL, value);
    }

}
