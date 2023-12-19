package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.RecipesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "recipes",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 40 , name = NAME , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FOUNDATION , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Q , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LVL , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SUCCESS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RECID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EXP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DWARVEN , defValue = "1"),
        }
)
public class RecipesResource extends DataBaseTable<RecipesResource> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ITEM = "item";
    public static final String FOUNDATION = "foundation";
    public static final String Q = "q";
    public static final String LVL = "lvl";
    public static final String SUCCESS = "success";
    public static final String RECID = "recid";
    public static final String MP = "mp";
    public static final String EXP = "exp";
    public static final String SP = "sp";
    public static final String DWARVEN = "dwarven";

    public RecipesResource() {
        super(RecipesResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getItem() {
        return get(ITEM);
    }
    public Integer getFoundation() {
        return get(FOUNDATION);
    }
    public Integer getQ() {
        return get(Q);
    }
    public Integer getLvl() {
        return get(LVL);
    }
    public Integer getSuccess() {
        return get(SUCCESS);
    }
    public Integer getRecid() {
        return get(RECID);
    }
    public Integer getMp() {
        return get(MP);
    }
    public Integer getExp() {
        return get(EXP);
    }
    public Integer getSp() {
        return get(SP);
    }
    public Integer getDwarven() {
        return get(DWARVEN);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setItem(Integer value) {
        set(ITEM, value);
    }
    public void setFoundation(Integer value) {
        set(FOUNDATION, value);
    }
    public void setQ(Integer value) {
        set(Q, value);
    }
    public void setLvl(Integer value) {
        set(LVL, value);
    }
    public void setSuccess(Integer value) {
        set(SUCCESS, value);
    }
    public void setRecid(Integer value) {
        set(RECID, value);
    }
    public void setMp(Integer value) {
        set(MP, value);
    }
    public void setExp(Integer value) {
        set(EXP, value);
    }
    public void setSp(Integer value) {
        set(SP, value);
    }
    public void setDwarven(Integer value) {
        set(DWARVEN, value);
    }

}
