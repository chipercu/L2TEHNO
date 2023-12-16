package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunitySkillsaveTable.*;

@Table(
        name = "community_skillsave",
        fields = {
                @Field(name = CHAR_ID),
                @Field(name = SCHAMEID),
                @Field(name = NAME),
                @Field(name = SKILLS),
        }
)
public class CommunitySkillsaveTable extends DataBaseTable<CommunitySkillsaveTable> {

    public static final String CHAR_ID = "charId";
    public static final String SCHAMEID = "schameid";
    public static final String NAME = "name";
    public static final String SKILLS = "skills";

    public CommunitySkillsaveTable() {
super(CommunitySkillsaveTable.class);
}

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getSchameid() {
        return get(SCHAMEID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getSkills() {
        return get(SKILLS, String.class);
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
