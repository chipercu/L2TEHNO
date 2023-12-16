package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Multisell_logTable.*;

@Table(
        name = "multisell_log",
        fields = {
                @Field(name = ID,
                @Field(name = DATE,
                @Field(name = ITEMID,
                @Field(name = COUNT,
                @Field(name = DITEMID,
                @Field(name = DCOUNT,
                @Field(name = NAME,
        }
)
public class Multisell_logTable extends DataBaseTable<Multisell_logTable> {

    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String ITEMID = "itemId";
    public static final String COUNT = "count";
    public static final String DITEMID = "dItemId";
    public static final String DCOUNT = "dCount";
    public static final String NAME = "name";
    public Multisell_logTable() {
super(Multisell_logTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetDate() {
        return get(DATE, String.class);
    }
    public StringgetItemId() {
        return get(ITEMID, String.class);
    }
    public StringgetCount() {
        return get(COUNT, String.class);
    }
    public StringgetDItemId() {
        return get(DITEMID, String.class);
    }
    public StringgetDCount() {
        return get(DCOUNT, String.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setDate() {
        set(DATE, value);
    }
    public void setItemId() {
        set(ITEMID, value);
    }
    public void setCount() {
        set(COUNT, value);
    }
    public void setDItemId() {
        set(DITEMID, value);
    }
    public void setDCount() {
        set(DCOUNT, value);
    }
    public void setName() {
        set(NAME, value);
    }

}
