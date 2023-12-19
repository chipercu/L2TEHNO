package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.ClanhallResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clanhall",
        primary_key = {ID,NAME},
        fields = {
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 40 , name = NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OWNER_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEASE , defValue = "0"),
                @Column(is_null = NO , type = TEXT , type_size = 65535 , name = DESC , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 15 , name = LOCATION , defValue = ""),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = PAID_UNTIL , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = GRADE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = PRICE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DEPOSIT , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = IN_DEBT , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 32 , name = SKILLS , defValue = "0;0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SIEGE_DATE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SIEGE_DAY_OF_WEEK , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SIEGE_HOUR_OF_DAY , defValue = "0"),
        }
)
public class ClanhallResource extends DataBaseTable<ClanhallResource> {

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

    public ClanhallResource() {
        super(ClanhallResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getOwnerId() {
        return get(OWNER_ID);
    }
    public Integer getLease() {
        return get(LEASE);
    }
    public String getDesc() {
        return get(DESC);
    }
    public String getLocation() {
        return get(LOCATION);
    }
    public Long getPaidUntil() {
        return get(PAID_UNTIL);
    }
    public Integer getGrade() {
        return get(GRADE);
    }
    public Long getPrice() {
        return get(PRICE);
    }
    public Integer getDeposit() {
        return get(DEPOSIT);
    }
    public Integer getInDebt() {
        return get(IN_DEBT);
    }
    public String getSkills() {
        return get(SKILLS);
    }
    public Integer getSiegeDate() {
        return get(SIEGE_DATE);
    }
    public Integer getSiegeDayOfWeek() {
        return get(SIEGE_DAY_OF_WEEK);
    }
    public Integer getSiegeHourOfDay() {
        return get(SIEGE_HOUR_OF_DAY);
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
