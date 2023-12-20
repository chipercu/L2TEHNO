package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SkillTreesResource.*;

@Table(
        name = "skill_trees",
        primary_key = {CLASS_ID,SKILL_ID,LEVEL},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SKILL_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEVEL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 50 , name = NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SP , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MIN_LEVEL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = REP , defValue = "0"),
        }
)
public class SkillTreesResource extends DataBaseTable<SkillTreesResource> {

    public static final String CLASS_ID = "class_id";
    public static final String SKILL_ID = "skill_id";
    public static final String LEVEL = "level";
    public static final String NAME = "name";
    public static final String SP = "sp";
    public static final String MIN_LEVEL = "min_level";
    public static final String REP = "rep";

    public SkillTreesResource() {
        super(SkillTreesResource.class);
}

    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public Integer getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getSp() {
        return get(SP);
    }
    public Integer getMinLevel() {
        return get(MIN_LEVEL);
    }
    public Integer getRep() {
        return get(REP);
    }

    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setSkillId(Integer value) {
        set(SKILL_ID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setSp(Integer value) {
        set(SP, value);
    }
    public void setMinLevel(Integer value) {
        set(MIN_LEVEL, value);
    }
    public void setRep(Integer value) {
        set(REP, value);
    }

}
