package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterPremiumItemsTable.*;

@Table(
        name = "character_premium_items",
        fields = {
                @Field(name = CHAR_ID),
                @Field(name = ITEM_NUM),
                @Field(name = ITEM_ID),
                @Field(name = ITEM_COUNT),
                @Field(name = ITEM_SENDER),
        }
)
public class CharacterPremiumItemsTable extends DataBaseTable<CharacterPremiumItemsTable> {

    public static final String CHAR_ID = "charId";
    public static final String ITEM_NUM = "itemNum";
    public static final String ITEM_ID = "itemId";
    public static final String ITEM_COUNT = "itemCount";
    public static final String ITEM_SENDER = "itemSender";

    public CharacterPremiumItemsTable() {
super(CharacterPremiumItemsTable.class);
}

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getItemNum() {
        return get(ITEM_NUM, Integer.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Long getItemCount() {
        return get(ITEM_COUNT, Long.class);
    }
    public String getItemSender() {
        return get(ITEM_SENDER, String.class);
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
