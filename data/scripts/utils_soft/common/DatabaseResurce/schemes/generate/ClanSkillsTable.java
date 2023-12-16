package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanSkillsTable.*;

@Table(
        name = "clan_skills",
        fields = {
                @Field(name = CLAN_ID),
                @Field(name = SKILL_ID),
                @Field(name = SKILL_LEVEL),
                @Field(name = SKILL_NAME),
                @Field(name = SQUAD_INDEX),
        }
)
public class ClanSkillsTable extends DataBaseTable<ClanSkillsTable> {

    public static final String CLAN_ID = "clan_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String SKILL_NAME = "skill_name";
    public static final String SQUAD_INDEX = "squad_index";

    public ClanSkillsTable() {
super(ClanSkillsTable.class);
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
