package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.NpcskillsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "npcskills",
        primary_key = {NPCID,SKILLID,LEVEL},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPCID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SKILLID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEVEL , defValue = "0"),
        }
)
public class NpcskillsResource extends DataBaseTable<NpcskillsResource> {

    public static final String NPCID = "npcid";
    public static final String SKILLID = "skillid";
    public static final String LEVEL = "level";

    public NpcskillsResource() {
        super(NpcskillsResource.class);
}

    public Integer getNpcid() {
        return get(NPCID);
    }
    public Integer getSkillid() {
        return get(SKILLID);
    }
    public Integer getLevel() {
        return get(LEVEL);
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
