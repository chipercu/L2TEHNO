package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunitybuffTable.*;

@Table(
        name = "communitybuff",
        fields = {
                @Field(name = SKILL_ID),
                @Field(name = SKILL_LVL),
        }
)
public class CommunitybuffTable extends DataBaseTable<CommunitybuffTable> {

    public static final String SKILL_ID = "skillID";
    public static final String SKILL_LVL = "skillLvl";

    public CommunitybuffTable() {
super(CommunitybuffTable.class);
}

    public Integer getSkillID() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getSkillLvl() {
        return get(SKILL_LVL, Integer.class);
    }

    public void setSkillID(Integer value) {
        set(SKILL_ID, value);
    }
    public void setSkillLvl(Integer value) {
        set(SKILL_LVL, value);
    }

}
