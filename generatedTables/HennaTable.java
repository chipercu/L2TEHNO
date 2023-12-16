package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.HennaTable.*;

@Table(
        name = "henna",
        fields = {
                @Field(name = SYMBOL_ID,
                @Field(name = SYMBOL_NAME,
                @Field(name = DYE_ID,
                @Field(name = DYE_AMOUNT,
                @Field(name = PRICE,
                @Field(name = STAT_INT,
                @Field(name = STAT_STR,
                @Field(name = STAT_CON,
                @Field(name = STAT_MEM,
                @Field(name = STAT_DEX,
                @Field(name = STAT_WIT,
        }
)
public class HennaTable extends DataBaseTable<HennaTable> {

    public static final String SYMBOL_ID = "symbol_id";
    public static final String SYMBOL_NAME = "symbol_name";
    public static final String DYE_ID = "dye_id";
    public static final String DYE_AMOUNT = "dye_amount";
    public static final String PRICE = "price";
    public static final String STAT_INT = "stat_INT";
    public static final String STAT_STR = "stat_STR";
    public static final String STAT_CON = "stat_CON";
    public static final String STAT_MEM = "stat_MEM";
    public static final String STAT_DEX = "stat_DEX";
    public static final String STAT_WIT = "stat_WIT";
    public HennaTable() {
super(HennaTable.class);
}
    public IntegergetSymbol_id() {
        return get(SYMBOL_ID, Integer.class);
    }
    public StringgetSymbol_name() {
        return get(SYMBOL_NAME, String.class);
    }
    public IntegergetDye_id() {
        return get(DYE_ID, Integer.class);
    }
    public IntegergetDye_amount() {
        return get(DYE_AMOUNT, Integer.class);
    }
    public IntegergetPrice() {
        return get(PRICE, Integer.class);
    }
    public IntegergetStat_INT() {
        return get(STAT_INT, Integer.class);
    }
    public IntegergetStat_STR() {
        return get(STAT_STR, Integer.class);
    }
    public IntegergetStat_CON() {
        return get(STAT_CON, Integer.class);
    }
    public IntegergetStat_MEM() {
        return get(STAT_MEM, Integer.class);
    }
    public IntegergetStat_DEX() {
        return get(STAT_DEX, Integer.class);
    }
    public IntegergetStat_WIT() {
        return get(STAT_WIT, Integer.class);
    }

    public void setSymbol_id() {
        set(SYMBOL_ID, value);
    }
    public void setSymbol_name() {
        set(SYMBOL_NAME, value);
    }
    public void setDye_id() {
        set(DYE_ID, value);
    }
    public void setDye_amount() {
        set(DYE_AMOUNT, value);
    }
    public void setPrice() {
        set(PRICE, value);
    }
    public void setStat_INT() {
        set(STAT_INT, value);
    }
    public void setStat_STR() {
        set(STAT_STR, value);
    }
    public void setStat_CON() {
        set(STAT_CON, value);
    }
    public void setStat_MEM() {
        set(STAT_MEM, value);
    }
    public void setStat_DEX() {
        set(STAT_DEX, value);
    }
    public void setStat_WIT() {
        set(STAT_WIT, value);
    }

}
