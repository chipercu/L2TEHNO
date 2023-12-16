package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.CommunitybuffTable.*;

@Table(
        name = "communitybuff",
        fields = {
                @Field(name = SKILLID,
                @Field(name = SKILLLVL,
        }
)
public class CommunitybuffTable extends DataBaseTable<CommunitybuffTable> {

    public static final String SKILLID = "skillID";
    public static final String SKILLLVL = "skillLvl";
    public CommunitybuffTable() {
super(CommunitybuffTable.class);
}
    public IntegergetSkillID() {
        return get(SKILLID, Integer.class);
    }
    public IntegergetSkillLvl() {
        return get(SKILLLVL, Integer.class);
    }

    public void setSkillID() {
        set(SKILLID, value);
    }
    public void setSkillLvl() {
        set(SKILLLVL, value);
    }

}
