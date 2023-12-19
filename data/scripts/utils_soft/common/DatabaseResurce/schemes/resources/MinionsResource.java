package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.MinionsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "minions",
        primary_key = {BOSS_ID,MINION_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = BOSS_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MINION_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AMOUNT , defValue = "0"),
        }
)
public class MinionsResource extends DataBaseTable<MinionsResource> {

    public static final String BOSS_ID = "boss_id";
    public static final String MINION_ID = "minion_id";
    public static final String AMOUNT = "amount";

    public MinionsResource() {
        super(MinionsResource.class);
}

    public Integer getBossId() {
        return get(BOSS_ID);
    }
    public Integer getMinionId() {
        return get(MINION_ID);
    }
    public Integer getAmount() {
        return get(AMOUNT);
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
