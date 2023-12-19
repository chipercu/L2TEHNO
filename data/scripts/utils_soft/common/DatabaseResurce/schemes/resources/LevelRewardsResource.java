package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.LevelRewardsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "level_rewards",
        primary_key = {OBJECT_ID,CLASS_ID,CLASS_LEVEL},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJECT_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLASS_LEVEL , defValue = "0"),
        }
)
public class LevelRewardsResource extends DataBaseTable<LevelRewardsResource> {

    public static final String OBJECT_ID = "objectId";
    public static final String CLASS_ID = "classId";
    public static final String CLASS_LEVEL = "classLevel";

    public LevelRewardsResource() {
        super(LevelRewardsResource.class);
}

    public Integer getObjectId() {
        return get(OBJECT_ID);
    }
    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public Integer getClassLevel() {
        return get(CLASS_LEVEL);
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
