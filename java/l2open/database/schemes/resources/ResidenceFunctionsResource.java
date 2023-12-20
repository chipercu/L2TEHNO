package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ResidenceFunctionsResource.*;

@Table(
        name = "residence_functions",
        primary_key = {ID,TYPE},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = TYPE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = LVL , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEASE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = RATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = END_TIME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = IN_DEBT , defValue = "0"),
        }
)
public class ResidenceFunctionsResource extends DataBaseTable<ResidenceFunctionsResource> {

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String LVL = "lvl";
    public static final String LEASE = "lease";
    public static final String RATE = "rate";
    public static final String END_TIME = "endTime";
    public static final String IN_DEBT = "inDebt";

    public ResidenceFunctionsResource() {
        super(ResidenceFunctionsResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getType() {
        return get(TYPE);
    }
    public Integer getLvl() {
        return get(LVL);
    }
    public Integer getLease() {
        return get(LEASE);
    }
    public Long getRate() {
        return get(RATE);
    }
    public Integer getEndTime() {
        return get(END_TIME);
    }
    public Integer getInDebt() {
        return get(IN_DEBT);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setLvl(Integer value) {
        set(LVL, value);
    }
    public void setLease(Integer value) {
        set(LEASE, value);
    }
    public void setRate(Long value) {
        set(RATE, value);
    }
    public void setEndTime(Integer value) {
        set(END_TIME, value);
    }
    public void setInDebt(Integer value) {
        set(IN_DEBT, value);
    }

}
