package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterSkillsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_skills",
        primary_key = {CHAR_OBJ_ID,SKILL_ID,CLASS_INDEX},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = SKILL_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = SKILL_LEVEL , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 100 , name = SKILL_NAME , defValue = "NULL"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CLASS_INDEX , defValue = "0"),
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
        return get(CHAR_OBJ_ID);
    }
    public Integer getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL);
    }
    public String getSkillName() {
        return get(SKILL_NAME);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX);
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
