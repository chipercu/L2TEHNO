package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunitySkillsaveResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "community_skillsave",
        primary_key = {SCHAMEID},
        fields = {
                @Field(name = CHAR_ID , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = SCHAMEID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = NAME , type = VARCHAR , type_size = 250 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = SKILLS , type = VARCHAR , type_size = 1000 , nullable = true , defValue = @DefValue(String = "")),
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


    public CommunitySkillsaveResource(String schameid){
        super(CommunitySkillsaveResource.class);
        getSTAT_SET().set(SCHAMEID, schameid);
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
