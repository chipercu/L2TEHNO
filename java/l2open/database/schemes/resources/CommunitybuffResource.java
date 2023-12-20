package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CommunitybuffResource.*;

@Table(
        name = "communitybuff",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = SKILL_ID , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = SKILL_LVL , defValue = "1"),
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
