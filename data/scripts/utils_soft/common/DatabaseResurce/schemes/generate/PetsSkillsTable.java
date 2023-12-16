package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.PetsSkillsTable.*;

@Table(
        name = "pets_skills",
        fields = {
                @Field(name = TEMPLATE_ID),
                @Field(name = MIN_LVL),
                @Field(name = SKILL_ID),
                @Field(name = SKILL_LVL),
        }
)
public class PetsSkillsTable extends DataBaseTable<PetsSkillsTable> {

    public static final String TEMPLATE_ID = "templateId";
    public static final String MIN_LVL = "minLvl";
    public static final String SKILL_ID = "skillId";
    public static final String SKILL_LVL = "skillLvl";

    public PetsSkillsTable() {
super(PetsSkillsTable.class);
}

    public Integer getTemplateId() {
        return get(TEMPLATE_ID, Integer.class);
    }
    public Integer getMinLvl() {
        return get(MIN_LVL, Integer.class);
    }
    public Integer getSkillId() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getSkillLvl() {
        return get(SKILL_LVL, Integer.class);
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
