package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ReferralsResource.*;

@Table(
        name = "referrals",
        primary_key = {},
        fields = {
                @Field(name = LOGIN , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = SERVER , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = CHAR , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = BONUS1 , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = BONUS2 , data_type = "varchar" , defValue = @DefValue(String = "")),
        }
)
public class ReferralsResource extends DataBaseTable<ReferralsResource> {

    public static final String LOGIN = "login";
    public static final String SERVER = "server";
    public static final String CHAR = "char";
    public static final String BONUS1 = "bonus1";
    public static final String BONUS2 = "bonus2";

    public ReferralsResource() {
super(ReferralsResource.class);
}

    public String getLogin() {
        return get(LOGIN, String.class);
    }
    public Integer getServer() {
        return get(SERVER, Integer.class);
    }
    public Integer getChar() {
        return get(CHAR, Integer.class);
    }
    public String getBonus1() {
        return get(BONUS1, String.class);
    }
    public String getBonus2() {
        return get(BONUS2, String.class);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setServer(Integer value) {
        set(SERVER, value);
    }
    public void setChar(Integer value) {
        set(CHAR, value);
    }
    public void setBonus1(String value) {
        set(BONUS1, value);
    }
    public void setBonus2(String value) {
        set(BONUS2, value);
    }

}
