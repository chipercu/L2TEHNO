package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Level_rewardsTable.*;

@Table(
        name = "level_rewards",
        fields = {
                @Field(name = OBJECTID,
                @Field(name = CLASSID,
                @Field(name = CLASSLEVEL,
        }
)
public class Level_rewardsTable extends DataBaseTable<Level_rewardsTable> {

    public static final String OBJECTID = "objectId";
    public static final String CLASSID = "classId";
    public static final String CLASSLEVEL = "classLevel";
    public Level_rewardsTable() {
super(Level_rewardsTable.class);
}
    public IntegergetObjectId() {
        return get(OBJECTID, Integer.class);
    }
    public IntegergetClassId() {
        return get(CLASSID, Integer.class);
    }
    public IntegergetClassLevel() {
        return get(CLASSLEVEL, Integer.class);
    }

    public void setObjectId() {
        set(OBJECTID, value);
    }
    public void setClassId() {
        set(CLASSID, value);
    }
    public void setClassLevel() {
        set(CLASSLEVEL, value);
    }

}
