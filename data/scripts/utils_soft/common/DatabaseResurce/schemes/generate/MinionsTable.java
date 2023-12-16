package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MinionsTable.*;

@Table(
        name = "minions",
        fields = {
                @Field(name = BOSS_ID),
                @Field(name = MINION_ID),
                @Field(name = AMOUNT),
        }
)
public class MinionsTable extends DataBaseTable<MinionsTable> {

    public static final String BOSS_ID = "boss_id";
    public static final String MINION_ID = "minion_id";
    public static final String AMOUNT = "amount";

    public MinionsTable() {
super(MinionsTable.class);
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