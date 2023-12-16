package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Pet_dataTable.*;

@Table(
        name = "pet_data",
        fields = {
                @Field(name = TYPE,
                @Field(name = ID,
                @Field(name = LEVEL,
                @Field(name = EXP,
                @Field(name = HP,
                @Field(name = MP,
                @Field(name = PATK,
                @Field(name = PDEF,
                @Field(name = MATK,
                @Field(name = MDEF,
                @Field(name = ACC,
                @Field(name = EVASION,
                @Field(name = CRIT,
                @Field(name = SPEED,
                @Field(name = ATK_SPEED,
                @Field(name = CAST_SPEED,
                @Field(name = MAX_MEAL,
                @Field(name = BATTLE_MEAL,
                @Field(name = NORMAL_MEAL,
                @Field(name = LOADMAX,
                @Field(name = HPREGEN,
                @Field(name = MPREGEN,
                @Field(name = GET_EXP_TYPE,
                @Field(name = SPIRITSHOT_COUNT,
                @Field(name = SOULSHOT_COUNT,
                @Field(name = ITEM,
                @Field(name = FOOD,
                @Field(name = ADD_MEAL,
                @Field(name = HUNGRY_LIMIT,
                @Field(name = MOUNTABE,
        }
)
public class Pet_dataTable extends DataBaseTable<Pet_dataTable> {

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
    public static final String LOADMAX = "loadMax";
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
    public Pet_dataTable() {
super(Pet_dataTable.class);
}
    public StringgetType() {
        return get(TYPE, String.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public DoublegetExp() {
        return get(EXP, Double.class);
    }
    public IntegergetHp() {
        return get(HP, Integer.class);
    }
    public IntegergetMp() {
        return get(MP, Integer.class);
    }
    public IntegergetPatk() {
        return get(PATK, Integer.class);
    }
    public IntegergetPdef() {
        return get(PDEF, Integer.class);
    }
    public IntegergetMatk() {
        return get(MATK, Integer.class);
    }
    public IntegergetMdef() {
        return get(MDEF, Integer.class);
    }
    public IntegergetAcc() {
        return get(ACC, Integer.class);
    }
    public IntegergetEvasion() {
        return get(EVASION, Integer.class);
    }
    public IntegergetCrit() {
        return get(CRIT, Integer.class);
    }
    public IntegergetSpeed() {
        return get(SPEED, Integer.class);
    }
    public IntegergetAtk_speed() {
        return get(ATK_SPEED, Integer.class);
    }
    public IntegergetCast_speed() {
        return get(CAST_SPEED, Integer.class);
    }
    public IntegergetMax_meal() {
        return get(MAX_MEAL, Integer.class);
    }
    public IntegergetBattle_meal() {
        return get(BATTLE_MEAL, Integer.class);
    }
    public IntegergetNormal_meal() {
        return get(NORMAL_MEAL, Integer.class);
    }
    public IntegergetLoadMax() {
        return get(LOADMAX, Integer.class);
    }
    public DoublegetHpregen() {
        return get(HPREGEN, Double.class);
    }
    public DoublegetMpregen() {
        return get(MPREGEN, Double.class);
    }
    public DoublegetGet_exp_type() {
        return get(GET_EXP_TYPE, Double.class);
    }
    public IntegergetSpiritshot_count() {
        return get(SPIRITSHOT_COUNT, Integer.class);
    }
    public IntegergetSoulshot_count() {
        return get(SOULSHOT_COUNT, Integer.class);
    }
    public IntegergetItem() {
        return get(ITEM, Integer.class);
    }
    public IntegergetFood() {
        return get(FOOD, Integer.class);
    }
    public IntegergetAdd_meal() {
        return get(ADD_MEAL, Integer.class);
    }
    public IntegergetHungry_limit() {
        return get(HUNGRY_LIMIT, Integer.class);
    }
    public ObjectgetMountabe() {
        return get(MOUNTABE, Object.class);
    }

    public void setType() {
        set(TYPE, value);
    }
    public void setId() {
        set(ID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setExp() {
        set(EXP, value);
    }
    public void setHp() {
        set(HP, value);
    }
    public void setMp() {
        set(MP, value);
    }
    public void setPatk() {
        set(PATK, value);
    }
    public void setPdef() {
        set(PDEF, value);
    }
    public void setMatk() {
        set(MATK, value);
    }
    public void setMdef() {
        set(MDEF, value);
    }
    public void setAcc() {
        set(ACC, value);
    }
    public void setEvasion() {
        set(EVASION, value);
    }
    public void setCrit() {
        set(CRIT, value);
    }
    public void setSpeed() {
        set(SPEED, value);
    }
    public void setAtk_speed() {
        set(ATK_SPEED, value);
    }
    public void setCast_speed() {
        set(CAST_SPEED, value);
    }
    public void setMax_meal() {
        set(MAX_MEAL, value);
    }
    public void setBattle_meal() {
        set(BATTLE_MEAL, value);
    }
    public void setNormal_meal() {
        set(NORMAL_MEAL, value);
    }
    public void setLoadMax() {
        set(LOADMAX, value);
    }
    public void setHpregen() {
        set(HPREGEN, value);
    }
    public void setMpregen() {
        set(MPREGEN, value);
    }
    public void setGet_exp_type() {
        set(GET_EXP_TYPE, value);
    }
    public void setSpiritshot_count() {
        set(SPIRITSHOT_COUNT, value);
    }
    public void setSoulshot_count() {
        set(SOULSHOT_COUNT, value);
    }
    public void setItem() {
        set(ITEM, value);
    }
    public void setFood() {
        set(FOOD, value);
    }
    public void setAdd_meal() {
        set(ADD_MEAL, value);
    }
    public void setHungry_limit() {
        set(HUNGRY_LIMIT, value);
    }
    public void setMountabe() {
        set(MOUNTABE, value);
    }

}
