package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MinionsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "minions",
        primary_key = {BOSS_ID,MINION_ID},
        fields = {
                @Field(name = BOSS_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MINION_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = AMOUNT , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class MinionsResource extends DataBaseTable<MinionsResource> {

    public static final String BOSS_ID = "boss_id";
    public static final String MINION_ID = "minion_id";
    public static final String AMOUNT = "amount";

    public MinionsResource() {
super(MinionsResource.class);
}


    public MinionsResource(String boss_id,String minion_id){
        super(MinionsResource.class);
        getSTAT_SET().set(BOSS_ID, boss_id);
        getSTAT_SET().set(MINION_ID, minion_id);
    }

    public Integer getBossId() {
        return get(BOSS_ID, Integer.class);
    }
    public Integer getMinionId() {
        return get(MINION_ID, Integer.class);
    }
    public Integer getAmount() {
        return get(AMOUNT, Integer.class);
    }

    public void setBossId(Integer value) {
        set(BOSS_ID, value);
    }
    public void setMinionId(Integer value) {
        set(MINION_ID, value);
    }
    public void setAmount(Integer value) {
        set(AMOUNT, value);
    }

}
