package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SkillLearnTable.*;

@Table(
        name = "skill_learn",
        fields = {
                @Field(name = NPC_ID),
                @Field(name = CLASS_ID),
        }
)
public class SkillLearnTable extends DataBaseTable<SkillLearnTable> {

    public static final String NPC_ID = "npc_id";
    public static final String CLASS_ID = "class_id";

    public SkillLearnTable() {
super(SkillLearnTable.class);
}

    public Integer getNpcId() {
        return get(NPC_ID, Integer.class);
    }
    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }

    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }

}
