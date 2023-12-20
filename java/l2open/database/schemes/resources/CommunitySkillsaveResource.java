package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CommunitySkillsaveResource.*;

@Table(
        name = "community_skillsave",
        primary_key = {SCHAMEID},
        fields = {
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SCHAMEID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 250 , name = NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 1000 , name = SKILLS , defValue = ""),
        }
)
public class CommunitySkillsaveResource extends DataBaseTable<CommunitySkillsaveResource> {

    public static final String CHAR_ID = "charId";
    public static final String SCHAMEID = "schameid";
    public static final String NAME = "name";
    public static final String SKILLS = "skills";

    public CommunitySkillsaveResource() {
        super(CommunitySkillsaveResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getSchameid() {
        return get(SCHAMEID);
    }
    public String getName() {
        return get(NAME);
    }
    public String getSkills() {
        return get(SKILLS);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setSchameid(Integer value) {
        set(SCHAMEID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setSkills(String value) {
        set(SKILLS, value);
    }

}
