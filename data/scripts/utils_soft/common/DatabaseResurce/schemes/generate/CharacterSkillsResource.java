package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSkillsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_skills",
        primary_key = {CHAR_OBJ_ID,SKILL_ID,CLASS_INDEX},
        fields = {
                @Field(name = CHAR_OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_LEVEL , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_NAME , type = VARCHAR , type_size = 100 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = CLASS_INDEX , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public CharacterSkillsResource(String char_obj_id,String skill_id,String class_index){
        super(CharacterSkillsResource.class);
        getSTAT_SET().set(CHAR_OBJ_ID, char_obj_id);
        getSTAT_SET().set(SKILL_ID, skill_id);
        getSTAT_SET().set(CLASS_INDEX, class_index);
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
