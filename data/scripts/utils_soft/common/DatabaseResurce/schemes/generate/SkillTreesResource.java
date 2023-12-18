package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SkillTreesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "skill_trees",
        primary_key = {CLASS_ID,SKILL_ID,LEVEL},
        fields = {
                @Field(name = CLASS_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LEVEL , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , type = VARCHAR , type_size = 50 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = SP , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MIN_LEVEL , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = REP , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public SkillTreesResource(String class_id,String skill_id,String level){
        super(SkillTreesResource.class);
        getSTAT_SET().set(CLASS_ID, class_id);
        getSTAT_SET().set(SKILL_ID, skill_id);
        getSTAT_SET().set(LEVEL, level);
    }

    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public Integer getSkillId() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getSp() {
        return get(SP, Integer.class);
    }
    public Integer getMinLevel() {
        return get(MIN_LEVEL, Integer.class);
    }
    public Integer getRep() {
        return get(REP, Integer.class);
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
