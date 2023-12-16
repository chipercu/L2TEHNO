package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.FishTable.*;

@Table(
        name = "fish",
        fields = {
                @Field(name = ID),
                @Field(name = LEVEL),
                @Field(name = NAME),
                @Field(name = HP),
                @Field(name = HPREGEN),
                @Field(name = FISH_TYPE),
                @Field(name = FISH_GROUP),
                @Field(name = FISH_GUTS),
                @Field(name = GUTS_CHECK_TIME),
                @Field(name = WAIT_TIME),
                @Field(name = COMBAT_TIME),
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

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getHp() {
        return get(HP, Integer.class);
    }
    public Integer getHpregen() {
        return get(HPREGEN, Integer.class);
    }
    public Integer getFishType() {
        return get(FISH_TYPE, Integer.class);
    }
    public Integer getFishGroup() {
        return get(FISH_GROUP, Integer.class);
    }
    public Integer getFishGuts() {
        return get(FISH_GUTS, Integer.class);
    }
    public Integer getGutsCheckTime() {
        return get(GUTS_CHECK_TIME, Integer.class);
    }
    public Integer getWaitTime() {
        return get(WAIT_TIME, Integer.class);
    }
    public Integer getCombatTime() {
        return get(COMBAT_TIME, Integer.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setHp(Integer value) {
        set(HP, value);
    }
    public void setHpregen(Integer value) {
        set(HPREGEN, value);
    }
    public void setFishType(Integer value) {
        set(FISH_TYPE, value);
    }
    public void setFishGroup(Integer value) {
        set(FISH_GROUP, value);
    }
    public void setFishGuts(Integer value) {
        set(FISH_GUTS, value);
    }
    public void setGutsCheckTime(Integer value) {
        set(GUTS_CHECK_TIME, value);
    }
    public void setWaitTime(Integer value) {
        set(WAIT_TIME, value);
    }
    public void setCombatTime(Integer value) {
        set(COMBAT_TIME, value);
    }

}
