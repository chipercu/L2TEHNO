package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ResidenceFunctionsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "residence_functions",
        primary_key = {ID,TYPE},
        fields = {
                @Field(name = ID , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LVL , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LEASE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = RATE , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = END_TIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = IN_DEBT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public ResidenceFunctionsResource(String id,String type){
        super(ResidenceFunctionsResource.class);
        getSTAT_SET().set(ID, id);
        getSTAT_SET().set(TYPE, type);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }
    public Integer getLvl() {
        return get(LVL, Integer.class);
    }
    public Integer getLease() {
        return get(LEASE, Integer.class);
    }
    public Long getRate() {
        return get(RATE, Long.class);
    }
    public Integer getEndTime() {
        return get(END_TIME, Integer.class);
    }
    public Integer getInDebt() {
        return get(IN_DEBT, Integer.class);
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
