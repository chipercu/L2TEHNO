package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Community_skillsaveTable.*;

@Table(
        name = "community_skillsave",
        fields = {
                @Field(name = CHARID,
                @Field(name = SCHAMEID,
                @Field(name = NAME,
                @Field(name = SKILLS,
        }
)
public class Community_skillsaveTable extends DataBaseTable<Community_skillsaveTable> {

    public static final String CHARID = "charId";
    public static final String SCHAMEID = "schameid";
    public static final String NAME = "name";
    public static final String SKILLS = "skills";
    public Community_skillsaveTable() {
super(Community_skillsaveTable.class);
}
    public IntegergetCharId() {
        return get(CHARID, Integer.class);
    }
    public IntegergetSchameid() {
        return get(SCHAMEID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetSkills() {
        return get(SKILLS, String.class);
    }

    public void setCharId() {
        set(CHARID, value);
    }
    public void setSchameid() {
        set(SCHAMEID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setSkills() {
        set(SKILLS, value);
    }

}
