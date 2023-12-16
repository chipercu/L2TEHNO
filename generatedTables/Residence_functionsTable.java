package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Residence_functionsTable.*;

@Table(
        name = "residence_functions",
        fields = {
                @Field(name = ID,
                @Field(name = TYPE,
                @Field(name = LVL,
                @Field(name = LEASE,
                @Field(name = RATE,
                @Field(name = ENDTIME,
                @Field(name = INDEBT,
        }
)
public class Residence_functionsTable extends DataBaseTable<Residence_functionsTable> {

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String LVL = "lvl";
    public static final String LEASE = "lease";
    public static final String RATE = "rate";
    public static final String ENDTIME = "endTime";
    public static final String INDEBT = "inDebt";
    public Residence_functionsTable() {
super(Residence_functionsTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }
    public IntegergetLvl() {
        return get(LVL, Integer.class);
    }
    public IntegergetLease() {
        return get(LEASE, Integer.class);
    }
    public ObjectgetRate() {
        return get(RATE, Object.class);
    }
    public IntegergetEndTime() {
        return get(ENDTIME, Integer.class);
    }
    public IntegergetInDebt() {
        return get(INDEBT, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setLvl() {
        set(LVL, value);
    }
    public void setLease() {
        set(LEASE, value);
    }
    public void setRate() {
        set(RATE, value);
    }
    public void setEndTime() {
        set(ENDTIME, value);
    }
    public void setInDebt() {
        set(INDEBT, value);
    }

}
