package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.PetsSkillsResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "pets_skills",
        primary_key = {TEMPLATE_ID,SKILL_ID,SKILL_LVL},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = TEMPLATE_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MIN_LVL , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SKILL_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SKILL_LVL , defValue = "0"),
        }
)
public class PetsSkillsResource extends DataBaseTable<PetsSkillsResource> {

    public static final String TEMPLATE_ID = "templateId";
    public static final String MIN_LVL = "minLvl";
    public static final String SKILL_ID = "skillId";
    public static final String SKILL_LVL = "skillLvl";

    public PetsSkillsResource() {
        super(PetsSkillsResource.class);
}

    public Integer getTemplateId() {
        return get(TEMPLATE_ID);
    }
    public Integer getMinLvl() {
        return get(MIN_LVL);
    }
    public Integer getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getSkillLvl() {
        return get(SKILL_LVL);
    }

    public void setTemplateId(Integer value) {
        set(TEMPLATE_ID, value);
    }
    public void setMinLvl(Integer value) {
        set(MIN_LVL, value);
    }
    public void setSkillId(Integer value) {
        set(SKILL_ID, value);
    }
    public void setSkillLvl(Integer value) {
        set(SKILL_LVL, value);
    }

}
