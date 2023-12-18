package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LevelRewardsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "level_rewards",
        primary_key = {OBJECT_ID,CLASS_ID,CLASS_LEVEL},
        fields = {
                @Field(name = OBJECT_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CLASS_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CLASS_LEVEL , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class LevelRewardsResource extends DataBaseTable<LevelRewardsResource> {

    public static final String OBJECT_ID = "objectId";
    public static final String CLASS_ID = "classId";
    public static final String CLASS_LEVEL = "classLevel";

    public LevelRewardsResource() {
super(LevelRewardsResource.class);
}


    public LevelRewardsResource(String objectId,String classId,String classLevel){
        super(LevelRewardsResource.class);
        getSTAT_SET().set(OBJECT_ID, objectId);
        getSTAT_SET().set(CLASS_ID, classId);
        getSTAT_SET().set(CLASS_LEVEL, classLevel);
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
