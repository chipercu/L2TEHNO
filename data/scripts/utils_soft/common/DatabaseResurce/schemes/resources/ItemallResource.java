package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ItemallResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "itemall",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = REUSE , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = MAGIC_WEAPON , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = IS_OLYMPIAD_CAN_USE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = IMMEDIATE_EFFECT , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = EX_IMMEDIATE_EFFECT , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DELAY_SHARE_GROUP , defValue = "-1"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = IS_PREMIUM , defValue = "0"),
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

    public Integer getId() {
        return get(ID);
    }
    public Integer getReuse() {
        return get(REUSE);
    }
    public Integer getMagicWeapon() {
        return get(MAGIC_WEAPON);
    }
    public Integer getIsOlympiadCanUse() {
        return get(IS_OLYMPIAD_CAN_USE);
    }
    public Integer getImmediateEffect() {
        return get(IMMEDIATE_EFFECT);
    }
    public Integer getExImmediateEffect() {
        return get(EX_IMMEDIATE_EFFECT);
    }
    public Integer getDelayShareGroup() {
        return get(DELAY_SHARE_GROUP);
    }
    public Integer getIsPremium() {
        return get(IS_PREMIUM);
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
