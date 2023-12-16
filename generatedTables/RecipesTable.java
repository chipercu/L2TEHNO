package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.RecipesTable.*;

@Table(
        name = "recipes",
        fields = {
                @Field(name = ID,
                @Field(name = NAME,
                @Field(name = ITEM,
                @Field(name = FOUNDATION,
                @Field(name = Q,
                @Field(name = LVL,
                @Field(name = SUCCESS,
                @Field(name = RECID,
                @Field(name = MP,
                @Field(name = EXP,
                @Field(name = SP,
                @Field(name = DWARVEN,
        }
)
public class RecipesTable extends DataBaseTable<RecipesTable> {

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
    public RecipesTable() {
super(RecipesTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetItem() {
        return get(ITEM, Integer.class);
    }
    public IntegergetFoundation() {
        return get(FOUNDATION, Integer.class);
    }
    public IntegergetQ() {
        return get(Q, Integer.class);
    }
    public IntegergetLvl() {
        return get(LVL, Integer.class);
    }
    public IntegergetSuccess() {
        return get(SUCCESS, Integer.class);
    }
    public IntegergetRecid() {
        return get(RECID, Integer.class);
    }
    public IntegergetMp() {
        return get(MP, Integer.class);
    }
    public IntegergetExp() {
        return get(EXP, Integer.class);
    }
    public IntegergetSp() {
        return get(SP, Integer.class);
    }
    public IntegergetDwarven() {
        return get(DWARVEN, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setItem() {
        set(ITEM, value);
    }
    public void setFoundation() {
        set(FOUNDATION, value);
    }
    public void setQ() {
        set(Q, value);
    }
    public void setLvl() {
        set(LVL, value);
    }
    public void setSuccess() {
        set(SUCCESS, value);
    }
    public void setRecid() {
        set(RECID, value);
    }
    public void setMp() {
        set(MP, value);
    }
    public void setExp() {
        set(EXP, value);
    }
    public void setSp() {
        set(SP, value);
    }
    public void setDwarven() {
        set(DWARVEN, value);
    }

}
