package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SkillLearnResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "skill_learn",
        primary_key = {NPC_ID,CLASS_ID},
        fields = {
                @Field(name = NPC_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CLASS_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class SkillLearnResource extends DataBaseTable<SkillLearnResource> {

    public static final String NPC_ID = "npc_id";
    public static final String CLASS_ID = "class_id";

    public SkillLearnResource() {
super(SkillLearnResource.class);
}


    public SkillLearnResource(String npc_id,String class_id){
        super(SkillLearnResource.class);
        getSTAT_SET().set(NPC_ID, npc_id);
        getSTAT_SET().set(CLASS_ID, class_id);
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
