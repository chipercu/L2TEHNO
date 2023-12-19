package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SkillLearnResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "skill_learn",
        primary_key = {NPC_ID,CLASS_ID},
        fields = {
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = CLASS_ID , defValue = "0"),
        }
)
public class SkillLearnResource extends DataBaseTable<SkillLearnResource> {

    public static final String NPC_ID = "npc_id";
    public static final String CLASS_ID = "class_id";

    public SkillLearnResource() {
        super(SkillLearnResource.class);
}

    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public Integer getClassId() {
        return get(CLASS_ID);
    }

    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }

}
