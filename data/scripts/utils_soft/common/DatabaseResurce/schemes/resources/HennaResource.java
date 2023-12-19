package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.HennaResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "henna",
        primary_key = {SYMBOL_ID},
        fields = {
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SYMBOL_ID , defValue = "null"),
                @Column(is_null = YES , type = VARCHAR , type_size = 15 , name = SYMBOL_NAME , defValue = "NULL"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = DYE_ID , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = DYE_AMOUNT , defValue = "10"),
                @Column(is_null = NO , type = MEDIUMINT , type_size = 0 , name = PRICE , defValue = "1"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STAT__IN_T , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STAT__ST_R , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STAT__CO_N , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STAT__ME_M , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STAT__DE_X , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STAT__WI_T , defValue = "null"),
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
        return get(SYMBOL_ID);
    }
    public String getSymbolName() {
        return get(SYMBOL_NAME);
    }
    public Integer getDyeId() {
        return get(DYE_ID);
    }
    public Integer getDyeAmount() {
        return get(DYE_AMOUNT);
    }
    public Integer getPrice() {
        return get(PRICE);
    }
    public Integer getStatINT() {
        return get(STAT__IN_T);
    }
    public Integer getStatSTR() {
        return get(STAT__ST_R);
    }
    public Integer getStatCON() {
        return get(STAT__CO_N);
    }
    public Integer getStatMEM() {
        return get(STAT__ME_M);
    }
    public Integer getStatDEX() {
        return get(STAT__DE_X);
    }
    public Integer getStatWIT() {
        return get(STAT__WI_T);
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
