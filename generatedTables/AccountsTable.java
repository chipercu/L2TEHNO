package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.AccountsTable.*;

@Table(
        name = "accounts",
        fields = {
                @Field(name = LOGIN,
                @Field(name = PASSWORD,
                @Field(name = LASTACTIVE,
                @Field(name = ACCESS_LEVEL,
                @Field(name = LASTIP,
                @Field(name = LASTSERVER,
                @Field(name = COMMENTS,
                @Field(name = EMAIL,
                @Field(name = PAY_STAT,
                @Field(name = BONUS,
                @Field(name = BONUS_EXPIRE,
                @Field(name = BANEXPIRES,
                @Field(name = ALLOWIPS,
                @Field(name = POINTS,
                @Field(name = LOCK_EXPIRE,
                @Field(name = ACTIVATED,
                @Field(name = LAST_HWID,
        }
)
public class AccountsTable extends DataBaseTable<AccountsTable> {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String LASTACTIVE = "lastactive";
    public static final String ACCESS_LEVEL = "access_level";
    public static final String LASTIP = "lastIP";
    public static final String LASTSERVER = "lastServer";
    public static final String COMMENTS = "comments";
    public static final String EMAIL = "email";
    public static final String PAY_STAT = "pay_stat";
    public static final String BONUS = "bonus";
    public static final String BONUS_EXPIRE = "bonus_expire";
    public static final String BANEXPIRES = "banExpires";
    public static final String ALLOWIPS = "AllowIPs";
    public static final String POINTS = "points";
    public static final String LOCK_EXPIRE = "lock_expire";
    public static final String ACTIVATED = "activated";
    public static final String LAST_HWID = "last_hwid";
    public AccountsTable() {
super(AccountsTable.class);
}
    public StringgetLogin() {
        return get(LOGIN, String.class);
    }
    public StringgetPassword() {
        return get(PASSWORD, String.class);
    }
    public IntegergetLastactive() {
        return get(LASTACTIVE, Integer.class);
    }
    public IntegergetAccess_level() {
        return get(ACCESS_LEVEL, Integer.class);
    }
    public StringgetLastIP() {
        return get(LASTIP, String.class);
    }
    public IntegergetLastServer() {
        return get(LASTSERVER, Integer.class);
    }
    public StringgetComments() {
        return get(COMMENTS, String.class);
    }
    public StringgetEmail() {
        return get(EMAIL, String.class);
    }
    public IntegergetPay_stat() {
        return get(PAY_STAT, Integer.class);
    }
    public ObjectgetBonus() {
        return get(BONUS, Object.class);
    }
    public IntegergetBonus_expire() {
        return get(BONUS_EXPIRE, Integer.class);
    }
    public IntegergetBanExpires() {
        return get(BANEXPIRES, Integer.class);
    }
    public StringgetAllowIPs() {
        return get(ALLOWIPS, String.class);
    }
    public IntegergetPoints() {
        return get(POINTS, Integer.class);
    }
    public IntegergetLock_expire() {
        return get(LOCK_EXPIRE, Integer.class);
    }
    public IntegergetActivated() {
        return get(ACTIVATED, Integer.class);
    }
    public StringgetLast_hwid() {
        return get(LAST_HWID, String.class);
    }

    public void setLogin() {
        set(LOGIN, value);
    }
    public void setPassword() {
        set(PASSWORD, value);
    }
    public void setLastactive() {
        set(LASTACTIVE, value);
    }
    public void setAccess_level() {
        set(ACCESS_LEVEL, value);
    }
    public void setLastIP() {
        set(LASTIP, value);
    }
    public void setLastServer() {
        set(LASTSERVER, value);
    }
    public void setComments() {
        set(COMMENTS, value);
    }
    public void setEmail() {
        set(EMAIL, value);
    }
    public void setPay_stat() {
        set(PAY_STAT, value);
    }
    public void setBonus() {
        set(BONUS, value);
    }
    public void setBonus_expire() {
        set(BONUS_EXPIRE, value);
    }
    public void setBanExpires() {
        set(BANEXPIRES, value);
    }
    public void setAllowIPs() {
        set(ALLOWIPS, value);
    }
    public void setPoints() {
        set(POINTS, value);
    }
    public void setLock_expire() {
        set(LOCK_EXPIRE, value);
    }
    public void setActivated() {
        set(ACTIVATED, value);
    }
    public void setLast_hwid() {
        set(LAST_HWID, value);
    }

}
