package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CommunityPerformBuffsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "community_perform_buffs",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = DISPLAY_LEVEL , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = SKILL_ID , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = SKILL_LEVEL , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 100 , name = NAME , defValue = ""),
                @Column(is_null = YES , type = INT , type_size = 0 , name = PRICE , defValue = "1"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = PRICE_ITEM , defValue = "57"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = MIN_LEVEL , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = MAX_LEVEL , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 50 , name = ICON , defValue = ""),
                @Column(is_null = YES , type = VARCHAR , type_size = 50 , name = TYPE_ , defValue = "NULL"),
        }
)
public class CommunityPerformBuffsResource extends DataBaseTable<CommunityPerformBuffsResource> {

    public static final String ID = "id";
    public static final String DISPLAY_LEVEL = "display_level";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String PRICE_ITEM = "price_item";
    public static final String MIN_LEVEL = "minLevel";
    public static final String MAX_LEVEL = "maxLevel";
    public static final String ICON = "icon";
    public static final String TYPE_ = "type_";

    public CommunityPerformBuffsResource() {
        super(CommunityPerformBuffsResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getDisplayLevel() {
        return get(DISPLAY_LEVEL);
    }
    public Integer getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getPrice() {
        return get(PRICE);
    }
    public Integer getPriceItem() {
        return get(PRICE_ITEM);
    }
    public Integer getMinLevel() {
        return get(MIN_LEVEL);
    }
    public Integer getMaxLevel() {
        return get(MAX_LEVEL);
    }
    public String getIcon() {
        return get(ICON);
    }
    public String getType() {
        return get(TYPE_);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setDisplayLevel(Integer value) {
        set(DISPLAY_LEVEL, value);
    }
    public void setSkillId(Integer value) {
        set(SKILL_ID, value);
    }
    public void setSkillLevel(Integer value) {
        set(SKILL_LEVEL, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setPrice(Integer value) {
        set(PRICE, value);
    }
    public void setPriceItem(Integer value) {
        set(PRICE_ITEM, value);
    }
    public void setMinLevel(Integer value) {
        set(MIN_LEVEL, value);
    }
    public void setMaxLevel(Integer value) {
        set(MAX_LEVEL, value);
    }
    public void setIcon(String value) {
        set(ICON, value);
    }
    public void setType(String value) {
        set(TYPE_, value);
    }

}
