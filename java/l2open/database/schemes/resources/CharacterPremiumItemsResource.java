package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterPremiumItemsResource.*;

@Table(
        name = "character_premium_items",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM_NUM , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = ITEM_COUNT , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 50 , name = ITEM_SENDER , defValue = "null"),
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
