package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ClanSkillsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clan_skills",
        primary_key = {CLAN_ID,SKILL_ID,SQUAD_INDEX},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLAN_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = SKILL_ID , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SKILL_LEVEL , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 26 , name = SKILL_NAME , defValue = "NULL"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = SQUAD_INDEX , defValue = "-1"),
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

    public Integer getClanId() {
        return get(CLAN_ID);
    }
    public Integer getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL);
    }
    public String getSkillName() {
        return get(SKILL_NAME);
    }
    public Integer getSquadIndex() {
        return get(SQUAD_INDEX);
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
