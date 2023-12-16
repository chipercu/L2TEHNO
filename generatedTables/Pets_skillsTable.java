package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Pets_skillsTable.*;

@Table(
        name = "pets_skills",
        fields = {
                @Field(name = TEMPLATEID,
                @Field(name = MINLVL,
                @Field(name = SKILLID,
                @Field(name = SKILLLVL,
        }
)
public class Pets_skillsTable extends DataBaseTable<Pets_skillsTable> {

    public static final String TEMPLATEID = "templateId";
    public static final String MINLVL = "minLvl";
    public static final String SKILLID = "skillId";
    public static final String SKILLLVL = "skillLvl";
    public Pets_skillsTable() {
super(Pets_skillsTable.class);
}
    public IntegergetTemplateId() {
        return get(TEMPLATEID, Integer.class);
    }
    public IntegergetMinLvl() {
        return get(MINLVL, Integer.class);
    }
    public IntegergetSkillId() {
        return get(SKILLID, Integer.class);
    }
    public IntegergetSkillLvl() {
        return get(SKILLLVL, Integer.class);
    }

    public void setTemplateId() {
        set(TEMPLATEID, value);
    }
    public void setMinLvl() {
        set(MINLVL, value);
    }
    public void setSkillId() {
        set(SKILLID, value);
    }
    public void setSkillLvl() {
        set(SKILLLVL, value);
    }

}
