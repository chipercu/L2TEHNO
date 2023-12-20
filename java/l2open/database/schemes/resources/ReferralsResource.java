package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ReferralsResource.*;

@Table(
        name = "referrals",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = LOGIN , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SERVER , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = _CHAR , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = BONUS1 , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = BONUS2 , defValue = ""),
        }
)
public class ReferralsResource extends DataBaseTable<ReferralsResource> {

    public static final String LOGIN = "login";
    public static final String SERVER = "server";
    public static final String _CHAR = "char";
    public static final String BONUS1 = "bonus1";
    public static final String BONUS2 = "bonus2";

    public ReferralsResource() {
        super(ReferralsResource.class);
}

    public String getLogin() {
        return get(LOGIN);
    }
    public Integer getServer() {
        return get(SERVER);
    }
    public Integer getChar() {
        return get(_CHAR);
    }
    public String getBonus1() {
        return get(BONUS1);
    }
    public String getBonus2() {
        return get(BONUS2);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setServer(Integer value) {
        set(SERVER, value);
    }
    public void setChar(Integer value) {
        set(_CHAR, value);
    }
    public void setBonus1(String value) {
        set(BONUS1, value);
    }
    public void setBonus2(String value) {
        set(BONUS2, value);
    }

}
