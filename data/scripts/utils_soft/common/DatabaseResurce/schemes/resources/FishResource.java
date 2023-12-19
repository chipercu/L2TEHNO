package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.FishResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "fish",
        primary_key = {ID,LEVEL},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEVEL , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 40 , name = NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HPREGEN , defValue = "5"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FISH_TYPE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FISH_GROUP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FISH_GUTS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = GUTS_CHECK_TIME , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = WAIT_TIME , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = COMBAT_TIME , defValue = "0"),
        }
)
public class FishResource extends DataBaseTable<FishResource> {

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

    public FishResource() {
        super(FishResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getHp() {
        return get(HP);
    }
    public Integer getHpregen() {
        return get(HPREGEN);
    }
    public Integer getFishType() {
        return get(FISH_TYPE);
    }
    public Integer getFishGroup() {
        return get(FISH_GROUP);
    }
    public Integer getFishGuts() {
        return get(FISH_GUTS);
    }
    public Integer getGutsCheckTime() {
        return get(GUTS_CHECK_TIME);
    }
    public Integer getWaitTime() {
        return get(WAIT_TIME);
    }
    public Integer getCombatTime() {
        return get(COMBAT_TIME);
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
