package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.MultisellLogResource.*;

@Table(
        name = "multisell_log",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 2048 , name = _DATE , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 2048 , name = ITEM_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 2048 , name = COUNT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 2048 , name = D_ITEM_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 2048 , name = D_COUNT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 16 , name = NAME , defValue = ""),
        }
)
public class MultisellLogResource extends DataBaseTable<MultisellLogResource> {

    public static final String ID = "id";
    public static final String _DATE = "date";
    public static final String ITEM_ID = "itemId";
    public static final String COUNT = "count";
    public static final String D_ITEM_ID = "dItemId";
    public static final String D_COUNT = "dCount";
    public static final String NAME = "name";

    public MultisellLogResource() {
        super(MultisellLogResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getDate() {
        return get(_DATE);
    }
    public String getItemId() {
        return get(ITEM_ID);
    }
    public String getCount() {
        return get(COUNT);
    }
    public String getDItemId() {
        return get(D_ITEM_ID);
    }
    public String getDCount() {
        return get(D_COUNT);
    }
    public String getName() {
        return get(NAME);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setDate(String value) {
        set(_DATE, value);
    }
    public void setItemId(String value) {
        set(ITEM_ID, value);
    }
    public void setCount(String value) {
        set(COUNT, value);
    }
    public void setDItemId(String value) {
        set(D_ITEM_ID, value);
    }
    public void setDCount(String value) {
        set(D_COUNT, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }

}