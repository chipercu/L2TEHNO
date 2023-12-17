package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterSkillsSaveResource.*;

@Table(
        name = "character_skills_save",
        primary_key = {CHAR_OBJ_ID,SKILL_ID,CLASS_INDEX},
        fields = {
                @Field(name = CHAR_OBJ_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_ID , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = CLASS_INDEX , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = END_TIME , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = REUSE_DELAY_ORG , data_type = "int" , defValue = @DefValue(Integer = 0)),
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
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Long getSkillId() {
        return get(SKILL_ID, Long.class);
    }
    public Integer getClassIndex() {
        return get(CLASS_INDEX, Integer.class);
    }
    public Long getEndTime() {
        return get(END_TIME, Long.class);
    }
    public Integer getReuseDelayOrg() {
        return get(REUSE_DELAY_ORG, Integer.class);
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
