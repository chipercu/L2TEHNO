package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_premium_itemsTable.*;

@Table(
        name = "character_premium_items",
        fields = {
                @Field(name = CHARID,
                @Field(name = ITEMNUM,
                @Field(name = ITEMID,
                @Field(name = ITEMCOUNT,
                @Field(name = ITEMSENDER,
        }
)
public class Character_premium_itemsTable extends DataBaseTable<Character_premium_itemsTable> {

    public static final String CHARID = "charId";
    public static final String ITEMNUM = "itemNum";
    public static final String ITEMID = "itemId";
    public static final String ITEMCOUNT = "itemCount";
    public static final String ITEMSENDER = "itemSender";
    public Character_premium_itemsTable() {
super(Character_premium_itemsTable.class);
}
    public IntegergetCharId() {
        return get(CHARID, Integer.class);
    }
    public IntegergetItemNum() {
        return get(ITEMNUM, Integer.class);
    }
    public IntegergetItemId() {
        return get(ITEMID, Integer.class);
    }
    public ObjectgetItemCount() {
        return get(ITEMCOUNT, Object.class);
    }
    public StringgetItemSender() {
        return get(ITEMSENDER, String.class);
    }

    public void setCharId() {
        set(CHARID, value);
    }
    public void setItemNum() {
        set(ITEMNUM, value);
    }
    public void setItemId() {
        set(ITEMID, value);
    }
    public void setItemCount() {
        set(ITEMCOUNT, value);
    }
    public void setItemSender() {
        set(ITEMSENDER, value);
    }

}
