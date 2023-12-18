package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanSkillsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clan_skills",
        primary_key = {CLAN_ID,SKILL_ID,SQUAD_INDEX},
        fields = {
                @Field(name = CLAN_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_LEVEL , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SKILL_NAME , type = VARCHAR , type_size = 26 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = SQUAD_INDEX , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = -1)),
        }
)
public class ClanSkillsResource extends DataBaseTable<ClanSkillsResource> {

    public static final String CLAN_ID = "clan_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String SKILL_NAME = "skill_name";
    public static final String SQUAD_INDEX = "squad_index";

    public ClanSkillsResource() {
super(ClanSkillsResource.class);
}


    public ClanSkillsResource(String clan_id,String skill_id,String squad_index){
        super(ClanSkillsResource.class);
        getSTAT_SET().set(CLAN_ID, clan_id);
        getSTAT_SET().set(SKILL_ID, skill_id);
        getSTAT_SET().set(SQUAD_INDEX, squad_index);
    }

    public Integer getClanId() {
        return get(CLAN_ID, Integer.class);
    }
    public Integer getSkillId() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public String getSkillName() {
        return get(SKILL_NAME, String.class);
    }
    public Integer getSquadIndex() {
        return get(SQUAD_INDEX, Integer.class);
    }

    public void setClanId(Integer value) {
        set(CLAN_ID, value);
    }
    public void setSkillId(Integer value) {
        set(SKILL_ID, value);
    }
    public void setSkillLevel(Integer value) {
        set(SKILL_LEVEL, value);
    }
    public void setSkillName(String value) {
        set(SKILL_NAME, value);
    }
    public void setSquadIndex(Integer value) {
        set(SQUAD_INDEX, value);
    }

}
