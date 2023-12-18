package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.NpcskillsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "npcskills",
        primary_key = {NPCID,SKILLID,LEVEL},
        fields = {
                @Field(name = NPCID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILLID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LEVEL , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class NpcskillsResource extends DataBaseTable<NpcskillsResource> {

    public static final String NPCID = "npcid";
    public static final String SKILLID = "skillid";
    public static final String LEVEL = "level";

    public NpcskillsResource() {
super(NpcskillsResource.class);
}


    public NpcskillsResource(String npcid,String skillid,String level){
        super(NpcskillsResource.class);
        getSTAT_SET().set(NPCID, npcid);
        getSTAT_SET().set(SKILLID, skillid);
        getSTAT_SET().set(LEVEL, level);
    }

    public Integer getNpcid() {
        return get(NPCID, Integer.class);
    }
    public Integer getSkillid() {
        return get(SKILLID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }

    public void setNpcid(Integer value) {
        set(NPCID, value);
    }
    public void setSkillid(Integer value) {
        set(SKILLID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }

}
