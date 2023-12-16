package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.ClanhallTable.*;

@Table(
        name = "clanhall",
        fields = {
                @Field(name = ID,
                @Field(name = NAME,
                @Field(name = OWNERID,
                @Field(name = LEASE,
                @Field(name = DESC,
                @Field(name = LOCATION,
                @Field(name = PAIDUNTIL,
                @Field(name = GRADE,
                @Field(name = PRICE,
                @Field(name = DEPOSIT,
                @Field(name = INDEBT,
                @Field(name = SKILLS,
                @Field(name = SIEGEDATE,
                @Field(name = SIEGEDAYOFWEEK,
                @Field(name = SIEGEHOUROFDAY,
        }
)
public class ClanhallTable extends DataBaseTable<ClanhallTable> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String OWNERID = "ownerId";
    public static final String LEASE = "lease";
    public static final String DESC = "desc";
    public static final String LOCATION = "location";
    public static final String PAIDUNTIL = "paidUntil";
    public static final String GRADE = "Grade";
    public static final String PRICE = "price";
    public static final String DEPOSIT = "deposit";
    public static final String INDEBT = "inDebt";
    public static final String SKILLS = "skills";
    public static final String SIEGEDATE = "siegeDate";
    public static final String SIEGEDAYOFWEEK = "siegeDayOfWeek";
    public static final String SIEGEHOUROFDAY = "siegeHourOfDay";
    public ClanhallTable() {
super(ClanhallTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetOwnerId() {
        return get(OWNERID, Integer.class);
    }
    public IntegergetLease() {
        return get(LEASE, Integer.class);
    }
    public StringgetDesc() {
        return get(DESC, String.class);
    }
    public StringgetLocation() {
        return get(LOCATION, String.class);
    }
    public ObjectgetPaidUntil() {
        return get(PAIDUNTIL, Object.class);
    }
    public IntegergetGrade() {
        return get(GRADE, Integer.class);
    }
    public ObjectgetPrice() {
        return get(PRICE, Object.class);
    }
    public IntegergetDeposit() {
        return get(DEPOSIT, Integer.class);
    }
    public IntegergetInDebt() {
        return get(INDEBT, Integer.class);
    }
    public StringgetSkills() {
        return get(SKILLS, String.class);
    }
    public IntegergetSiegeDate() {
        return get(SIEGEDATE, Integer.class);
    }
    public IntegergetSiegeDayOfWeek() {
        return get(SIEGEDAYOFWEEK, Integer.class);
    }
    public IntegergetSiegeHourOfDay() {
        return get(SIEGEHOUROFDAY, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setOwnerId() {
        set(OWNERID, value);
    }
    public void setLease() {
        set(LEASE, value);
    }
    public void setDesc() {
        set(DESC, value);
    }
    public void setLocation() {
        set(LOCATION, value);
    }
    public void setPaidUntil() {
        set(PAIDUNTIL, value);
    }
    public void setGrade() {
        set(GRADE, value);
    }
    public void setPrice() {
        set(PRICE, value);
    }
    public void setDeposit() {
        set(DEPOSIT, value);
    }
    public void setInDebt() {
        set(INDEBT, value);
    }
    public void setSkills() {
        set(SKILLS, value);
    }
    public void setSiegeDate() {
        set(SIEGEDATE, value);
    }
    public void setSiegeDayOfWeek() {
        set(SIEGEDAYOFWEEK, value);
    }
    public void setSiegeHourOfDay() {
        set(SIEGEHOUROFDAY, value);
    }

}
