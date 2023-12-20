package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.NpcskillsResource.*;

@Table(
        name = "npcskills",
        primary_key = {NPCID,SKILLID,LEVEL},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = NPCID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SKILLID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEVEL , defValue = "0"),
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
