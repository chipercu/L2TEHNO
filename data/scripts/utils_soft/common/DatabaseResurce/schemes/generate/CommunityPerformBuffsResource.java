package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CommunityPerformBuffsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "community_perform_buffs",
        primary_key = {ID},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = BUFF_ID , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = BUFF_LEVEL , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = DISPLAY_LEVEL , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = NAME , type = VARCHAR , type_size = 250 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = ENCHANT_NAME , type = VARCHAR , type_size = 250 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = DURATION , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = PRICE , type = INT , nullable = true , defValue = @DefValue(Integer = 1)),
                @Field(name = PRICE_ITEM , type = INT , nullable = true , defValue = @DefValue(Integer = 57)),
                @Field(name = ICON , type = VARCHAR , type_size = 250 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = IS_PREMIUM , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
        }
)
public class CommunityPerformBuffsResource extends DataBaseTable<CommunityPerformBuffsResource> {

    public static final String ID = "id";
    public static final String BUFF_ID = "buff_id";
    public static final String BUFF_LEVEL = "buff_level";
    public static final String DISPLAY_LEVEL = "display_level";
    public static final String NAME = "name";
    public static final String ENCHANT_NAME = "enchant_name";
    public static final String DURATION = "duration";
    public static final String PRICE = "price";
    public static final String PRICE_ITEM = "price_item";
    public static final String ICON = "icon";
    public static final String IS_PREMIUM = "is_premium";

    public CommunityPerformBuffsResource() {
super(CommunityPerformBuffsResource.class);
}


    public CommunityPerformBuffsResource(String id){
        super(CommunityPerformBuffsResource.class);
        getSTAT_SET().set(ID, id);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getBuffId() {
        return get(BUFF_ID, Integer.class);
    }
    public Integer getBuffLevel() {
        return get(BUFF_LEVEL, Integer.class);
    }
    public Integer getDisplayLevel() {
        return get(DISPLAY_LEVEL, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getEnchantName() {
        return get(ENCHANT_NAME, String.class);
    }
    public Integer getDuration() {
        return get(DURATION, Integer.class);
    }
    public Integer getPrice() {
        return get(PRICE, Integer.class);
    }
    public Integer getPriceItem() {
        return get(PRICE_ITEM, Integer.class);
    }
    public String getIcon() {
        return get(ICON, String.class);
    }
    public Integer getIsPremium() {
        return get(IS_PREMIUM, Integer.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setBuffId(Integer value) {
        set(BUFF_ID, value);
    }
    public void setBuffLevel(Integer value) {
        set(BUFF_LEVEL, value);
    }
    public void setDisplayLevel(Integer value) {
        set(DISPLAY_LEVEL, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setEnchantName(String value) {
        set(ENCHANT_NAME, value);
    }
    public void setDuration(Integer value) {
        set(DURATION, value);
    }
    public void setPrice(Integer value) {
        set(PRICE, value);
    }
    public void setPriceItem(Integer value) {
        set(PRICE_ITEM, value);
    }
    public void setIcon(String value) {
        set(ICON, value);
    }
    public void setIsPremium(Integer value) {
        set(IS_PREMIUM, value);
    }

}
