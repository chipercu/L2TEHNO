package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.MinionsResource.*;

@Table(
        name = "minions",
        primary_key = {BOSS_ID,MINION_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = BOSS_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MINION_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = AMOUNT , defValue = "0"),
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
