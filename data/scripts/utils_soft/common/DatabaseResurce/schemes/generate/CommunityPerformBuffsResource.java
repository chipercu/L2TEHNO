package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunityPerformBuffsResource.*;

@Table(
        name = "community_perform_buffs",
        primary_key = {ID},
        fields = {
                @Field(name = ID , data_type = "int"),
                @Field(name = DISPLAY_LEVEL , data_type = "int"),
                @Field(name = SKILL_ID , data_type = "int"),
                @Field(name = SKILL_LEVEL , data_type = "int"),
                @Field(name = NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = PRICE , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = PRICE_ITEM , data_type = "int" , defValue = @DefValue(Integer = 57)),
                @Field(name = MIN_LEVEL , data_type = "int"),
                @Field(name = MAX_LEVEL , data_type = "int"),
                @Field(name = ICON , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = TYPE_ , data_type = "varchar"),
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
        return get(ID, Integer.class);
    }
    public Integer getDisplayLevel() {
        return get(DISPLAY_LEVEL, Integer.class);
    }
    public Integer getSkillId() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getSkillLevel() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getPrice() {
        return get(PRICE, Integer.class);
    }
    public Integer getPriceItem() {
        return get(PRICE_ITEM, Integer.class);
    }
    public Integer getMinLevel() {
        return get(MIN_LEVEL, Integer.class);
    }
    public Integer getMaxLevel() {
        return get(MAX_LEVEL, Integer.class);
    }
    public String getIcon() {
        return get(ICON, String.class);
    }
    public String getType() {
        return get(TYPE_, String.class);
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
