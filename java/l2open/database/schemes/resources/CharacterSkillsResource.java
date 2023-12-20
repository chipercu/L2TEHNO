package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterSkillsResource.*;

@Table(
        name = "character_skills",
        primary_key = {CHAR_OBJ_ID,SKILL_ID,CLASS_INDEX},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = SKILL_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = SKILL_LEVEL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 100 , name = SKILL_NAME , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = CLASS_INDEX , defValue = "0"),
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
