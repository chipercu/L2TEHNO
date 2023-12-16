package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.DdosIpsTable.*;

@Table(
        name = "ddos_ips",
        fields = {
                @Field(name = IP),
                @Field(name = ADMIN),
                @Field(name = EXPIRETIME),
                @Field(name = COMMENTS),
        }
)
public class DdosIpsTable extends DataBaseTable<DdosIpsTable> {

    public static final String IP = "ip";
    public static final String ADMIN = "admin";
    public static final String EXPIRETIME = "expiretime";
    public static final String COMMENTS = "comments";

    public DdosIpsTable() {
super(DdosIpsTable.class);
}

    public String getIp() {
        return get(IP, String.class);
    }
    public String getAdmin() {
        return get(ADMIN, String.class);
    }
    public Integer getExpiretime() {
        return get(EXPIRETIME, Integer.class);
    }
    public String getComments() {
        return get(COMMENTS, String.class);
    }

    public void setIp(String value) {
        set(IP, value);
    }
    public void setAdmin(String value) {
        set(ADMIN, value);
    }
    public void setExpiretime(Integer value) {
        set(EXPIRETIME, value);
    }
    public void setComments(String value) {
        set(COMMENTS, value);
    }

}
