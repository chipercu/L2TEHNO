package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_skills_saveTable.*;

@Table(
        name = "character_skills_save",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = SKILL_ID,
                @Field(name = CLASS_INDEX,
                @Field(name = END_TIME,
                @Field(name = REUSE_DELAY_ORG,
        }
)
public class Character_skills_saveTable extends DataBaseTable<Character_skills_saveTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SKILL_ID = "skill_id";
    public static final String CLASS_INDEX = "class_index";
    public static final String END_TIME = "end_time";
    public static final String REUSE_DELAY_ORG = "reuse_delay_org";
    public Character_skills_saveTable() {
super(Character_skills_saveTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public ObjectgetSkill_id() {
        return get(SKILL_ID, Object.class);
    }
    public IntegergetClass_index() {
        return get(CLASS_INDEX, Integer.class);
    }
    public ObjectgetEnd_time() {
        return get(END_TIME, Object.class);
    }
    public IntegergetReuse_delay_org() {
        return get(REUSE_DELAY_ORG, Integer.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setSkill_id() {
        set(SKILL_ID, value);
    }
    public void setClass_index() {
        set(CLASS_INDEX, value);
    }
    public void setEnd_time() {
        set(END_TIME, value);
    }
    public void setReuse_delay_org() {
        set(REUSE_DELAY_ORG, value);
    }

}
