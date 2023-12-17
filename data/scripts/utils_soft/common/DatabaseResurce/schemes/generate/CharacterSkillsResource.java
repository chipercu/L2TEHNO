package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSkillsResource.*;

@Table(
        name = "character_skills",
        primary_key = {CHAR_OBJ_ID,SKILL_ID,CLASS_INDEX},
        fields = {
                @Field(name = CHAR_OBJ_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_ID , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_LEVEL , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_NAME , data_type = "varchar"),
                @Field(name = CLASS_INDEX , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
        }
)
public class CharacterSkillsResource extends DataBaseTable<CharacterSkillsResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String SKILL_NAME = "skill_name";
    public static final String CLASS_INDEX = "class_index";

    public CharacterSkillsResource() {
super(CharacterSkillsResource.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Integer getSkillId() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public String getSkillName() {
        return get(SKILL_NAME, String.class);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX, Integer.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setSkillId(Integer value) {
        set(SKILL_ID, value);
    }
    public void setSkillLevel(Integer value) {
        set(SKILL_LEVEL, value);
    }
    public void setSkillName(String value) {
        set(SKILL_NAME, value);
    }
    public void setClassIndex(Integer value) {
        set(CLASS_INDEX, value);
    }

}
