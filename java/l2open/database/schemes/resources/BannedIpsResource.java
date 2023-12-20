package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.BannedIpsResource.*;

@Table(
        name = "banned_ips",
        primary_key = {IP},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 15 , name = IP , defValue = "null"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 45 , name = ADMIN , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = EXPIRETIME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 255 , name = COMMENTS , defValue = "NULL"),
        }
)
public class BannedIpsResource extends DataBaseTable<BannedIpsResource> {

    public static final String IP = "ip";
    public static final String ADMIN = "admin";
    public static final String EXPIRETIME = "expiretime";
    public static final String COMMENTS = "comments";

    public BannedIpsResource() {
        super(BannedIpsResource.class);
}

    public String getIp() {
        return get(IP);
    }
    public String getAdmin() {
        return get(ADMIN);
    }
    public Integer getExpiretime() {
        return get(EXPIRETIME);
    }
    public String getComments() {
        return get(COMMENTS);
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
