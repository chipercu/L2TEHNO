package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterPremiumItemsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_premium_items",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_NUM , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_ID , defValue = "null"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ITEM_COUNT , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 50 , name = ITEM_SENDER , defValue = "null"),
        }
)
public class CharacterPremiumItemsResource extends DataBaseTable<CharacterPremiumItemsResource> {

    public static final String CHAR_ID = "charId";
    public static final String ITEM_NUM = "itemNum";
    public static final String ITEM_ID = "itemId";
    public static final String ITEM_COUNT = "itemCount";
    public static final String ITEM_SENDER = "itemSender";

    public CharacterPremiumItemsResource() {
        super(CharacterPremiumItemsResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getItemNum() {
        return get(ITEM_NUM);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Long getItemCount() {
        return get(ITEM_COUNT);
    }
    public String getItemSender() {
        return get(ITEM_SENDER);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setItemNum(Integer value) {
        set(ITEM_NUM, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setItemCount(Long value) {
        set(ITEM_COUNT, value);
    }
    public void setItemSender(String value) {
        set(ITEM_SENDER, value);
    }

}
