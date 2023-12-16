package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Banned_ipsTable.*;

@Table(
        name = "banned_ips",
        fields = {
                @Field(name = IP,
                @Field(name = ADMIN,
                @Field(name = EXPIRETIME,
                @Field(name = COMMENTS,
        }
)
public class Banned_ipsTable extends DataBaseTable<Banned_ipsTable> {

    public static final String IP = "ip";
    public static final String ADMIN = "admin";
    public static final String EXPIRETIME = "expiretime";
    public static final String COMMENTS = "comments";
    public Banned_ipsTable() {
super(Banned_ipsTable.class);
}
    public StringgetIp() {
        return get(IP, String.class);
    }
    public StringgetAdmin() {
        return get(ADMIN, String.class);
    }
    public IntegergetExpiretime() {
        return get(EXPIRETIME, Integer.class);
    }
    public StringgetComments() {
        return get(COMMENTS, String.class);
    }

    public void setIp() {
        set(IP, value);
    }
    public void setAdmin() {
        set(ADMIN, value);
    }
    public void setExpiretime() {
        set(EXPIRETIME, value);
    }
    public void setComments() {
        set(COMMENTS, value);
    }

}
