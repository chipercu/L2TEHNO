package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.HeroesTable.*;

@Table(
        name = "heroes",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = COUNT,
                @Field(name = PLAYED,
                @Field(name = ACTIVE,
                @Field(name = MESSAGE,
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
    public IntegergetChar_id() {
        return get(CHAR_ID, Integer.class);
    }
    public IntegergetCount() {
        return get(COUNT, Integer.class);
    }
    public IntegergetPlayed() {
        return get(PLAYED, Integer.class);
    }
    public IntegergetActive() {
        return get(ACTIVE, Integer.class);
    }
    public StringgetMessage() {
        return get(MESSAGE, String.class);
    }

    public void setChar_id() {
        set(CHAR_ID, value);
    }
    public void setCount() {
        set(COUNT, value);
    }
    public void setPlayed() {
        set(PLAYED, value);
    }
    public void setActive() {
        set(ACTIVE, value);
    }
    public void setMessage() {
        set(MESSAGE, value);
    }

}
