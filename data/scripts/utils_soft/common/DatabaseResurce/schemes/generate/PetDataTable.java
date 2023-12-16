package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.PetDataTable.*;

@Table(
        name = "pet_data",
        fields = {
                @Field(name = TYPE),
                @Field(name = ID),
                @Field(name = LEVEL),
                @Field(name = EXP),
                @Field(name = HP),
                @Field(name = MP),
                @Field(name = PATK),
                @Field(name = PDEF),
                @Field(name = MATK),
                @Field(name = MDEF),
                @Field(name = ACC),
                @Field(name = EVASION),
                @Field(name = CRIT),
                @Field(name = SPEED),
                @Field(name = ATK_SPEED),
                @Field(name = CAST_SPEED),
                @Field(name = MAX_MEAL),
                @Field(name = BATTLE_MEAL),
                @Field(name = NORMAL_MEAL),
                @Field(name = LOAD_MAX),
                @Field(name = HPREGEN),
                @Field(name = MPREGEN),
                @Field(name = GET_EXP_TYPE),
                @Field(name = SPIRITSHOT_COUNT),
                @Field(name = SOULSHOT_COUNT),
                @Field(name = ITEM),
                @Field(name = FOOD),
                @Field(name = ADD_MEAL),
                @Field(name = HUNGRY_LIMIT),
                @Field(name = MOUNTABE),
        }
)
public class PetDataTable extends DataBaseTable<PetDataTable> {

    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String LEVEL = "level";
    public static final String EXP = "exp";
    public static final String HP = "hp";
    public static final String MP = "mp";
    public static final String PATK = "patk";
    public static final String PDEF = "pdef";
    public static final String MATK = "matk";
    public static final String MDEF = "mdef";
    public static final String ACC = "acc";
    public static final String EVASION = "evasion";
    public static final String CRIT = "crit";
    public static final String SPEED = "speed";
    public static final String ATK_SPEED = "atk_speed";
    public static final String CAST_SPEED = "cast_speed";
    public static final String MAX_MEAL = "max_meal";
    public static final String BATTLE_MEAL = "battle_meal";
    public static final String NORMAL_MEAL = "normal_meal";
    public static final String LOAD_MAX = "loadMax";
    public static final String HPREGEN = "hpregen";
    public static final String MPREGEN = "mpregen";
    public static final String GET_EXP_TYPE = "get_exp_type";
    public static final String SPIRITSHOT_COUNT = "spiritshot_count";
    public static final String SOULSHOT_COUNT = "soulshot_count";
    public static final String ITEM = "item";
    public static final String FOOD = "food";
    public static final String ADD_MEAL = "add_meal";
    public static final String HUNGRY_LIMIT = "hungry_limit";
    public static final String MOUNTABE = "mountabe";

    public PetDataTable() {
super(PetDataTable.class);
}

    public String getType() {
        return get(TYPE, String.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Double getExp() {
        return get(EXP, Double.class);
    }
    public Integer getHp() {
        return get(HP, Integer.class);
    }
    public Integer getMp() {
        return get(MP, Integer.class);
    }
    public Integer getPatk() {
        return get(PATK, Integer.class);
    }
    public Integer getPdef() {
        return get(PDEF, Integer.class);
    }
    public Integer getMatk() {
        return get(MATK, Integer.class);
    }
    public Integer getMdef() {
        return get(MDEF, Integer.class);
    }
    public Integer getAcc() {
        return get(ACC, Integer.class);
    }
    public Integer getEvasion() {
        return get(EVASION, Integer.class);
    }
    public Integer getCrit() {
        return get(CRIT, Integer.class);
    }
    public Integer getSpeed() {
        return get(SPEED, Integer.class);
    }
    public Integer getAtkSpeed() {
        return get(ATK_SPEED, Integer.class);
    }
    public Integer getCastSpeed() {
        return get(CAST_SPEED, Integer.class);
    }
    public Integer getMaxMeal() {
        return get(MAX_MEAL, Integer.class);
    }
    public Integer getBattleMeal() {
        return get(BATTLE_MEAL, Integer.class);
    }
    public Integer getNormalMeal() {
        return get(NORMAL_MEAL, Integer.class);
    }
    public Integer getLoadMax() {
        return get(LOAD_MAX, Integer.class);
    }
    public Double getHpregen() {
        return get(HPREGEN, Double.class);
    }
    public Double getMpregen() {
        return get(MPREGEN, Double.class);
    }
    public Double getGetExpType() {
        return get(GET_EXP_TYPE, Double.class);
    }
    public Integer getSpiritshotCount() {
        return get(SPIRITSHOT_COUNT, Integer.class);
    }
    public Integer getSoulshotCount() {
        return get(SOULSHOT_COUNT, Integer.class);
    }
    public Integer getItem() {
        return get(ITEM, Integer.class);
    }
    public Integer getFood() {
        return get(FOOD, Integer.class);
    }
    public Integer getAddMeal() {
        return get(ADD_MEAL, Integer.class);
    }
    public Integer getHungryLimit() {
        return get(HUNGRY_LIMIT, Integer.class);
    }
    public Boolean getMountabe() {
        return get(MOUNTABE, Boolean.class);
    }

    public void setType(String value) {
        set(TYPE, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setExp(Double value) {
        set(EXP, value);
    }
    public void setHp(Integer value) {
        set(HP, value);
    }
    public void setMp(Integer value) {
        set(MP, value);
    }
    public void setPatk(Integer value) {
        set(PATK, value);
    }
    public void setPdef(Integer value) {
        set(PDEF, value);
    }
    public void setMatk(Integer value) {
        set(MATK, value);
    }
    public void setMdef(Integer value) {
        set(MDEF, value);
    }
    public void setAcc(Integer value) {
        set(ACC, value);
    }
    public void setEvasion(Integer value) {
        set(EVASION, value);
    }
    public void setCrit(Integer value) {
        set(CRIT, value);
    }
    public void setSpeed(Integer value) {
        set(SPEED, value);
    }
    public void setAtkSpeed(Integer value) {
        set(ATK_SPEED, value);
    }
    public void setCastSpeed(Integer value) {
        set(CAST_SPEED, value);
    }
    public void setMaxMeal(Integer value) {
        set(MAX_MEAL, value);
    }
    public void setBattleMeal(Integer value) {
        set(BATTLE_MEAL, value);
    }
    public void setNormalMeal(Integer value) {
        set(NORMAL_MEAL, value);
    }
    public void setLoadMax(Integer value) {
        set(LOAD_MAX, value);
    }
    public void setHpregen(Double value) {
        set(HPREGEN, value);
    }
    public void setMpregen(Double value) {
        set(MPREGEN, value);
    }
    public void setGetExpType(Double value) {
        set(GET_EXP_TYPE, value);
    }
    public void setSpiritshotCount(Integer value) {
        set(SPIRITSHOT_COUNT, value);
    }
    public void setSoulshotCount(Integer value) {
        set(SOULSHOT_COUNT, value);
    }
    public void setItem(Integer value) {
        set(ITEM, value);
    }
    public void setFood(Integer value) {
        set(FOOD, value);
    }
    public void setAddMeal(Integer value) {
        set(ADD_MEAL, value);
    }
    public void setHungryLimit(Integer value) {
        set(HUNGRY_LIMIT, value);
    }
    public void setMountabe(Boolean value) {
        set(MOUNTABE, value);
    }

}
