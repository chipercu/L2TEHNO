package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AccountsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "accounts",
        primary_key = {LOGIN},
        fields = {
                @Field(name = LOGIN , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = PASSWORD , type = VARCHAR , type_size = 256 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = LASTACTIVE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ACCESS_LEVEL , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LAST_IP , type = VARCHAR , type_size = 15 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = LAST_SERVER , type = INT , nullable = true , defValue = @DefValue(Integer = 1)),
                @Field(name = COMMENTS , type = TEXT , type_size = 65535 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = EMAIL , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "null@null")),
                @Field(name = PAY_STAT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = BONUS , type = FLOAT , nullable = false , defValue = @DefValue(Double = 1)),
                @Field(name = BONUS_EXPIRE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = BAN_EXPIRES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ALLOW_IPS , type = VARCHAR , type_size = 256 , nullable = false , defValue = @DefValue(String = "*")),
                @Field(name = POINTS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LOCK_EXPIRE , type = INT , nullable = false , defValue = @DefValue(Integer = 604800)),
                @Field(name = ACTIVATED , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = LAST_HWID , type = VARCHAR , type_size = 50 , nullable = true , defValue = @DefValue(String = "")),
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


    public AccountsResource(String login){
        super(AccountsResource.class);
        getSTAT_SET().set(LOGIN, login);
    }

    public String getLogin() {
        return get(LOGIN, String.class);
    }
    public String getPassword() {
        return get(PASSWORD, String.class);
    }
    public Integer getLastactive() {
        return get(LASTACTIVE, Integer.class);
    }
    public Integer getAccessLevel() {
        return get(ACCESS_LEVEL, Integer.class);
    }
    public String getLastIP() {
        return get(LAST_IP, String.class);
    }
    public Integer getLastServer() {
        return get(LAST_SERVER, Integer.class);
    }
    public String getComments() {
        return get(COMMENTS, String.class);
    }
    public String getEmail() {
        return get(EMAIL, String.class);
    }
    public Integer getPayStat() {
        return get(PAY_STAT, Integer.class);
    }
    public Double getBonus() {
        return get(BONUS, Double.class);
    }
    public Integer getBonusExpire() {
        return get(BONUS_EXPIRE, Integer.class);
    }
    public Integer getBanExpires() {
        return get(BAN_EXPIRES, Integer.class);
    }
    public String getAllowIPs() {
        return get(ALLOW_IPS, String.class);
    }
    public Integer getPoints() {
        return get(POINTS, Integer.class);
    }
    public Integer getLockExpire() {
        return get(LOCK_EXPIRE, Integer.class);
    }
    public Integer getActivated() {
        return get(ACTIVATED, Integer.class);
    }
    public String getLastHwid() {
        return get(LAST_HWID, String.class);
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
