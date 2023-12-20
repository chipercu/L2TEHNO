package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.PetDataResource.*;

@Table(
        name = "pet_data",
        primary_key = {ID,LEVEL},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 25 , name = TYPE , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEVEL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = EXP , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = HP , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MP , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = PATK , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = PDEF , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MATK , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MDEF , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ACC , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = EVASION , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CRIT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SPEED , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ATK_SPEED , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CAST_SPEED , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MAX_MEAL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = BATTLE_MEAL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = NORMAL_MEAL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOAD_MAX , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = HPREGEN , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = MPREGEN , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = GET_EXP_TYPE , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = SPIRITSHOT_COUNT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = SOULSHOT_COUNT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FOOD , defValue = "-1"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ADD_MEAL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = HUNGRY_LIMIT , defValue = "55"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.ENUM , type_size = 5 , name = MOUNTABE , defValue = "false"),
        }
)
public class PetDataResource extends DataBaseTable<PetDataResource> {

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

    public PetDataResource() {
        super(PetDataResource.class);
}

    public String getType() {
        return get(TYPE);
    }
    public Integer getId() {
        return get(ID);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Double getExp() {
        return get(EXP);
    }
    public Integer getHp() {
        return get(HP);
    }
    public Integer getMp() {
        return get(MP);
    }
    public Integer getPatk() {
        return get(PATK);
    }
    public Integer getPdef() {
        return get(PDEF);
    }
    public Integer getMatk() {
        return get(MATK);
    }
    public Integer getMdef() {
        return get(MDEF);
    }
    public Integer getAcc() {
        return get(ACC);
    }
    public Integer getEvasion() {
        return get(EVASION);
    }
    public Integer getCrit() {
        return get(CRIT);
    }
    public Integer getSpeed() {
        return get(SPEED);
    }
    public Integer getAtkSpeed() {
        return get(ATK_SPEED);
    }
    public Integer getCastSpeed() {
        return get(CAST_SPEED);
    }
    public Integer getMaxMeal() {
        return get(MAX_MEAL);
    }
    public Integer getBattleMeal() {
        return get(BATTLE_MEAL);
    }
    public Integer getNormalMeal() {
        return get(NORMAL_MEAL);
    }
    public Integer getLoadMax() {
        return get(LOAD_MAX);
    }
    public Double getHpregen() {
        return get(HPREGEN);
    }
    public Double getMpregen() {
        return get(MPREGEN);
    }
    public Double getGetExpType() {
        return get(GET_EXP_TYPE);
    }
    public Integer getSpiritshotCount() {
        return get(SPIRITSHOT_COUNT);
    }
    public Integer getSoulshotCount() {
        return get(SOULSHOT_COUNT);
    }
    public Integer getItem() {
        return get(ITEM);
    }
    public Integer getFood() {
        return get(FOOD);
    }
    public Integer getAddMeal() {
        return get(ADD_MEAL);
    }
    public Integer getHungryLimit() {
        return get(HUNGRY_LIMIT);
    }
    public Boolean getMountabe() {
        return get(MOUNTABE);
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
