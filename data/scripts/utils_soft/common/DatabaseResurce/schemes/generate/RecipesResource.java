package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RecipesResource.*;

@Table(
        name = "recipes",
        primary_key = {ID},
        fields = {
                @Field(name = ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , data_type = "varchar"),
                @Field(name = ITEM , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = FOUNDATION , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = Q , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LVL , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = SUCCESS , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = RECID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = MP , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = EXP , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = SP , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = DWARVEN , data_type = "int" , defValue = @DefValue(Integer = 1)),
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
        return get(ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getItem() {
        return get(ITEM, Integer.class);
    }
    public Integer getFoundation() {
        return get(FOUNDATION, Integer.class);
    }
    public Integer getQ() {
        return get(Q, Integer.class);
    }
    public Integer getLvl() {
        return get(LVL, Integer.class);
    }
    public Integer getSuccess() {
        return get(SUCCESS, Integer.class);
    }
    public Integer getRecid() {
        return get(RECID, Integer.class);
    }
    public Integer getMp() {
        return get(MP, Integer.class);
    }
    public Integer getExp() {
        return get(EXP, Integer.class);
    }
    public Integer getSp() {
        return get(SP, Integer.class);
    }
    public Integer getDwarven() {
        return get(DWARVEN, Integer.class);
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
