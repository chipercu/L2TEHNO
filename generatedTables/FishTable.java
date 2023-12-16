package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.FishTable.*;

@Table(
        name = "fish",
        fields = {
                @Field(name = ID,
                @Field(name = LEVEL,
                @Field(name = NAME,
                @Field(name = HP,
                @Field(name = HPREGEN,
                @Field(name = FISH_TYPE,
                @Field(name = FISH_GROUP,
                @Field(name = FISH_GUTS,
                @Field(name = GUTS_CHECK_TIME,
                @Field(name = WAIT_TIME,
                @Field(name = COMBAT_TIME,
        }
)
public class FishTable extends DataBaseTable<FishTable> {

    public static final String ID = "id";
    public static final String LEVEL = "level";
    public static final String NAME = "name";
    public static final String HP = "hp";
    public static final String HPREGEN = "hpregen";
    public static final String FISH_TYPE = "fish_type";
    public static final String FISH_GROUP = "fish_group";
    public static final String FISH_GUTS = "fish_guts";
    public static final String GUTS_CHECK_TIME = "guts_check_time";
    public static final String WAIT_TIME = "wait_time";
    public static final String COMBAT_TIME = "combat_time";
    public FishTable() {
super(FishTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetHp() {
        return get(HP, Integer.class);
    }
    public IntegergetHpregen() {
        return get(HPREGEN, Integer.class);
    }
    public IntegergetFish_type() {
        return get(FISH_TYPE, Integer.class);
    }
    public IntegergetFish_group() {
        return get(FISH_GROUP, Integer.class);
    }
    public IntegergetFish_guts() {
        return get(FISH_GUTS, Integer.class);
    }
    public IntegergetGuts_check_time() {
        return get(GUTS_CHECK_TIME, Integer.class);
    }
    public IntegergetWait_time() {
        return get(WAIT_TIME, Integer.class);
    }
    public IntegergetCombat_time() {
        return get(COMBAT_TIME, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setHp() {
        set(HP, value);
    }
    public void setHpregen() {
        set(HPREGEN, value);
    }
    public void setFish_type() {
        set(FISH_TYPE, value);
    }
    public void setFish_group() {
        set(FISH_GROUP, value);
    }
    public void setFish_guts() {
        set(FISH_GUTS, value);
    }
    public void setGuts_check_time() {
        set(GUTS_CHECK_TIME, value);
    }
    public void setWait_time() {
        set(WAIT_TIME, value);
    }
    public void setCombat_time() {
        set(COMBAT_TIME, value);
    }

}
