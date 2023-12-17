package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SkillLearnResource.*;

@Table(
        name = "skill_learn",
        primary_key = {NPC_ID,CLASS_ID},
        fields = {
                @Field(name = NPC_ID , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = CLASS_ID , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
        }
)
public class SkillLearnResource extends DataBaseTable<SkillLearnResource> {

    public static final String NPC_ID = "npc_id";
    public static final String CLASS_ID = "class_id";

    public SkillLearnResource() {
super(SkillLearnResource.class);
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
