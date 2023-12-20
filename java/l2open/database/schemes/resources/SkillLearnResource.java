package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.SkillLearnResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

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
