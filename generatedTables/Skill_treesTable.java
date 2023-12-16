package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Skill_treesTable.*;

@Table(
        name = "skill_trees",
        fields = {
                @Field(name = CLASS_ID,
                @Field(name = SKILL_ID,
                @Field(name = LEVEL,
                @Field(name = NAME,
                @Field(name = SP,
                @Field(name = MIN_LEVEL,
                @Field(name = REP,
        }
)
public class Skill_treesTable extends DataBaseTable<Skill_treesTable> {

    public static final String CLASS_ID = "class_id";
    public static final String SKILL_ID = "skill_id";
    public static final String LEVEL = "level";
    public static final String NAME = "name";
    public static final String SP = "sp";
    public static final String MIN_LEVEL = "min_level";
    public static final String REP = "rep";
    public Skill_treesTable() {
super(Skill_treesTable.class);
}
    public IntegergetClass_id() {
        return get(CLASS_ID, Integer.class);
    }
    public IntegergetSkill_id() {
        return get(SKILL_ID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetSp() {
        return get(SP, Integer.class);
    }
    public IntegergetMin_level() {
        return get(MIN_LEVEL, Integer.class);
    }
    public IntegergetRep() {
        return get(REP, Integer.class);
    }

    public void setClass_id() {
        set(CLASS_ID, value);
    }
    public void setSkill_id() {
        set(SKILL_ID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setSp() {
        set(SP, value);
    }
    public void setMin_level() {
        set(MIN_LEVEL, value);
    }
    public void setRep() {
        set(REP, value);
    }

}
