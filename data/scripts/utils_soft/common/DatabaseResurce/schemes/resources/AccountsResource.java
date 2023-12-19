package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.AccountsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "accounts",
        primary_key = {LOGIN},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = LOGIN , defValue = ""),
                @Column(is_null = YES , type = VARCHAR , type_size = 256 , name = PASSWORD , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LASTACTIVE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ACCESS_LEVEL , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 15 , name = LAST_IP , defValue = ""),
                @Column(is_null = YES , type = INT , type_size = 0 , name = LAST_SERVER , defValue = "1"),
                @Column(is_null = YES , type = TEXT , type_size = 65535 , name = COMMENTS , defValue = "NULL"),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = EMAIL , defValue = "null@null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = PAY_STAT , defValue = "1"),
                @Column(is_null = NO , type = FLOAT , type_size = 0 , name = BONUS , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BONUS_EXPIRE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BAN_EXPIRES , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 256 , name = ALLOW_IPS , defValue = "*"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POINTS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOCK_EXPIRE , defValue = "604800"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ACTIVATED , defValue = "1"),
                @Column(is_null = YES , type = VARCHAR , type_size = 50 , name = LAST_HWID , defValue = ""),
        }
)
public class AccountsResource extends DataBaseTable<AccountsResource> {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String LASTACTIVE = "lastactive";
    public static final String ACCESS_LEVEL = "access_level";
    public static final String LAST_IP = "lastIP";
    public static final String LAST_SERVER = "lastServer";
    public static final String COMMENTS = "comments";
    public static final String EMAIL = "email";
    public static final String PAY_STAT = "pay_stat";
    public static final String BONUS = "bonus";
    public static final String BONUS_EXPIRE = "bonus_expire";
    public static final String BAN_EXPIRES = "banExpires";
    public static final String ALLOW_IPS = "AllowIPs";
    public static final String POINTS = "points";
    public static final String LOCK_EXPIRE = "lock_expire";
    public static final String ACTIVATED = "activated";
    public static final String LAST_HWID = "last_hwid";

    public AccountsResource() {
        super(AccountsResource.class);
}

    public String getLogin() {
        return get(LOGIN);
    }
    public String getPassword() {
        return get(PASSWORD);
    }
    public Integer getLastactive() {
        return get(LASTACTIVE);
    }
    public Integer getAccessLevel() {
        return get(ACCESS_LEVEL);
    }
    public String getLastIP() {
        return get(LAST_IP);
    }
    public Integer getLastServer() {
        return get(LAST_SERVER);
    }
    public String getComments() {
        return get(COMMENTS);
    }
    public String getEmail() {
        return get(EMAIL);
    }
    public Integer getPayStat() {
        return get(PAY_STAT);
    }
    public Double getBonus() {
        return get(BONUS);
    }
    public Integer getBonusExpire() {
        return get(BONUS_EXPIRE);
    }
    public Integer getBanExpires() {
        return get(BAN_EXPIRES);
    }
    public String getAllowIPs() {
        return get(ALLOW_IPS);
    }
    public Integer getPoints() {
        return get(POINTS);
    }
    public Integer getLockExpire() {
        return get(LOCK_EXPIRE);
    }
    public Integer getActivated() {
        return get(ACTIVATED);
    }
    public String getLastHwid() {
        return get(LAST_HWID);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setPassword(String value) {
        set(PASSWORD, value);
    }
    public void setLastactive(Integer value) {
        set(LASTACTIVE, value);
    }
    public void setAccessLevel(Integer value) {
        set(ACCESS_LEVEL, value);
    }
    public void setLastIP(String value) {
        set(LAST_IP, value);
    }
    public void setLastServer(Integer value) {
        set(LAST_SERVER, value);
    }
    public void setComments(String value) {
        set(COMMENTS, value);
    }
    public void setEmail(String value) {
        set(EMAIL, value);
    }
    public void setPayStat(Integer value) {
        set(PAY_STAT, value);
    }
    public void setBonus(Double value) {
        set(BONUS, value);
    }
    public void setBonusExpire(Integer value) {
        set(BONUS_EXPIRE, value);
    }
    public void setBanExpires(Integer value) {
        set(BAN_EXPIRES, value);
    }
    public void setAllowIPs(String value) {
        set(ALLOW_IPS, value);
    }
    public void setPoints(Integer value) {
        set(POINTS, value);
    }
    public void setLockExpire(Integer value) {
        set(LOCK_EXPIRE, value);
    }
    public void setActivated(Integer value) {
        set(ACTIVATED, value);
    }
    public void setLastHwid(String value) {
        set(LAST_HWID, value);
    }

}
