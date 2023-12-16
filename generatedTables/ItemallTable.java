package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.ItemallTable.*;

@Table(
        name = "itemall",
        fields = {
                @Field(name = ID,
                @Field(name = REUSE,
                @Field(name = MAGIC_WEAPON,
                @Field(name = IS_OLYMPIAD_CAN_USE,
                @Field(name = IMMEDIATE_EFFECT,
                @Field(name = EX_IMMEDIATE_EFFECT,
                @Field(name = DELAY_SHARE_GROUP,
                @Field(name = IS_PREMIUM,
        }
)
public class ItemallTable extends DataBaseTable<ItemallTable> {

    public static final String ID = "id";
    public static final String REUSE = "reuse";
    public static final String MAGIC_WEAPON = "magic_weapon";
    public static final String IS_OLYMPIAD_CAN_USE = "is_olympiad_can_use";
    public static final String IMMEDIATE_EFFECT = "immediate_effect";
    public static final String EX_IMMEDIATE_EFFECT = "ex_immediate_effect";
    public static final String DELAY_SHARE_GROUP = "delay_share_group";
    public static final String IS_PREMIUM = "is_premium";
    public ItemallTable() {
super(ItemallTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetReuse() {
        return get(REUSE, Integer.class);
    }
    public IntegergetMagic_weapon() {
        return get(MAGIC_WEAPON, Integer.class);
    }
    public IntegergetIs_olympiad_can_use() {
        return get(IS_OLYMPIAD_CAN_USE, Integer.class);
    }
    public IntegergetImmediate_effect() {
        return get(IMMEDIATE_EFFECT, Integer.class);
    }
    public IntegergetEx_immediate_effect() {
        return get(EX_IMMEDIATE_EFFECT, Integer.class);
    }
    public IntegergetDelay_share_group() {
        return get(DELAY_SHARE_GROUP, Integer.class);
    }
    public IntegergetIs_premium() {
        return get(IS_PREMIUM, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setReuse() {
        set(REUSE, value);
    }
    public void setMagic_weapon() {
        set(MAGIC_WEAPON, value);
    }
    public void setIs_olympiad_can_use() {
        set(IS_OLYMPIAD_CAN_USE, value);
    }
    public void setImmediate_effect() {
        set(IMMEDIATE_EFFECT, value);
    }
    public void setEx_immediate_effect() {
        set(EX_IMMEDIATE_EFFECT, value);
    }
    public void setDelay_share_group() {
        set(DELAY_SHARE_GROUP, value);
    }
    public void setIs_premium() {
        set(IS_PREMIUM, value);
    }

}
