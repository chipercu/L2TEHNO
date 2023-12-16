package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Skill_learnTable.*;

@Table(
        name = "skill_learn",
        fields = {
                @Field(name = NPC_ID,
                @Field(name = CLASS_ID,
        }
)
public class Skill_learnTable extends DataBaseTable<Skill_learnTable> {

    public static final String NPC_ID = "npc_id";
    public static final String CLASS_ID = "class_id";
    public Skill_learnTable() {
super(Skill_learnTable.class);
}
    public IntegergetNpc_id() {
        return get(NPC_ID, Integer.class);
    }
    public IntegergetClass_id() {
        return get(CLASS_ID, Integer.class);
    }

    public void setNpc_id() {
        set(NPC_ID, value);
    }
    public void setClass_id() {
        set(CLASS_ID, value);
    }

}
