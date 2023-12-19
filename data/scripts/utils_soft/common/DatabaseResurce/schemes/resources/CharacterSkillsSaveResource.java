package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterSkillsSaveResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_skills_save",
        primary_key = {CHAR_OBJ_ID,SKILL_ID,CLASS_INDEX},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = SKILL_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CLASS_INDEX , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = END_TIME , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = REUSE_DELAY_ORG , defValue = "0"),
        }
)
public class CharacterSkillsSaveResource extends DataBaseTable<CharacterSkillsSaveResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String SKILL_ID = "skill_id";
    public static final String CLASS_INDEX = "class_index";
    public static final String END_TIME = "end_time";
    public static final String REUSE_DELAY_ORG = "reuse_delay_org";

    public CharacterSkillsSaveResource() {
        super(CharacterSkillsSaveResource.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public Long getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX);
    }
    public Long getEndTime() {
        return get(END_TIME);
    }
    public Integer getReuseDelayOrg() {
        return get(REUSE_DELAY_ORG);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setSkillId(Long value) {
        set(SKILL_ID, value);
    }
    public void setClassIndex(Integer value) {
        set(CLASS_INDEX, value);
    }
    public void setEndTime(Long value) {
        set(END_TIME, value);
    }
    public void setReuseDelayOrg(Integer value) {
        set(REUSE_DELAY_ORG, value);
    }

}
