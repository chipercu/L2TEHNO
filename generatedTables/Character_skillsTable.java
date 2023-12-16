package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_skillsTable.*;

@Table(
        name = "character_skills",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = SKILL_ID,
                @Field(name = SKILL_LEVEL,
                @Field(name = SKILL_NAME,
                @Field(name = CLASS_INDEX,
        }
)
public class Character_skillsTable extends DataBaseTable<Character_skillsTable> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String SKILL_NAME = "skill_name";
    public static final String CLASS_INDEX = "class_index";
    public Character_skillsTable() {
super(Character_skillsTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public IntegergetSkill_id() {
        return get(SKILL_ID, Integer.class);
    }
    public IntegergetSkill_level() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public StringgetSkill_name() {
        return get(SKILL_NAME, String.class);
    }
    public IntegergetClass_index() {
        return get(CLASS_INDEX, Integer.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setSkill_id() {
        set(SKILL_ID, value);
    }
    public void setSkill_level() {
        set(SKILL_LEVEL, value);
    }
    public void setSkill_name() {
        set(SKILL_NAME, value);
    }
    public void setClass_index() {
        set(CLASS_INDEX, value);
    }

}
