package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanhallTable.*;

@Table(
        name = "clanhall",
        fields = {
                @Field(name = ID),
                @Field(name = NAME),
                @Field(name = OWNER_ID),
                @Field(name = LEASE),
                @Field(name = DESC),
                @Field(name = LOCATION),
                @Field(name = PAID_UNTIL),
                @Field(name = GRADE),
                @Field(name = PRICE),
                @Field(name = DEPOSIT),
                @Field(name = IN_DEBT),
                @Field(name = SKILLS),
                @Field(name = SIEGE_DATE),
                @Field(name = SIEGE_DAY_OF_WEEK),
                @Field(name = SIEGE_HOUR_OF_DAY),
        }
)
public class ClanhallTable extends DataBaseTable<ClanhallTable> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String OWNER_ID = "ownerId";
    public static final String LEASE = "lease";
    public static final String DESC = "desc";
    public static final String LOCATION = "location";
    public static final String PAID_UNTIL = "paidUntil";
    public static final String GRADE = "Grade";
    public static final String PRICE = "price";
    public static final String DEPOSIT = "deposit";
    public static final String IN_DEBT = "inDebt";
    public static final String SKILLS = "skills";
    public static final String SIEGE_DATE = "siegeDate";
    public static final String SIEGE_DAY_OF_WEEK = "siegeDayOfWeek";
    public static final String SIEGE_HOUR_OF_DAY = "siegeHourOfDay";

    public ClanhallTable() {
super(ClanhallTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getOwnerId() {
        return get(OWNER_ID, Integer.class);
    }
    public Integer getLease() {
        return get(LEASE, Integer.class);
    }
    public String getDesc() {
        return get(DESC, String.class);
    }
    public String getLocation() {
        return get(LOCATION, String.class);
    }
    public Long getPaidUntil() {
        return get(PAID_UNTIL, Long.class);
    }
    public Integer getGrade() {
        return get(GRADE, Integer.class);
    }
    public Long getPrice() {
        return get(PRICE, Long.class);
    }
    public Integer getDeposit() {
        return get(DEPOSIT, Integer.class);
    }
    public Integer getInDebt() {
        return get(IN_DEBT, Integer.class);
    }
    public String getSkills() {
        return get(SKILLS, String.class);
    }
    public Integer getSiegeDate() {
        return get(SIEGE_DATE, Integer.class);
    }
    public Integer getSiegeDayOfWeek() {
        return get(SIEGE_DAY_OF_WEEK, Integer.class);
    }
    public Integer getSiegeHourOfDay() {
        return get(SIEGE_HOUR_OF_DAY, Integer.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setOwnerId(Integer value) {
        set(OWNER_ID, value);
    }
    public void setLease(Integer value) {
        set(LEASE, value);
    }
    public void setDesc(String value) {
        set(DESC, value);
    }
    public void setLocation(String value) {
        set(LOCATION, value);
    }
    public void setPaidUntil(Long value) {
        set(PAID_UNTIL, value);
    }
    public void setGrade(Integer value) {
        set(GRADE, value);
    }
    public void setPrice(Long value) {
        set(PRICE, value);
    }
    public void setDeposit(Integer value) {
        set(DEPOSIT, value);
    }
    public void setInDebt(Integer value) {
        set(IN_DEBT, value);
    }
    public void setSkills(String value) {
        set(SKILLS, value);
    }
    public void setSiegeDate(Integer value) {
        set(SIEGE_DATE, value);
    }
    public void setSiegeDayOfWeek(Integer value) {
        set(SIEGE_DAY_OF_WEEK, value);
    }
    public void setSiegeHourOfDay(Integer value) {
        set(SIEGE_HOUR_OF_DAY, value);
    }

}