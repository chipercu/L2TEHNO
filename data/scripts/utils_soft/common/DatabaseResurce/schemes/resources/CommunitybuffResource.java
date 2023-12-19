package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CommunitybuffResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "communitybuff",
        primary_key = {},
        fields = {
                @Column(is_null = YES , type = INT , type_size = 0 , name = SKILL_ID , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = SKILL_LVL , defValue = "1"),
        }
)
public class CommunitybuffResource extends DataBaseTable<CommunitybuffResource> {

    public static final String SKILL_ID = "skillID";
    public static final String SKILL_LVL = "skillLvl";

    public CommunitybuffResource() {
        super(CommunitybuffResource.class);
}

    public Integer getSkillID() {
        return get(SKILL_ID);
    }
    public Integer getSkillLvl() {
        return get(SKILL_LVL);
    }

    public void setSkillID(Integer value) {
        set(SKILL_ID, value);
    }
    public void setSkillLvl(Integer value) {
        set(SKILL_LVL, value);
    }

}
