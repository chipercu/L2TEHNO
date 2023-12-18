package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunitybuffResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "communitybuff",
        primary_key = {},
        fields = {
                @Field(name = SKILL_ID , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = SKILL_LVL , type = INT , nullable = true , defValue = @DefValue(Integer = 1)),
        }
)
public class CommunitybuffResource extends DataBaseTable<CommunitybuffResource> {

    public static final String SKILL_ID = "skillID";
    public static final String SKILL_LVL = "skillLvl";

    public CommunitybuffResource() {
super(CommunitybuffResource.class);
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
