package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Clan_skillsTable.*;

@Table(
        name = "clan_skills",
        fields = {
                @Field(name = CLAN_ID,
                @Field(name = SKILL_ID,
                @Field(name = SKILL_LEVEL,
                @Field(name = SKILL_NAME,
                @Field(name = SQUAD_INDEX,
        }
)
public class Clan_skillsTable extends DataBaseTable<Clan_skillsTable> {

    public static final String CLAN_ID = "clan_id";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String SKILL_NAME = "skill_name";
    public static final String SQUAD_INDEX = "squad_index";
    public Clan_skillsTable() {
super(Clan_skillsTable.class);
}
    public IntegergetClan_id() {
        return get(CLAN_ID, Integer.class);
    }
    public IntegergetSkill_id() {
        return get(SKILL_ID, Integer.class);
    }
    public IntegergetSkill_level() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public StringgetSkill_name() {
        return get(SKILL_NAME, String.class);
    }
    public IntegergetSquad_index() {
        return get(SQUAD_INDEX, Integer.class);
    }

    public void setClan_id() {
        set(CLAN_ID, value);
    }
    public void setSkill_id() {
        set(SKILL_ID, value);
    }
    public void setSkill_level() {
        set(SKILL_LEVEL, value);
    }
    public void setSkill_name() {
        set(SKILL_NAME, value);
    }
    public void setSquad_index() {
        set(SQUAD_INDEX, value);
    }

}
