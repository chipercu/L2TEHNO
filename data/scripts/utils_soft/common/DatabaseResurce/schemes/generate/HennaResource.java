package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HennaResource.*;

@Table(
        name = "henna",
        primary_key = {SYMBOL_ID},
        fields = {
                @Field(name = SYMBOL_ID , data_type = "tinyint"),
                @Field(name = SYMBOL_NAME , data_type = "varchar"),
                @Field(name = DYE_ID , data_type = "smallint"),
                @Field(name = DYE_AMOUNT , data_type = "tinyint" , defValue = @DefValue(Integer = 10)),
                @Field(name = PRICE , data_type = "mediumint" , defValue = @DefValue(Integer = 1)),
                @Field(name = STAT__IN_T , data_type = "tinyint"),
                @Field(name = STAT__ST_R , data_type = "tinyint"),
                @Field(name = STAT__CO_N , data_type = "tinyint"),
                @Field(name = STAT__ME_M , data_type = "tinyint"),
                @Field(name = STAT__DE_X , data_type = "tinyint"),
                @Field(name = STAT__WI_T , data_type = "tinyint"),
        }
)
public class HennaResource extends DataBaseTable<HennaResource> {

    public static final String SYMBOL_ID = "symbol_id";
    public static final String SYMBOL_NAME = "symbol_name";
    public static final String DYE_ID = "dye_id";
    public static final String DYE_AMOUNT = "dye_amount";
    public static final String PRICE = "price";
    public static final String STAT__IN_T = "stat_INT";
    public static final String STAT__ST_R = "stat_STR";
    public static final String STAT__CO_N = "stat_CON";
    public static final String STAT__ME_M = "stat_MEM";
    public static final String STAT__DE_X = "stat_DEX";
    public static final String STAT__WI_T = "stat_WIT";

    public HennaResource() {
super(HennaResource.class);
}

    public Integer getSymbolId() {
        return get(SYMBOL_ID, Integer.class);
    }
    public String getSymbolName() {
        return get(SYMBOL_NAME, String.class);
    }
    public Integer getDyeId() {
        return get(DYE_ID, Integer.class);
    }
    public Integer getDyeAmount() {
        return get(DYE_AMOUNT, Integer.class);
    }
    public Integer getPrice() {
        return get(PRICE, Integer.class);
    }
    public Integer getStatINT() {
        return get(STAT__IN_T, Integer.class);
    }
    public Integer getStatSTR() {
        return get(STAT__ST_R, Integer.class);
    }
    public Integer getStatCON() {
        return get(STAT__CO_N, Integer.class);
    }
    public Integer getStatMEM() {
        return get(STAT__ME_M, Integer.class);
    }
    public Integer getStatDEX() {
        return get(STAT__DE_X, Integer.class);
    }
    public Integer getStatWIT() {
        return get(STAT__WI_T, Integer.class);
    }

    public void setSymbolId(Integer value) {
        set(SYMBOL_ID, value);
    }
    public void setSymbolName(String value) {
        set(SYMBOL_NAME, value);
    }
    public void setDyeId(Integer value) {
        set(DYE_ID, value);
    }
    public void setDyeAmount(Integer value) {
        set(DYE_AMOUNT, value);
    }
    public void setPrice(Integer value) {
        set(PRICE, value);
    }
    public void setStatINT(Integer value) {
        set(STAT__IN_T, value);
    }
    public void setStatSTR(Integer value) {
        set(STAT__ST_R, value);
    }
    public void setStatCON(Integer value) {
        set(STAT__CO_N, value);
    }
    public void setStatMEM(Integer value) {
        set(STAT__ME_M, value);
    }
    public void setStatDEX(Integer value) {
        set(STAT__DE_X, value);
    }
    public void setStatWIT(Integer value) {
        set(STAT__WI_T, value);
    }

}
