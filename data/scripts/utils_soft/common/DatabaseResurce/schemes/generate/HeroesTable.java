package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HeroesTable.*;

@Table(
        name = "heroes",
        fields = {
                @Field(name = CHAR_ID),
                @Field(name = COUNT),
                @Field(name = PLAYED),
                @Field(name = ACTIVE),
                @Field(name = MESSAGE),
        }
)
public class HeroesTable extends DataBaseTable<HeroesTable> {

    public static final String CHAR_ID = "char_id";
    public static final String COUNT = "count";
    public static final String PLAYED = "played";
    public static final String ACTIVE = "active";
    public static final String MESSAGE = "message";

    public HeroesTable() {
super(HeroesTable.class);
}

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getCount() {
        return get(COUNT, Integer.class);
    }
    public Integer getPlayed() {
        return get(PLAYED, Integer.class);
    }
    public Integer getActive() {
        return get(ACTIVE, Integer.class);
    }
    public String getMessage() {
        return get(MESSAGE, String.class);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setCount(Integer value) {
        set(COUNT, value);
    }
    public void setPlayed(Integer value) {
        set(PLAYED, value);
    }
    public void setActive(Integer value) {
        set(ACTIVE, value);
    }
    public void setMessage(String value) {
        set(MESSAGE, value);
    }

}
