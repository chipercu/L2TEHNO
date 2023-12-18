package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ItemallResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "itemall",
        primary_key = {ID},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = REUSE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MAGIC_WEAPON , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = IS_OLYMPIAD_CAN_USE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = IMMEDIATE_EFFECT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = EX_IMMEDIATE_EFFECT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DELAY_SHARE_GROUP , type = INT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = IS_PREMIUM , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class ItemallResource extends DataBaseTable<ItemallResource> {

    public static final String ID = "id";
    public static final String REUSE = "reuse";
    public static final String MAGIC_WEAPON = "magic_weapon";
    public static final String IS_OLYMPIAD_CAN_USE = "is_olympiad_can_use";
    public static final String IMMEDIATE_EFFECT = "immediate_effect";
    public static final String EX_IMMEDIATE_EFFECT = "ex_immediate_effect";
    public static final String DELAY_SHARE_GROUP = "delay_share_group";
    public static final String IS_PREMIUM = "is_premium";

    public ItemallResource() {
super(ItemallResource.class);
}


    public ItemallResource(String id){
        super(ItemallResource.class);
        getSTAT_SET().set(ID, id);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getReuse() {
        return get(REUSE, Integer.class);
    }
    public Integer getMagicWeapon() {
        return get(MAGIC_WEAPON, Integer.class);
    }
    public Integer getIsOlympiadCanUse() {
        return get(IS_OLYMPIAD_CAN_USE, Integer.class);
    }
    public Integer getImmediateEffect() {
        return get(IMMEDIATE_EFFECT, Integer.class);
    }
    public Integer getExImmediateEffect() {
        return get(EX_IMMEDIATE_EFFECT, Integer.class);
    }
    public Integer getDelayShareGroup() {
        return get(DELAY_SHARE_GROUP, Integer.class);
    }
    public Integer getIsPremium() {
        return get(IS_PREMIUM, Integer.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setReuse(Integer value) {
        set(REUSE, value);
    }
    public void setMagicWeapon(Integer value) {
        set(MAGIC_WEAPON, value);
    }
    public void setIsOlympiadCanUse(Integer value) {
        set(IS_OLYMPIAD_CAN_USE, value);
    }
    public void setImmediateEffect(Integer value) {
        set(IMMEDIATE_EFFECT, value);
    }
    public void setExImmediateEffect(Integer value) {
        set(EX_IMMEDIATE_EFFECT, value);
    }
    public void setDelayShareGroup(Integer value) {
        set(DELAY_SHARE_GROUP, value);
    }
    public void setIsPremium(Integer value) {
        set(IS_PREMIUM, value);
    }

}
