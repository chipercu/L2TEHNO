package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.NpcskillsTable.*;

@Table(
        name = "npcskills",
        fields = {
                @Field(name = NPCID,
                @Field(name = SKILLID,
                @Field(name = LEVEL,
        }
)
public class NpcskillsTable extends DataBaseTable<NpcskillsTable> {

    public static final String NPCID = "npcid";
    public static final String SKILLID = "skillid";
    public static final String LEVEL = "level";
    public NpcskillsTable() {
super(NpcskillsTable.class);
}
    public IntegergetNpcid() {
        return get(NPCID, Integer.class);
    }
    public IntegergetSkillid() {
        return get(SKILLID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }

    public void setNpcid() {
        set(NPCID, value);
    }
    public void setSkillid() {
        set(SKILLID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }

}
