package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.LevelRewardsResource.*;

@Table(
        name = "level_rewards",
        primary_key = {OBJECT_ID,CLASS_ID,CLASS_LEVEL},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJECT_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLASS_LEVEL , defValue = "0"),
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
